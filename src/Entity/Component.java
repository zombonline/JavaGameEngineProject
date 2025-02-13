package Entity;

import java.awt.*;

public abstract class Component {
    protected GameObject gameObject;

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }
    public GameObject getGameObject() {
        return gameObject;
    }

    public void update() {}
    public void draw(Graphics2D g2d) {}
}