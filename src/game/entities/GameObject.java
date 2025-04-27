package game.entities;

import game.components.core.Component;
import game.components.core.Transform;
import core.scene.SessionManager;
import main.GamePanel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameObject {
    private static final String DEFAULT_NAME = "Unnamed";
    private final Transform transform;
    private final List<Component> componentList = new ArrayList<>();
    private final String name;

    private final HashMap extraDataMap = new HashMap<>();

    public GameObject(String name) {
        this.transform = new Transform();
        this.transform.setGameObject(this);
        componentList.add(this.transform);
        this.name = (name == null || name.isBlank()) ? DEFAULT_NAME : name; // Default naming
    }
    public void initialize(){
        if(SessionManager.getCurrentLevel() != null){
            SessionManager.getCurrentLevel().gameObjectsToAwake.add(this);
        }
    }
    public GameObject() {
        this(null);
    }
    public <T extends Component> T addComponent(T component) {
        componentList.add(component); // Added explicit list reference
        component.setGameObject(this);
        return component;
    }

    public <T extends Component> void removeComponentByType(Class<T> type) {
        Component componentToRemove = findComponent(type);
        if (componentToRemove != null) {
            componentList.remove(componentToRemove);
        }
    }

    public <T extends Component> T getComponent(Class<T> type) {
        Component foundComponent = findComponent(type);
        return type.cast(foundComponent);
    }

    public <T extends Component> boolean hasComponent(Class<T> type){
        return findComponent(type)!=null;
    }

    public List<Component> getAllComponents() {
        return Collections.unmodifiableList(componentList); // Defensive copy with unmodifiable list
    }

    public void awake(){
        for(Component component : componentList){
            component.awake();
        }
    }
    public void start(){
        for (Component component :componentList){
            component.start();
        }
    }
    public void update() {
        for (Component component : componentList) {
            component.update();
        }
    }
    public void draw(Graphics2D g2d) {
        for (Component component : componentList) {
            component.draw(g2d);
        }
    }
    public static void destroy(GameObject object){
        SessionManager.getCurrentLevel().gameObjectsToDestroy.add(object);
    }
    private <T extends Component> Component findComponent(Class<T> type) {
        for (Component component : componentList) {
            if (type.isInstance(component)) {
                return component;
            }
        }
        return null; // No matching component
    }

    public void insertExtraData(Object key, Object value){
        extraDataMap.put(key,value);
    }

    public boolean hasExtraData(Object key){
        return extraDataMap.containsKey(key);
    }

    public Object getExtraData(String key){
        return extraDataMap.getOrDefault(key, null);
    }

    public Transform getTransform() {
        return transform;
    }

    public String getName() {
        return name;
    }

    public static <T extends Component> GameObject findFirstObjectByType(Class<T> type){
        for(GameObject gameObject : SessionManager.getCurrentLevel().activeGameObjects){
            if(gameObject.hasComponent(type)){
                return gameObject;
            }
        }
        System.out.println("Unable to find object with component: " + type.getName());
        return null;
    }
    public static <T extends Component> ArrayList<GameObject> findAllObjectsByType(Class<T> type){
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        for(GameObject gameObject : SessionManager.getCurrentLevel().activeGameObjects){
            if(gameObject.hasComponent(type)){
                gameObjects.add(gameObject);
            }
        }
        return gameObjects;
    }
}