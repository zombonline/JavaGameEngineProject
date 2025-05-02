package main;

import core.asset.AssetLoader;
import core.asset.Assets;
import core.ui.GameUI;

public class GameInitiator {
    public GameInitiator(){
        GameUI.getInstance().updateScreen(GameUI.Screen.LOADING);
        Thread initThread = new Thread(() -> {
//            LevelData levelData = SessionManager.loadLevelByIndex(0);
//            levelData.initializeObjects();
            for(String asset : Assets.getAssetMap().values()){
                if(asset.contains("Image")){
                    AssetLoader.getInstance().getImage(asset);
                }
            }
        });
        initThread.start();
        try {
            initThread.join();  // main thread waits until initThread is completely done
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GameUI.getInstance().updateScreen(GameUI.Screen.GAME);

    }
}
