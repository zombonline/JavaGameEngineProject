package core.asset;

import core.audio.SFXPlayer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Assets {
    private Assets() {} //private constructor as the class is static

    // ANIMATION .JSON FILES
    public static class Animations {
        private static final String ANIMATIONS_PATH = "/Resources/Animations/";

        public static final String CRATE_EXPLOSIVE_TRIGGER = ANIMATIONS_PATH +"anim_crate_explosive_triggered.json";
        public static final String CRATE_INTERACTABLE_GLOW = ANIMATIONS_PATH +"anim_crate_interactable.json";

        public static final String EXPLOSION = ANIMATIONS_PATH +"anim_explosion.json";

        public static final String PLAYER_IDLE = ANIMATIONS_PATH +"anim_player_idle.json";
        public static final String PLAYER_FALL = ANIMATIONS_PATH +"anim_player_fall.json";
        public static final String PLAYER_RUN = ANIMATIONS_PATH +"anim_player_run.json";
        public static final String PLAYER_WALK = ANIMATIONS_PATH +"anim_player_walk.json";
    }

    // IMAGE FILES
    public static class Images {
        private static final String IMAGES_PATH = "/Resources/Images/";

        public static final String BACKGROUND = IMAGES_PATH +"background.png";

        public static final String CRATE_BASIC = IMAGES_PATH +"Crates/crate_basic.png";
        public static final String CRATE_BOUNCE = IMAGES_PATH +"Crates/crate_bounce.png";
        public static final String CRATE_EXPLOSIVE = IMAGES_PATH +"Crates/crate_explosive.png";
        public static final String CRATE_EXPLOSIVE_TRIGGERED = IMAGES_PATH +"Crates/crate_explosive_triggered.png";
        public static final String CRATE_HOVER = IMAGES_PATH +"Crates/crate_hover.png";
        public static final String CRATE_HOVER_EFFECT = IMAGES_PATH +"Crates/crate_hover_effect.png";
        public static final String CRATE_METAL = IMAGES_PATH +"Crates/crate_metal.png";
        public static final String CRATE_MOVING = IMAGES_PATH +"Crates/crate_moving.png";
        public static final String CRATE_REINFORCED = IMAGES_PATH +"Crates/crate_reinforced.png";
        public static final String CRATE_TELEPORT = IMAGES_PATH +"Crates/crate_teleport.png";

        public static final String CRATE_INTERACTABLE_GLOW_1 = IMAGES_PATH +"Crates/crate_interactable_glow.png";
        public static final String CRATE_INTERACTABLE_GLOW_2 = IMAGES_PATH +"Crates/crate_interactable_glow_1.png";
        public static final String CRATE_INTERACTABLE_GLOW_3 = IMAGES_PATH +"Crates/crate_interactable_glow_2.png";
        public static final String CRATE_INTERACTABLE_GLOW_4 = IMAGES_PATH +"Crates/crate_interactable_glow_3.png";
        public static final String CRATE_INTERACTABLE_GLOW_5 = IMAGES_PATH +"Crates/crate_interactable_glow_4.png";

        public static final String DOOR = IMAGES_PATH +"door.png";

        public static final String EXPLOSION_SMALL = IMAGES_PATH +"effect_explosion_small.png";
        public static final String EXPLOSION_MEDIUM = IMAGES_PATH +"effect_explosion_medium.png";
        public static final String EXPLOSION_BIG = IMAGES_PATH +"effect_explosion_big.png";

        public static final String PLACEHOLDER_NPC = IMAGES_PATH +"placeholder_npc.png";

        public static final String PLACEHOLDER_TERRAIN = IMAGES_PATH +"placeholder_terrain.png";
        public static final String PLACEHOLDER_BACKGROUND = IMAGES_PATH +"placeholder_background.png";


        public static final String PLAYER_IDLE = IMAGES_PATH +"Player/player_idle.png";
        public static final String PLAYER_IDLE_BLINK = IMAGES_PATH +"Player/player_idle_blink.png";
        public static final String PLAYER_FALL_1 = IMAGES_PATH +"Player/player_fall_1.png";
        public static final String PLAYER_FALL_2 = IMAGES_PATH +"Player/player_fall_2.png";
        public static final String PLAYER_RUN_1 = IMAGES_PATH +"Player/player_run_1.png";
        public static final String PLAYER_RUN_2 = IMAGES_PATH +"Player/player_run_2.png";
        public static final String PLAYER_RUN_3 = IMAGES_PATH +"Player/player_run_3.png";
        public static final String PLAYER_RUN_4 = IMAGES_PATH +"Player/player_run_4.png";

        public static final String SPRITESHEET_BACKGROUND = IMAGES_PATH+"Sprite Sheets/spritesheet_background.png";
        public static final String SPRITESHEET_TERRAIN = IMAGES_PATH+"Sprite Sheets/spritesheet_terrain.png";
        public static final String SPRITESHEET_PANELS = IMAGES_PATH +"Sprite Sheets/spritesheet_panels.png";
        public static final String SPRITESHEET_PANELS_2X2 = IMAGES_PATH +"Sprite Sheets/spritesheet_panels_2x2.png";
        public static final String SPRITESHEET_PANELS_1X2 = IMAGES_PATH +"Sprite Sheets/spritesheet_panels_1x2.png";
        public static final String SPRITESHEET_PANELS_2X1 = IMAGES_PATH +"Sprite Sheets/spritesheet_panels_2x1.png";

    }

    // PREFAB .JSON FILES
    public static class Prefabs {
        private static final String PREFABS_PATH = "/Resources/Prefabs/";

        public static final String CRATE_BASIC = PREFABS_PATH +"prefab_crate_basic.json";
        public static final String CRATE_BASIC_WITH_RB = PREFABS_PATH +"prefab_crate_basic_with_rb.json";
        public static final String CRATE_BOUNCE = PREFABS_PATH +"prefab_crate_bounce.json";
        public static final String CRATE_EXPLOSIVE = PREFABS_PATH +"prefab_crate_explosive.json";
        public static final String CRATE_EXPLOSIVE_WITH_RB = PREFABS_PATH +"prefab_crate_explosive_with_rb.json";
        public static final String CRATE_HOVER = PREFABS_PATH +"prefab_crate_hover.json";
        public static final String CRATE_METAL = PREFABS_PATH +"prefab_crate_metal.json";
        public static final String CRATE_METAL_WITH_RB = PREFABS_PATH +"prefab_crate_metal_with_rb.json";
        public static final String CRATE_MOVING = PREFABS_PATH +"prefab_crate_horizontal_moving.json";
        public static final String CRATE_ORANGE_BITS = PREFABS_PATH +"prefab_crate_orange_bits.json";
        public static final String CRATE_REINFORCED = PREFABS_PATH +"prefab_crate_reinforced.json";
        public static final String CRATE_TELEPORT = PREFABS_PATH +"prefab_crate_teleport.json";

        public static final String CRATE_INTERACTABLE_GLOW = PREFABS_PATH +"prefab_crate_interactable_glow.json";


        public static final String DOOR = PREFABS_PATH +"prefab_door.json";

        public static final String EXPLOSION = PREFABS_PATH +"prefab_explosion.json";

        public static final String NPC = PREFABS_PATH +"prefab_npc.json";

        public static final String PLAYER = PREFABS_PATH +"prefab_player.json";

        public static final String PLACEHOLDER_TERRAIN = PREFABS_PATH+"prefab_placeholder_terrain.json";
        public static final String PLACEHOLDER_BACKGROUND = PREFABS_PATH+"prefab_placeholder_background.json";
        public static final String PLACEHOLDER_PANELS = PREFABS_PATH +"prefab_placeholder_panels.json";
    }

    public static class SFXClips{
        private static final String SFX_PATH = "/Resources/SFX/";
        public static final String CRATE_BOUNCE = SFX_PATH + "crate_bounce.wav";
        public static final String CRATE_DESTROYED = SFX_PATH +"crate_destroyed.wav";
        public static final String CRATE_REINFORCED_DESTROYED = SFX_PATH +"crate_reinforced_destroyed.wav";
        public static final String CRATE_REINFORCED_BOUNCE = SFX_PATH +"crate_reinforced_bounce.wav";

        public static final String EXPLOSION = SFX_PATH +"explosion.wav";

        public static final String PLAYER_RUN_1 = SFX_PATH +"player_run_1.wav";
        public static final String PLAYER_RUN_2 = SFX_PATH +"player_run_2.wav";
        public static final String PLAYER_JUMP = SFX_PATH +"player_jump.wav";

    }

    // TILEMAP .TMX FILES
    public static class Tilemaps {
        private static final String TILEMAPS_PATH = "/Resources/Tilemaps/";
        public static final String LEVEL_1 = TILEMAPS_PATH+"level_1.tmx";
        public static final String LEVEL_0 = TILEMAPS_PATH+"level_0.tmx";
        public static final String LEVEL_1_NEW = TILEMAPS_PATH +"level_1_new.tmx";
        public static final String LEVEL_2 = TILEMAPS_PATH +"level_2.tmx";
    }

    // TILESET .TSX FILES
    public static class Tilesets {
        private static final String TILESETS_PATH = "/Resources/Tilesets/";
        public static final String TILES = TILESETS_PATH + "Tiles.tsx";
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
        if(key.startsWith("..")){
            key = key.replace("..","/Resources");
        }
        return assetMap.getOrDefault(key, key); // Fallback to raw value if not found
    }
}
