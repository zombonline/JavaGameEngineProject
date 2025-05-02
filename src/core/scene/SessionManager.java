package core.scene;

import core.asset.AssetLoader;
import core.asset.Assets;
import core.asset.LevelData;
import core.asset.LevelLoader;
import core.ui.GameUI;
import main.Main;

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
    private static final Object levelLoadLock = new Object();


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
        synchronized (levelLoadLock) {
            System.out.println("Loading level: " + levelPath + " on thread: " + Thread.currentThread().getName());
            GameUI.getInstance().updateScreen(GameUI.Screen.LOADING);
            Thread initThread = new Thread(() -> {
                currentLevel = LevelLoader.parse(levelPath);
                currentLevel.initializeObjects();
            });
            initThread.start();
            while(initThread.isAlive()){
                Main.gamePanel.draw();
            }
            try {
                initThread.join();  // main thread waits until initThread is completely done
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GameUI.getInstance().updateScreen(GameUI.Screen.GAME);
        }
    }
    public static LevelData getCurrentLevel(){
        return currentLevel;
    }
    public static boolean loadNextLevel(){
        currentLevelIndex++;
        if(currentLevelIndex < levelList.size()){
            GameUI.getInstance().updateScreen(GameUI.Screen.LOADING);
           loadLevelByIndex(currentLevelIndex);
            return true;
        }
        return false;
    }
    public static void reloadCurrentLevel(){
        currentLevel = null;
        loadLevelByPath(levelList.get(currentLevelIndex));
    }
}
