package ObjectSystem;

import java.awt.*;
import java.util.Map;

public abstract class Component {
    protected GameObject gameObject;

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }
    public GameObject getGameObject() {
        return gameObject;
    }
    public <T extends Component> T getComponent(Class<T> type) {
        return  gameObject.getComponent(type);
    }
    public void awake() {}
    public void update() {}
    public void draw(Graphics2D g2d) {}
    public void onDestroy(){}
//    public static Map<String,Object> getDefaultValues() {}

}