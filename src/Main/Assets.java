package Main;

import ObjectSystem.Explosion;

import java.util.HashMap;
import java.util.Map;

public class Assets {
    public static class Animations {
        public static final String EXPLOSION = "/Resources/Animations/anim_explosion.json";
        public static final String CRATE_EXPLOSIVE_TRIGGER = "/Resources/Animations/anim_crate_explosive_triggered.json";
    }

    public static class Images {
        public static final String EXPLOSION_SMALL = "Resources\\Images\\effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = "Resources\\Images\\effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = "Resources\\Images\\effect_explosion_big.png";

        public static final String CRATE_EXPLOSIVE = "Resources\\Images\\Crates\\crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = "Resources\\Images\\Crates\\crate_explosive_triggered.png";

        public static final String CRATE_BACKGROUND = "Resources/Images/Crates/crate_background.png";
    }

    public static class Prefabs {
        public static final String EXPLOSION = "/Resources\\Prefabs\\prefab_explosion.json";
        public static final String CRATE_BACKGROUND = "Resources/Prefabs/prefab_crate_background.json";
    }

    private static final Map<String, String> assetMap = new HashMap<>();

    static {
        assetMap.put("Animations.EXPLOSION", Animations.EXPLOSION);
        assetMap.put("Animations.CRATE_EXPLOSIVE_TRIGGER", Animations.CRATE_EXPLOSIVE_TRIGGER);

        assetMap.put("Images.EXPLOSION_SMALL", Images.EXPLOSION_SMALL);
        assetMap.put("Images.EXPLOSION_MEDIUM", Images.EXPLOSION_MEDIUM);
        assetMap.put("Images.EXPLOSION_BIG", Images.EXPLOSION_BIG);
        assetMap.put("Images.CRATE_EXPLOSIVE", Images.CRATE_EXPLOSIVE);
        assetMap.put("Images.CRATE_EXPLOSIVE_TRIGGERED", Images.CRATE_EXPLOSIVE_TRIGGERED);
        assetMap.put("Images.CRATE_BACKGROUND", Images.CRATE_BACKGROUND);

        assetMap.put("Prefabs.EXPLOSION", Prefabs.EXPLOSION);
        assetMap.put("Prefabs.CRATE_BACKGROUND",Prefabs.CRATE_BACKGROUND);
    }

    public static String getAssetPath(String key) {
        return assetMap.getOrDefault(key, key); // Fallback to raw value if not found
    }
}
