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
    private static final long MAX_UNUSED_TIME = 2 * 60 * 1000;
    private static AssetLoader instance;
    private final Map<String, CachedAsset> cache = new HashMap<>();

    private AssetLoader() {
        new Thread(this::cleanCache).start();
    }

    public static AssetLoader getInstance(){
        if(instance == null){
            instance = new AssetLoader();
        }
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
        try (InputStream input = getClass().getResourceAsStream(path)){
            if(input == null){
                System.err.println("Asset not found at: " + path);
                return null;
            }
            BufferedImage image = ImageIO.read(input);
            cache.put(path, new CachedImage(image));
            System.out.println("Loaded image: " + path);
            return image;
        } catch (Exception e){
            System.err.println("Failed to load asset: " + path);
            e.printStackTrace();
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
        try(InputStream input = getClass().getResourceAsStream(path)){
            if(input == null){
                System.err.println("Asset not found at: " + path);
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Animation animation = objectMapper.readValue(input, Animation.class);
            cache.put(path, new CachedAnimation(animation));
            System.out.println("Loaded animation: " + path);
            return animation;

        } catch (Exception e){
            System.err.println("Failed to load asset: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public GameObject getPrefab(String path){
        path = Assets.getAssetPath(path);
        synchronized (cache) {
            if(cache.containsKey(path)){
                CachedPrefab cachedPrefab = (CachedPrefab) cache.get(path);
                cachedPrefab.updateLastAccessTime();
                GameObject prefab = PrefabReader.getObject(cachedPrefab.getNewInputStream());
                if(prefab == null ){
                    System.err.println("Failed to load prefab: " + path);
                    return null;
                }
                prefab.initialize();
                return prefab;
            }
        }
        try(InputStream input = getClass().getResourceAsStream(path)){
            if(input == null){
                System.err.println("Asset not found at: " + path);
                return null;
            }
            cache.put(path, new CachedPrefab(input));
            CachedPrefab cachedPrefab = (CachedPrefab) cache.get(path);
            GameObject prefab = PrefabReader.getObject(cachedPrefab.getNewInputStream());
            if(prefab == null ){
                System.err.println("Failed to load prefab: " + path);
                return null;
            }
            prefab.initialize();
            return prefab;
        } catch (Exception e){
            System.err.println("Failed to load asset: " + path);
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
                    if (currentTime - entry.getValue().getLastAccessTime() > MAX_UNUSED_TIME) {
                        System.out.println("Removing unused asset: " + entry.getKey());
                        iterator.remove();
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
                throw new RuntimeException("Failed to read InputStream when storing prefab: ", e);
            }
            this.jsonData = data;
        }
        public InputStream getNewInputStream(){
            return new java.io.ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8));
        }
    }
}

