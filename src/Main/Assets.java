package Main;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    private Assets() {} //private constructor as the class is static

    // ANIMATION .JSON FILES
    public static class Animations {
        private static final String ANIMATIONS_PATH = "/Resources/Animations/";

        public static final String CRATE_EXPLOSIVE_TRIGGER = ANIMATIONS_PATH +"anim_crate_explosive_triggered.json";

        public static final String EXPLOSION = ANIMATIONS_PATH +"anim_explosion.json";

        public static final String PLAYER_IDLE = ANIMATIONS_PATH +"anim_player_idle.json";
        public static final String PLAYER_TEST_FALL = ANIMATIONS_PATH +"anim_player_test_fall.json";
        public static final String PLAYER_TEST_RUN = ANIMATIONS_PATH +"anim_player_test_run.json";
        public static final String PLAYER_TEST_WALK = ANIMATIONS_PATH +"anim_player_test_walk.json";
    }

    // IMAGE FILES
    public static class Images {
        private static final String IMAGES_PATH = "/Resources/Images/";

        public static final String CRATE_BASIC = IMAGES_PATH +"Crates/crate_basic.png";
        public static final String CRATE_EXPLOSIVE = IMAGES_PATH +"Crates/crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = IMAGES_PATH +"Crates/crate_explosive_triggered.png";
        public static final String CRATE_MOVING = IMAGES_PATH +"Crates/crate_moving.png";
        public static final String CRATE_ORANGE_BITS = IMAGES_PATH +"Crates/crate_orange_bits.png";
        public static final String CRATE_REINFORCED = IMAGES_PATH +"Crates/crate_reinforced.png";
        public static final String CRATE_TELEPORT = IMAGES_PATH +"Crates/crate_teleport.png";

        public static final String DOG_BONE = IMAGES_PATH +"dog_bone.png";

        public static final String DOOR = IMAGES_PATH +"door.png";

        public static final String EXPLOSION_SMALL = IMAGES_PATH +"effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = IMAGES_PATH +"effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = IMAGES_PATH +"effect_explosion_big.png";

        public static final String PLACEHOLDER_NPC = IMAGES_PATH +"placeholder_npc.png";

        public static final String PIPE_CORNER_1 = IMAGES_PATH +"Pipes/bg_pipe_corner_1.png";
        public static final String PIPE_CORNER_2 = IMAGES_PATH +"Pipes/bg_pipe_corner_2.png";
        public static final String PIPE_CORNER_3 = IMAGES_PATH +"Pipes/bg_pipe_corner_3.png";
        public static final String PIPE_CORNER_4 = IMAGES_PATH +"Pipes/bg_pipe_corner_4.png";
        public static final String PIPE_HORIZONTAL = IMAGES_PATH +"Pipes/bg_pipe_horizontal.png";
        public static final String PIPE_VERTICAL = IMAGES_PATH +"Pipes/bg_pipe_vertical.png";

        public static final String PLAYER_IDLE = IMAGES_PATH +"player_idle.png";
        public static final String PLAYER_TEST_FALL_1 = IMAGES_PATH +"test_player_fall_1.png";
        public static final String PLAYER_TEST_FALL_2 = IMAGES_PATH +"test_player_fall_2.png";
        public static final String PLAYER_TEST_RUN_1 = IMAGES_PATH +"test_player_run_1.png";
        public static final String PLAYER_TEST_RUN_2 = IMAGES_PATH +"test_player_run_2.png";
        public static final String PLAYER_TEST_RUN_3 = IMAGES_PATH +"test_player_run_3.png";
        public static final String PLAYER_TEST_RUN_4 = IMAGES_PATH +"test_player_run_4.png";

        public static final String TERRAIN_PLACEHOLDER = IMAGES_PATH +"terrain_placeholder.png";
        public static final String TERRAIN_STANDARD = IMAGES_PATH+"terrain_standard.png";
    }

    // PREFAB .JSON FILES
    public static class Prefabs {
        private static final String PREFABS_PATH = "/Resources/Prefabs/";

        public static final String CRATE_BASIC = PREFABS_PATH +"prefab_crate_basic.json";
        public static final String CRATE_BASIC_WITH_RB = PREFABS_PATH +"prefab_crate_basic_with_rb.json";
        public static final String CRATE_BOUNCE = PREFABS_PATH +"prefab_crate_bounce.json";
        public static final String CRATE_EXPLOSIVE = PREFABS_PATH +"prefab_crate_explosive.json";
        public static final String CRATE_HOVER = PREFABS_PATH +"prefab_crate_hover.json";
        public static final String CRATE_METAL = PREFABS_PATH +"prefab_crate_metal.json";
        public static final String CRATE_MOVING = PREFABS_PATH +"prefab_crate_horizontal_moving.json";
        public static final String CRATE_ORANGE_BITS = PREFABS_PATH +"prefab_crate_orange_bits.json";
        public static final String CRATE_REINFORCED = PREFABS_PATH +"prefab_crate_reinforced.json";
        public static final String CRATE_TELEPORT = PREFABS_PATH +"prefab_crate_teleport.json";

        public static final String DOG_BONE = PREFABS_PATH +"prefab_dog_bone.json";

        public static final String DOOR = PREFABS_PATH +"prefab_door.json";

        public static final String EXPLOSION = PREFABS_PATH +"prefab_explosion.json";

        public static final String NPC = PREFABS_PATH +"prefab_npc.json";

        public static final String PIPE_CORNER_1 = PREFABS_PATH +"Pipes/prefab_pipe_corner_1.json";
        public static final String PIPE_CORNER_2 = PREFABS_PATH +"Pipes/prefab_pipe_corner_2.json";
        public static final String PIPE_CORNER_3 = PREFABS_PATH +"Pipes/prefab_pipe_corner_3.json";
        public static final String PIPE_CORNER_4 = PREFABS_PATH +"Pipes/prefab_pipe_corner_4.json";
        public static final String PIPE_HORIZONTAL = PREFABS_PATH +"Pipes/prefab_pipe_horizontal.json";
        public static final String PIPE_VERTICAL = PREFABS_PATH +"Pipes/prefab_pipe_vertical.json";

        public static final String PLAYER = PREFABS_PATH +"prefab_player.json";

        public static final String TERRAIN_PLACEHOLDER = PREFABS_PATH+"prefab_terrain_placeholder.json";
    }

    // TILEMAP .TMX FILES
    public static class Tilemaps {
        private static final String TILEMAPS_PATH = "/Resources/Tilemaps/";
        public static final String LEVEL_TEST = TILEMAPS_PATH+"test_level.tmx";
        public static final String LEVEL_TEST_1 = TILEMAPS_PATH+"test_level_1.tmx";
    }

    // TILESET .TSX FILES
    public static class Tilesets {
        private static final String TILESETS_PATH = "/Resources/Tilesets/";
        public static final String TILES = TILESETS_PATH + "TTiles.tsx";
    }

    private static final Map<String, String> assetMap = new HashMap<>();

    static {
        addAssetsFromClass("Animations", Animations.class);
        addAssetsFromClass("Images", Images.class);
        addAssetsFromClass("Prefabs", Prefabs.class);
        addAssetsFromClass("Tilemaps", Tilemaps.class);
        addAssetsFromClass("Tilesets", Tilesets.class);
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
