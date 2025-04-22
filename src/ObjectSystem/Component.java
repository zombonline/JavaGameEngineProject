package ObjectSystem;

import Main.DebugText;

import java.awt.*;
import java.util.ArrayList;

public abstract class Component {
    protected GameObject gameObject;
    protected ArrayList<Component> requiredComponents = new ArrayList<>();
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }
    public GameObject getGameObject() {
        return gameObject;
    }
    public <T extends Component> T getComponent(Class<T> type) {
        return gameObject.getComponent(type);
    }
    public <T extends Component> boolean hasComponent(Class<T> type) {
        return gameObject.hasComponent(type);
    }
    public void awake() {}
    protected void getRequiredComponentReferences() {};
    public void update() {}
    public void draw(Graphics2D g2d) {}


    public void onDestroy(){}
//    public static Map<String,Object> getDefaultValues() {}

    protected <T extends Component> T fetchRequiredComponent(Class<T> tClass) {
        T component = gameObject.getComponent(tClass);
        if (component == null) {
            DebugText.logTemporarily(gameObject.getName() + " is missing required component: " + tClass.getSimpleName());
        }
        return component;
    }

}