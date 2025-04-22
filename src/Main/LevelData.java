package Main;

import ObjectSystem.Crate.Crate;
import ObjectSystem.GameObject;

import java.util.ArrayList;

public class LevelData {
    public final String levelString;
    public final ArrayList<GameObject> initialGameobjects = new ArrayList<>();
    public final ArrayList<GameObject> gameObjectsToAwake = new ArrayList<GameObject>();
    public final ArrayList<GameObject> activeGameObjects = new ArrayList<GameObject>();
    public final ArrayList<GameObject> gameObjectsToDestroy = new ArrayList<GameObject>();
    public final SpatialHashGrid spatialHashGrid = new SpatialHashGrid(8);
    final int cratesToDestroy;
    int cratesDestroyed = 0;
    int highestCombo = 0;
    final int width;
    final int height;

    public LevelData(String levelString, ArrayList<GameObject> gameObjects, int mapWidth, int mapHeight){
        this.levelString = levelString;
        this.initialGameobjects.addAll(gameObjects);
        this.cratesToDestroy = countCratesToDestroy();
        this.width = mapWidth;
        this.height = mapHeight;
        System.out.println("LevelData created: " + initialGameobjects.size() + " game objects, " + cratesToDestroy + " crates to destroy");
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
    public int getHighestCombo(){return highestCombo;}
    public void setHighestCombo(int val){highestCombo=val;}

    private int countCratesToDestroy(){
        int result = 0;
        for(GameObject gameObject : initialGameobjects){
            Crate crate = gameObject.getComponent(Crate.class);
            if(crate!=null){
                if(crate.isBreakable()){ result++;}
            }
        }
        return result;
    }
}
