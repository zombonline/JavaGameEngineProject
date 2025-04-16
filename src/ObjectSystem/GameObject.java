package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameObject {
    private static final String DEFAULT_NAME = "Unnamed";
    private final Transform transform;
    private final List<Component> componentList = new ArrayList<>();
    private final String name;
    public GameObject(String name) {
        this.transform = new Transform();
        this.transform.setGameObject(this);
        this.name = (name == null || name.isBlank()) ? DEFAULT_NAME : name; // Default naming
        if(GamePanel.currentLevel != null){
            GamePanel.currentLevel.gameObjectsToAwake.add(this);
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

    public List<Component> getAllComponents() {
        return Collections.unmodifiableList(componentList); // Defensive copy with unmodifiable list
    }

    public void awake(){
        for(Component component : componentList){
            component.awake();
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
        GamePanel.currentLevel.gameObjectsToDestroy.add(object);
    }
    private <T extends Component> Component findComponent(Class<T> type) {
        for (Component component : componentList) {
            if (type.isInstance(component)) {
                return component;
            }
        }
        return null; // No matching component
    }

    public Transform getTransform() {
        return transform;
    }

    public String getName() {
        return name;
    }

}