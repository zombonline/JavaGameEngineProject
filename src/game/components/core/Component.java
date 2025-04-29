package game.components.core;

import core.utils.DebugText;
import game.entities.GameObject;

import java.awt.*;
import java.util.ArrayList;

public abstract class Component {
    protected GameObject gameObject;

    //override methods
    public void awake() {}
    public void start() {}
    protected void getRequiredComponentReferences() {}
    public void update() {}
    public void draw(Graphics2D g2d) {}
    public void onDestroy(){}


    protected <T extends Component> T fetchRequiredComponent(Class<T> tClass) {
        T component = gameObject.getComponent(tClass);
        if (component == null) {
            DebugText.logTemporarily(gameObject.getName() + " is missing required component: " + tClass.getSimpleName());
        }
        return component;
    }
    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }
    public GameObject getGameObject() {
        return gameObject;
    }

    //game object methods accessed by component
    public <T extends Component> T getComponent(Class<T> type) {
        return gameObject.getComponent(type);
    }
    public <T extends Component> boolean hasComponent(Class<T> type) {
        return gameObject.hasComponent(type);
    }
}