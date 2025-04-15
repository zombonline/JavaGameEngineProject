package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public Transform transform;
    private List<Component> components = new ArrayList<>();
    public String name;
    public static void destroy(GameObject object){
        GamePanel.gameObjectsToDestroy.add(object);
    }
    public GameObject(String name) {
        this.transform = new Transform();
        this.transform.setGameObject(this);
        this.name = name;
        GamePanel.gameObjectsToAwake.add(this);
    }
    public <T extends Component> T addComponent(T component) {
        components.add(component);
        component.setGameObject(this);
        return component;
    }
    public <T extends Component> void removeComponent(Class<T> type) {
        for (Component c : components) {
            if (type.isInstance(c)) {
                components.remove(c);
                break;
            }
        }
    }
    public <T extends Component> T getComponent(Class<T> type) {
        for (Component c : components) {
            if (type.isInstance(c)) return type.cast(c);
        }
        return null;
    }
    public List<Component> getAllComponents(){
        return components;
    }
    public void awake(){
        for(Component component : components){
            component.awake();
        }
    }
    public void update() {
        for (Component component : components) {
            component.update();
        }
    }
    public void draw(Graphics2D g2d) {
        for (Component component : components) {
            component.draw(g2d);
        }
    }
}