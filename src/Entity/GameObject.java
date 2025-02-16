package Entity;

import Utility.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public Transform transform;
    private List<Component> components = new ArrayList<>();
    public String name;

    public static GameObject createNew(String name, Vector2 initialPosition){
        GameObject newObject = new GameObject(name);
        newObject.transform.setPosition(initialPosition);
        return newObject;
    }

    public GameObject(String name) {
        this.transform = new Transform();
        this.name = name;
    }

    public <T extends Component> T addComponent(T component) {
        components.add(component);
        component.setGameObject(this);
        return component;
    }
    public <T extends Component> void addComponents(T component) {
        components.add(component);
        component.setGameObject(this);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        for (Component c : components) {
            if (type.isInstance(c)) return type.cast(c);
        }
        return null;
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