package Main;

import ObjectSystem.GameObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
            e.printStackTrace();
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

