package Main;

import ObjectSystem.Explosion;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    public static class Animations {
        public static final String EXPLOSION = "/Resources/Animations/anim_explosion.json";
        public static final String CRATE_EXPLOSIVE_TRIGGER = "/Resources/Animations/anim_crate_explosive_triggered.json";

        public static final String PLAYER_TEST_RUN = "/Resources/Animations/anim_player_test_run.json";
        public static final String PLAYER_TEST_WALK = "/Resources/Animations/anim_player_test_walk.json";
        public static final String PLAYER_TEST_FALL = "/Resources/Animations/anim_player_test_fall.json";
        public static final String PLAYER_IDLE = "/Resources/Animations/anim_player_idle.json";

    }

    public static class Images {
        public static final String EXPLOSION_SMALL = "Resources\\Images\\effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = "Resources\\Images\\effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = "Resources\\Images\\effect_explosion_big.png";

        public static final String CRATE_EXPLOSIVE = "Resources\\Images\\Crates\\crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = "Resources\\Images\\Crates\\crate_explosive_triggered.png";

        public static final String CRATE_BACKGROUND = "Resources/Images/Crates/crate_background.png";

        public static final String PLAYER_TEST_RUN_1 = "Resources/Images/test_player_run_1.png";
        public static final String PLAYER_TEST_RUN_2 = "Resources/Images/test_player_run_2.png";
        public static final String PLAYER_TEST_RUN_3 = "Resources/Images/test_player_run_3.png";
        public static final String PLAYER_TEST_RUN_4 = "Resources/Images/test_player_run_4.png";
        public static final String PLAYER_IDLE = "Resources/Images/player_idle.png";
        public static final String PLAYER_TEST_FALL_1 = "Resources/Images/test_player_fall_1.png";
        public static final String PLAYER_TEST_FALL_2 = "Resources/Images/test_player_fall_2.png";
    }

    public static class Prefabs {
        public static final String EXPLOSION = "/Resources\\Prefabs\\prefab_explosion.json";
        public static final String CRATE_BACKGROUND = "Resources/Prefabs/prefab_crate_background.json";
    }

    private static final Map<String, String> assetMap = new HashMap<>();

    static {
        assetMap.put("Animations.EXPLOSION", Animations.EXPLOSION);
        assetMap.put("Animations.CRATE_EXPLOSIVE_TRIGGER", Animations.CRATE_EXPLOSIVE_TRIGGER);
        assetMap.put("Animations.PLAYER_TEST_RUN", Animations.PLAYER_TEST_RUN);
        assetMap.put("Animations.PLAYER_TEST_WALK", Animations.PLAYER_TEST_WALK);
        assetMap.put("Animations.PLAYER_IDLE", Animations.PLAYER_IDLE);


        assetMap.put("Images.EXPLOSION_SMALL", Images.EXPLOSION_SMALL);
        assetMap.put("Images.EXPLOSION_MEDIUM", Images.EXPLOSION_MEDIUM);
        assetMap.put("Images.EXPLOSION_BIG", Images.EXPLOSION_BIG);
        assetMap.put("Images.CRATE_EXPLOSIVE", Images.CRATE_EXPLOSIVE);
        assetMap.put("Images.CRATE_EXPLOSIVE_TRIGGERED", Images.CRATE_EXPLOSIVE_TRIGGERED);
        assetMap.put("Images.CRATE_BACKGROUND", Images.CRATE_BACKGROUND);
        assetMap.put("Images.PLAYER_TEST_RUN_1", Images.PLAYER_TEST_RUN_1);
        assetMap.put("Images.PLAYER_TEST_RUN_2", Images.PLAYER_TEST_RUN_2);
        assetMap.put("Images.PLAYER_TEST_RUN_3", Images.PLAYER_TEST_RUN_3);
        assetMap.put("Images.PLAYER_TEST_RUN_4", Images.PLAYER_TEST_RUN_4);
        assetMap.put("Images.PLAYER_IDLE", Images.PLAYER_IDLE);
        assetMap.put("Images.PLAYER_TEST_FALL_1", Images.PLAYER_TEST_FALL_1);
        assetMap.put("Images.PLAYER_TEST_FALL_2", Images.PLAYER_TEST_FALL_2);


        assetMap.put("Prefabs.EXPLOSION", Prefabs.EXPLOSION);
        assetMap.put("Prefabs.CRATE_BACKGROUND",Prefabs.CRATE_BACKGROUND);
    }

    public static String getAssetPath(String key) {
        return assetMap.getOrDefault(key, key); // Fallback to raw value if not found
    }
}
