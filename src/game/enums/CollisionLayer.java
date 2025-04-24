package game.enums;

import java.util.ArrayList;
import java.util.stream.Collectors;

public enum CollisionLayer {
    DEFAULT,
    WORLD;

    public static ArrayList<CollisionLayer> fromStringList(ArrayList<String> strings) {
        return (ArrayList<CollisionLayer>) strings.stream()
                .map(CollisionLayer::valueOf) // Convert each string to an enum
                .collect(Collectors.toList()); // Collect as a List
    }
}
