package core.scene;

import core.asset.Assets;
import core.asset.LevelData;
import core.asset.LevelLoader;
import game.entities.GameObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionManager {
    private static LevelData currentLevel;
    private static int currentLevelIndex;
    private static final List<String> levelList = List.of(
            Assets.Tilemaps.LEVEL_0,
            Assets.Tilemaps.LEVEL_1,
            Assets.Tilemaps.LEVEL_2,
            Assets.Tilemaps.LEVEL_3,
            Assets.Tilemaps.LEVEL_4
    );


    public static void loadLevelByPath(String levelPath) {
        if (levelList.contains(levelPath)) {
            currentLevelIndex = levelList.indexOf(levelPath);
            loadLevelInternal(levelPath);
        } else {
            System.out.println("No such level: " + levelPath);
        }
    }

    public static void loadLevelByIndex(int val) {
        if (val >= 0 && val < levelList.size()) {
            currentLevelIndex = val;
            loadLevelInternal(levelList.get(currentLevelIndex));
        } else {
            System.out.println("No level at index: " + val);
        }
    }

    private static void loadLevelInternal(String levelPath) {
        currentLevel = LevelLoader.parse(levelPath);
        assert currentLevel != null;
        for (GameObject gameObject : currentLevel.initialGameobjects) {
            gameObject.initialize();
        }
    }
    public static LevelData getCurrentLevel(){
        return currentLevel;
    }
    public static boolean loadNextLevel(){
        currentLevelIndex++;
        if(currentLevelIndex < levelList.size()){
            loadLevelByPath(levelList.get(currentLevelIndex));
            return true;
        }
        return false;
    }
    public static void reloadCurrentLevel(){
        currentLevel = null;
        loadLevelByPath(levelList.get(currentLevelIndex));
    }
}
