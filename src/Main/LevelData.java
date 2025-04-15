package Main;

import ObjectSystem.GameObject;

import java.util.ArrayList;

public class LevelData {
    ArrayList<GameObject> gameObjects;
    int cratesToDestroy;
    int cratesDestroyed = 0;
    int currentCombo = 0;
    int highestCombo = 0;
    int width;
    int height;

    public LevelData(ArrayList<GameObject> gameObjects, int cratesToDestroy, int mapWidth, int mapHeight){
        this.gameObjects = gameObjects;
        this.cratesToDestroy = cratesToDestroy;
        this.width = mapWidth;
        this.height = mapHeight;
        System.out.println("LevelData created: " + gameObjects.size() + " game objects, " + cratesToDestroy + " crates to destroy");
    }

    public ArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }
    public int getCratesToDestroy(){
        return cratesToDestroy;
    }
    public int getCratesDestroyed(){
        return cratesDestroyed;
    }
    public void incrementCratesDestroyed(){
        ++cratesDestroyed;
    }

    public GameObject getObjectByName(String name){
        for(GameObject go : gameObjects){
            if(go.name.equals(name)){
                return go;
            }
        }
        return null;
    }

}
