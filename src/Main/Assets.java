package Main;

import java.lang.reflect.Field;
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
        public static final String DOG_BONE = "/Resources/Images/dog_bone.png";
        public static final String EXPLOSION_SMALL = "/Resources/Images/effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = "/Resources/Images/effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = "/Resources/Images/effect_explosion_big.png";

        public static final String CRATE_EXPLOSIVE = "/Resources/Images/Crates/crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = "/Resources/Images/Crates/crate_explosive_triggered.png";

        public static final String CRATE_BASIC = "/Resources/Images/Crates/crate_basic.png";

        public static final String CRATE_REINFORCED = "/Resources/Images/Crates/crate_reinforced.png";

        public static final String CRATE_MOVING = "/Resources/Images/Crates/crate_moving.png";

        public static final String CRATE_ORANGE_BITS = "/Resources/Images/crate_orange_bits.png";

        public static final String PLAYER_TEST_RUN_1 = "/Resources/Images/test_player_run_1.png";
        public static final String PLAYER_TEST_RUN_2 = "/Resources/Images/test_player_run_2.png";
        public static final String PLAYER_TEST_RUN_3 = "/Resources/Images/test_player_run_3.png";
        public static final String PLAYER_TEST_RUN_4 = "/Resources/Images/test_player_run_4.png";
        public static final String PLAYER_IDLE = "/Resources/Images/player_idle.png";
        public static final String PLAYER_TEST_FALL_1 = "/Resources/Images/test_player_fall_1.png";
        public static final String PLAYER_TEST_FALL_2 = "/Resources/Images/test_player_fall_2.png";

        public static final String DOOR = "/Resources/Images/door.png";

        public static final String PLACEHOLDER_NPC = "/Resources/Images/placeholder_npc.png";

        public static final String PIPE_CORNER_1 = "/Resources/Images/Pipes/bg_pipe_corner_1.png";
        public static final String PIPE_CORNER_2 = "/Resources/Images/Pipes/bg_pipe_corner_2.png";
        public static final String PIPE_CORNER_3 = "/Resources/Images/Pipes/bg_pipe_corner_3.png";
        public static final String PIPE_CORNER_4 = "/Resources/Images/Pipes/bg_pipe_corner_4.png";
        public static final String PIPE_HORIZONTAL = "/Resources/Images/Pipes/bg_pipe_horizontal.png";
        public static final String PIPE_VERTICAL = "/Resources/Images/Pipes/bg_pipe_vertical.png";

        public  static final String PLACEHOLDER_CRATE = "/Resources/Images/placeholder_crate.png";
    }

    public static class Prefabs {
        public static final String DOG_BONE = "/Resources/Prefabs/prefab_dog_bone.json";
        public static final String EXPLOSION = "/Resources/Prefabs/prefab_explosion.json";
        public static final String DOOR = "/Resources/Prefabs/prefab_door.json";

        public static final String BACKGROUND_TILE = "/Resources/Prefabs/prefab_crate_orange_bits.json";
        public static final String PIPE_CORNER_1 = "/Resources/Prefabs/Pipes/prefab_pipe_corner_1.json";
        public static final String PIPE_CORNER_2 = "/Resources/Prefabs/Pipes/prefab_pipe_corner_2.json";
        public static final String PIPE_CORNER_3 = "/Resources/Prefabs/Pipes/prefab_pipe_corner_3.json";
        public static final String PIPE_CORNER_4 = "/Resources/Prefabs/Pipes/prefab_pipe_corner_4.json";
        public static final String PIPE_HORIZONTAL = "/Resources/Prefabs/Pipes/prefab_pipe_horizontal.json";
        public static final String PIPE_VERTICAL = "/Resources/Prefabs/Pipes/prefab_pipe_vertical.json";

        public static final String CRATE_HOVER = "/Resources/Prefabs/prefab_crate_hover.json";
        public static final String CRATE_BASIC = "/Resources/Prefabs/prefab_crate_basic.json";
        public static final String CRATE_BOUNCE = "/Resources/Prefabs/prefab_crate_bounce.json";
        public static final String CRATE_EXPLOSIVE = "/Resources/Prefabs/prefab_crate_explosive.json";
        public static final String CRATE_METAL = "/Resources/Prefabs/prefab_crate_metal.json";
        public static final String CRATE_REINFORCED = "/Resources/Prefabs/prefab_crate_reinforced.json";
        public static final String CRATE_MOVING = "/Resources/Prefabs/prefab_crate_horizontal_moving.json";
        public static final String PLAYER = "/Resources/Prefabs/prefab_player.json";
        public static final String CRATE_ORANGE_BITS = "/Resources/Prefabs/prefab_crate_orange_bits.json";

        public static final String NPC = "/Resources/Prefabs/prefab_npc.json";
        public static final String PLACEHOLDER_CRATE = "/Resources/Prefabs/prefab_placeholder_crate.json";
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
