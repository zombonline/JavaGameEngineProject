package Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public Transform transform;
    private List<Component> components = new ArrayList<>();

    public GameObject() {
        this.transform = new Transform();
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