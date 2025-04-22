package Utility;

public enum BackgroundTileValue {
    PIPE_CORNER_1(1),
    PIPE_CORNER_2(2),
    PIPE_CORNER_3(3),
    PIPE_CORNER_4(4),
    PIPE_VERTICAL(5),
    PIPE_HORIZONTAL(6);

    private final int value;
    BackgroundTileValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public static BackgroundTileValue fromValue(int value) {
        for (BackgroundTileValue d : values()) {
            if (d.value == value) {
                return d;
            }
        }
        return null;
    }
}
