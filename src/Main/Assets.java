package Main;

import ObjectSystem.Explosion;

import java.lang.reflect.Field;
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
        public static final String EXPLOSION_SMALL = "/Resources/Images/effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = "/Resources/Images/effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = "/Resources/Images/effect_explosion_big.png";

        public static final String CRATE_EXPLOSIVE = "/Resources/Images/Crates/crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = "/Resources/Images/Crates/crate_explosive_triggered.png";

        public static final String CRATE_BASIC = "/Resources/Images/Crates/crate_basic.png";

        public static final String CRATE_BACKGROUND = "/Resources/Images/Crates/crate_background.png";

        public static final String PLAYER_TEST_RUN_1 = "/Resources/Images/test_player_run_1.png";
        public static final String PLAYER_TEST_RUN_2 = "/Resources/Images/test_player_run_2.png";
        public static final String PLAYER_TEST_RUN_3 = "/Resources/Images/test_player_run_3.png";
        public static final String PLAYER_TEST_RUN_4 = "/Resources/Images/test_player_run_4.png";
        public static final String PLAYER_IDLE = "/Resources/Images/player_idle.png";
        public static final String PLAYER_TEST_FALL_1 = "/Resources/Images/test_player_fall_1.png";
        public static final String PLAYER_TEST_FALL_2 = "/Resources/Images/test_player_fall_2.png";

        public static final String DOOR_TOP = "/Resources/Images/door_top.png";
        public static final String DOOR_BOTTOM = "/Resources/Images/door_bottom.png";
    }

    public static class Prefabs {
        public static final String EXPLOSION = "/Resources/Prefabs/prefab_explosion.json";
        public static final String CRATE_BACKGROUND = "/Resources/Prefabs/prefab_crate_background.json";
        public static final String DOOR = "/Resources/Prefabs/prefab_door.json";
    }

    public static class Levels {
        public static final String LEVEL_TEST = "/Resources/Tilesets/test_level.tmx";
        public static final String LEVEL_TEST_1 = "/Resources/Tilesets/test_level_1.tmx";
    }

    private static final Map<String, String> assetMap = new HashMap<>();

    static {
        addAssetsFromClass("Animations", Animations.class);
        addAssetsFromClass("Images", Images.class);
        addAssetsFromClass("Prefabs", Prefabs.class);
        addAssetsFromClass("Levels", Levels.class);
    }

    private static void addAssetsFromClass(String prefix, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == String.class) {
                try {
                    String name = field.getName();
                    String value = (String) field.get(null);
                    assetMap.put(prefix + "." + name, value);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    public static String getAssetPath(String key) {
        return assetMap.getOrDefault(key, key); // Fallback to raw value if not found
    }
}
