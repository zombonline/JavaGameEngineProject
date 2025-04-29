package game.components.core;

import main.GamePanel;
import main.Main;
import core.utils.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transform extends Component {
    private Vector2 position;
    private Vector2 scale;

    public Transform(Vector2 position, Vector2 scale){
        this.position = position;
        this.scale = scale;
    }

    //region position and scale logic
    public void setPosition(Vector2 newPosition){
        Vector2 delta = newPosition.sub(position); // How much the parent moves
        for (Transform child : children) {
            child.setPosition(child.getPosition().add(delta)); // Move each child by same delta
        }
        position = newPosition;
    }
    public Vector2 getPosition(){return position;}
    public  Vector2 getScreenPosition(){
        return position.add(scale.div(2)).mul(GamePanel.WORLD_SCALE).sub(getScreenScale().div(2)).sub(Main.camera.getPosition());}
    public Vector2 getScreenPosition(Vector2 parallaxFactor) {
        Vector2 adjustedCameraPos = Main.camera.getPosition().mul(parallaxFactor); // Scale camera movement
        return position.mul(GamePanel.WORLD_SCALE)
                .sub(getScreenScale().div(2))
                .sub(adjustedCameraPos); // Apply modified camera position
    }
    public void translate(Vector2 translation) {
        if (translation.equals(Vector2.zero)){
            return;}
        setPosition(position.add(translation));
    }

    public void setScale(Vector2 scale) {this.scale = scale;}
    public Vector2 getScale(){return scale;}
    public Vector2 getScreenScale(){return scale.mul(GamePanel.WORLD_SCALE);}
    //endregion

    //region Child parent logic
    private final ArrayList<Transform> children = new ArrayList<>();
    private Transform parent;
    public void addChild(Transform child) {
        if (child == this || children.contains(child)) return;
        if(child.getParent() != null){
            child.getParent() .removeChild(child);
        }
        child.setParent(this);
        children.add(child);
    }
    public void removeChild(Transform child){
        child.setParent(null);
        children.remove(child);
    }
    public ArrayList<Transform> getChildren(){
        return children;
    }
    public void setParent(Transform parent){
        this.parent = parent;
    }
    public Transform getParent(){
        return parent;
    }
    //endregion

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("position", Vector2.zero);
        defaultValues.put("scale", Vector2.one);
        return defaultValues;
    }
}
