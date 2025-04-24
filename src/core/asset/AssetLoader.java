package core.asset;

import game.entities.GameObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.animation.Animation;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AssetLoader {
    private static final long MAX_UNUSED_TIME_MS = 5 * 60 * 1000;
    private static final int CACHE_CLEAN_INTERVAL_MS = 1000;
    private static final AssetLoader instance = new AssetLoader();
    private final Map<String, CachedAsset> cache = new HashMap<>();

    private AssetLoader() {
        Thread cleanerThread = new Thread(this::cleanCache);
        //set this to daemon (a low priority background thread that doesn't interfere with program shutting down
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }

    public static AssetLoader getInstance(){
        return instance;
    }

    public BufferedImage getSubImage(String path, int x, int y, int w, int h){
        path = Assets.getAssetPath(path);
        String subImagePath = path+x+y+w+h;
        synchronized (cache) {
            if(cache.containsKey(subImagePath)){
                CachedImage cachedImage = (CachedImage) cache.get(subImagePath);
                cachedImage.updateLastAccessTime();
                return cachedImage.image;
            }
        }
        BufferedImage image = loadSubImage(path,x,y,w,h);
        if(image != null){
            synchronized (cache) {
                cache.put(subImagePath, new CachedImage(image));
            }
        }
        return image;
    }
    public BufferedImage loadSubImage(String path, int x, int y, int w, int h){
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }
            return ImageIO.read(input).getSubimage(x,y,w,h);
        } catch (Exception e){
            System.err.println("[AssetLoader] Failed to load asset: " + path);
            return null;
        }
    }

    public BufferedImage getImage(String path){
        path = Assets.getAssetPath(path);
        synchronized (cache) {
            if(cache.containsKey(path)){
                CachedImage cachedImage = (CachedImage) cache.get(path);
                cachedImage.updateLastAccessTime();
                return cachedImage.image;
            }
        }

        BufferedImage image = loadImage(path);
        if(image != null){
            synchronized (cache) {
                cache.put(path, new CachedImage(image));
            }
        }
        return image;
    }
    private BufferedImage loadImage(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }
            return ImageIO.read(input);
        } catch (Exception e){
            System.err.println("[AssetLoader] Failed to load asset: " + path);
            return null;
        }
    }

    public Clip getSFXClip(String path) {
        path = Assets.getAssetPath(path);

        synchronized (cache) {
            if (cache.containsKey(path)) {
                CachedSFXClip cached = (CachedSFXClip) cache.get(path);
                cached.updateLastAccessTime();
                return cached.getNextClip();
            }
        }

        CachedSFXClip newCached = loadSFXClip(path);
        if (newCached != null) {
            synchronized (cache) {
                cache.put(path, newCached);
            }
            return newCached.getNextClip();
        }
        return null;
    }

    private CachedSFXClip loadSFXClip(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(input);
            AudioFormat format = audioStream.getFormat();
            byte[] audioData = audioStream.readAllBytes();

            List<Clip> clipPool = new ArrayList<>();
            for (int i = 0; i < 4; i++) {  // load a small pool of 4 copies
                Clip clip = AudioSystem.getClip();
                clip.open(format, audioData, 0, audioData.length);
                clipPool.add(clip);
            }

            return new CachedSFXClip(clipPool);

        } catch (Exception e) {
            System.err.println("[AssetLoader] Failed to load and cache SFX: " + path);
            return null;
        }
    }


    public Animation getAnimation(String path){
        if(path.isEmpty()){return null;}

        path = Assets.getAssetPath(path);

        synchronized (cache) {
            if(cache.containsKey(path)){
                CachedAnimation cachedAnimation = (CachedAnimation) cache.get(path);
                cachedAnimation.updateLastAccessTime();
                return cachedAnimation.animation;
            }
        }
        Animation animation = loadAnimation(path);
        if(animation != null){
            synchronized (cache) {
                cache.put(path, new CachedAnimation(animation));
            }
        }
        return animation;
    }
    private Animation loadAnimation(String path){
        try(InputStream input = getClass().getResourceAsStream(path)){
            if(input == null){
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(input, Animation.class);
        } catch (Exception e){
            System.err.println("[AssetLoader] Failed to load asset: " + path);
            return null;
        }
    }

    public GameObject getPrefab(String path) {
        InputStream cachedPrefabFile = getInputStream(path);
        GameObject prefab = PrefabReader.getObject(cachedPrefabFile);
        if (prefab == null) {
            System.err.println("[AssetLoader] Failed to parse prefab: " + path);
            return null;
        }
        prefab.initialize();
        return prefab;
    }
    public InputStream getInputStream(String path){
        path = Assets.getAssetPath(path);
        CachedInputStream cachedinputStream;
        synchronized (cache) {
            if (cache.containsKey(path)) {
                cachedinputStream = (CachedInputStream) cache.get(path);
                cachedinputStream.updateLastAccessTime();
            } else {
                cachedinputStream = loadAndCacheInputStream(path);
                if (cachedinputStream == null) {
                    return null;
                }
            }
        }
        if (cachedinputStream == null) {
            System.err.println("[AssetLoader] Failed to get inputStream: " + path);
            return null;
        }
        return cachedinputStream.getNewInputStream();
    }
    private CachedInputStream loadAndCacheInputStream(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }
            CachedInputStream cachedInputStream = new CachedInputStream(input);
            cache.put(path, cachedInputStream);
            System.out.println("[AssetLoader] Loaded input stream: " + path);
            return cachedInputStream;
        } catch (Exception e) {
            System.err.println("[AssetLoader] Failed to load asset: " + path);
            e.printStackTrace();
            return null;
        }
    }


    private void cleanCache(){
        while(true){
            synchronized (cache) {
                Iterator<Map.Entry<String, CachedAsset>> iterator = cache.entrySet().iterator();
                long currentTime = System.currentTimeMillis();
                while (iterator.hasNext()) {
                    Map.Entry<String, CachedAsset> entry = iterator.next();
                    if (currentTime - entry.getValue().getLastAccessTime() > MAX_UNUSED_TIME_MS) {
                        System.out.println("[AssetLoader] Removing unused asset: " + entry.getKey());
                        iterator.remove();
                    }
                }
            }
            try {
                Thread.sleep(CACHE_CLEAN_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static class CachedAsset {
        protected long lastAccessTime;
        public CachedAsset() {
            this.lastAccessTime = System.currentTimeMillis();
        }
        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
        public long getLastAccessTime() {
            return lastAccessTime;
        }
    }
    private static class CachedImage extends CachedAsset {
        BufferedImage image;
        public CachedImage(BufferedImage image) {
            super();
            this.image = image;
        }
    }
    private static class CachedAnimation extends CachedAsset {
        Animation animation;
        public CachedAnimation(Animation animation) {
            super();
            this.animation = animation;
        }
    }
    private static class CachedSFXClip extends CachedAsset {
        List<Clip> clipPool = new ArrayList<>();
        int nextClipIndex = 0;

        public CachedSFXClip(List<Clip> clips) {
            super();
            this.clipPool.addAll(clips);
        }

        public synchronized Clip getNextClip() {
            if (clipPool.isEmpty()) return null;
            Clip clip = clipPool.get(nextClipIndex);
            nextClipIndex = (nextClipIndex + 1) % clipPool.size();
            clip.setFramePosition(0); // rewind
            return clip;
        }
    }
    private static class CachedInputStream extends CachedAsset {
        private final String data;
        public CachedInputStream(InputStream file) {
            super();
            String newData;
            try {
                newData = new String(file.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("[AssetLoader] Failed to read InputStream when storing file: ", e);
            }
            this.data = newData;
        }
        public InputStream getNewInputStream(){
            return new java.io.ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        }
    }
}

