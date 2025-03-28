package Utility;

public enum Tile {
    prefab_crate_hover(1),
    prefab_crate_basic(2),
    prefab_crate_bounce(3),
    prefab_crate_explosive(4),
    prefab_crate_metal(5),
    prefab_crate_background(10);
    private final int value;

    Tile(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static Tile fromValue(int value) {
        for (Tile d : values()) {
            if (d.value == value) {
                return d;
            }
        }
        return null;
    }
}
