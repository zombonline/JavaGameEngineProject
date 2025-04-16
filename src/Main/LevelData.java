package Main;

import ObjectSystem.GameObject;

import java.util.ArrayList;

public class LevelData {
    public final ArrayList<GameObject> gameObjectsToAwake = new ArrayList<GameObject>();
    public final ArrayList<GameObject> activeGameObjects = new ArrayList<GameObject>();
    public final ArrayList<GameObject> gameObjectsToDestroy = new ArrayList<GameObject>();
    public final SpatialHashGrid spatialHashGrid = new SpatialHashGrid(8);
    int cratesToDestroy;
    int cratesDestroyed = 0;
    int currentCombo = 0;
    int highestCombo = 0;
    int width;
    int height;

    public LevelData(ArrayList<GameObject> gameObjects, int cratesToDestroy, int mapWidth, int mapHeight){
        this.gameObjectsToAwake.addAll(gameObjects);
        this.cratesToDestroy = cratesToDestroy;
        this.width = mapWidth;
        this.height = mapHeight;
        System.out.println("LevelData created: " + gameObjects.size() + " game objects, " + cratesToDestroy + " crates to destroy");
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
        for(GameObject go : activeGameObjects){
            if(go.getName().equals(name)){
                return go;
            }
        }
        return null;
    }
    public int getWidth(){return width;}
    public int getHeight(){return height;}

}
