package game.components.core;

import main.GamePanel;
import main.Main;
import core.utils.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transform extends Component {
    private Vector2 position;
    private Vector2 rotation;
    private Vector2 scale;
    private ArrayList<Transform> children = new ArrayList<>();
    public Transform(Vector2 position, Vector2 rotation, Vector2 scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    public Transform(){
        this(Vector2.zero, Vector2.zero, Vector2.one);
    }
    public void setPosition(Vector2 newPosition){

        Vector2 delta = newPosition.sub(position); // How much the parent moves
        for (Transform child : children) {
            System.out.println(getPosition() + ", " + newPosition);
            System.out.println("mobing child by: " + delta);
            child.setPosition(child.getPosition().add(delta)); // Move each child by same delta
        }
        position = newPosition;
    }
    public void translate(Vector2 translation) {
        if (translation.equals(Vector2.zero)){
            return;}
        setPosition(position.add(translation));
    }
    public Vector2 getScale(){
        return  this.scale;
    }
    public void setScale(Vector2 scale) {this.scale = scale;}
    public Vector2 getPosition(){
        return this.position;
    }
    public Vector2 getRotation(){return this.rotation;}
    public void setRotation(Vector2 rotation){this.rotation = rotation;}

    public Vector2 getScreenScale(){
        return scale.mul(GamePanel.WORLD_SCALE);
    }
    public  Vector2 getScreenPosition(){
        return position.add(scale.div(2)).mul(GamePanel.WORLD_SCALE).sub(getScreenScale().div(2)).sub(Main.camera.getPosition());
    }
    public Vector2 getScreenPosition(Vector2 parallaxFactor) {
        Vector2 adjustedCameraPos = Main.camera.getPosition().mul(parallaxFactor); // Scale camera movement
        return position.mul(GamePanel.WORLD_SCALE)
                .sub(getScreenScale().div(2))
                .sub(adjustedCameraPos); // Apply modified camera position
    }
    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("position", Vector2.zero);
        defaultValues.put("rotation", Vector2.zero);
        defaultValues.put("scale", Vector2.one);
        return defaultValues;
    }
    public void addChild(Transform child){
        children.add(child);
    }
    public void removeChild(Transform child){
        children.remove(child);
    }
    public ArrayList<Transform> getChildren(){
        return children;
    }

}
