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
    private static final long MAX_UNUSED_TIME_MS = 2 * 60 * 1000;
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
        path = Assets.getAssetPath(path);
        CachedPrefab cachedPrefab;

        synchronized (cache) {
            if (cache.containsKey(path)) {
                cachedPrefab = (CachedPrefab) cache.get(path);
                cachedPrefab.updateLastAccessTime();
            } else {
                cachedPrefab = loadAndCachePrefab(path);
                if (cachedPrefab == null) {
                    return null;
                }
            }
        }

        GameObject prefab = PrefabReader.getObject(cachedPrefab.getNewInputStream());
        if (prefab == null) {
            System.err.println("[AssetLoader] Failed to parse prefab: " + path);
            return null;
        }
        prefab.initialize();
        return prefab;
    }

    private CachedPrefab loadAndCachePrefab(String path) {
        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("[AssetLoader] Asset not found at: " + path);
                return null;
            }
            CachedPrefab cachedPrefab = new CachedPrefab(input);
            cache.put(path, cachedPrefab);
            System.out.println("[AssetLoader] Loaded prefab: " + path);
            return cachedPrefab;
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
    private static class CachedPrefab extends CachedAsset {
        private final String jsonData;
        public CachedPrefab(InputStream prefabFile) {
            super();
            String data;
            try {
                data = new String(prefabFile.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("[AssetLoader] Failed to read InputStream when storing prefab: ", e);
            }
            this.jsonData = data;
        }
        public InputStream getNewInputStream(){
            return new java.io.ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        }
    }
}

