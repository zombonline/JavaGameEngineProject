package Main;

import ObjectSystem.GameObject;

import java.util.ArrayList;

public class SessionManager {
    private static LevelData currentLevel;
    private static int currentLevelIndex;
    private static ArrayList<String> levelList = new ArrayList<String>();
    static {
        levelList.add(Assets.Tilemaps.LEVEL_TEST);
        levelList.add(Assets.Tilemaps.LEVEL_TEST_1);
    }

    public static void LoadLevelByPath(String levelPath){
        if(levelList.contains(levelPath)){
            currentLevelIndex = levelList.indexOf(levelPath);
            currentLevel = LevelLoader.parse(levelPath);
            for(GameObject gameObject : currentLevel.initialGameobjects){
                gameObject.initialize();
            }
        } else {
            System.out.println("No such level: " + levelPath);
        }
    }

    public static void LoadLevelByInt(int val){
        if(0 < val && val < levelList.size()){
            currentLevelIndex = val;
            currentLevel = LevelLoader.parse(levelList.get(currentLevelIndex));
            for(GameObject gameObject : currentLevel.initialGameobjects){
                gameObject.initialize();
            }
        } else {
            System.out.println("No Level at index: " + val);
        }
    }
    public static LevelData getCurrentLevel(){
        return currentLevel;
    }
    public static boolean loadNextLevel(){
        currentLevelIndex++;
        if(currentLevelIndex < levelList.size()){
            LoadLevelByPath(levelList.get(currentLevelIndex));
            return true;
        }
        return false;
    }
    public static void reloadCurrentLevel(){
        currentLevel = null;
        LoadLevelByPath(levelList.get(currentLevelIndex));
    }
}
