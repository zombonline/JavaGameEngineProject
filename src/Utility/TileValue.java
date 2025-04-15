package Utility;

public enum TileValue {
    prefab_crate_hover(1),
    prefab_crate_basic(2),
    prefab_crate_bounce(3),
    prefab_crate_explosive(4),
    prefab_crate_metal(5),
    prefab_crate_background(10),
    prefab_door(11),
    prefab_player(12);
    private final int value;

    TileValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static TileValue fromValue(int value) {
        for (TileValue d : values()) {
            if (d.value == value) {
                return d;
            }
        }
        return null;
    }
}
