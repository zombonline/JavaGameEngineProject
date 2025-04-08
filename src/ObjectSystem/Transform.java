package ObjectSystem;

import Main.GamePanel;
import Main.Main;
import Utility.Vector2;

import javax.sound.midi.VoiceStatus;
import java.util.HashMap;
import java.util.Map;

public class Transform extends Component{
    private Vector2 position;
    private Vector2 rotation;
    private Vector2 scale;
    public Transform(Vector2 position, Vector2 rotation, Vector2 scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }
    public Transform(){
        this.position = new Vector2(0,0);
        this.rotation = new Vector2(0,0);
        this.scale = new Vector2(1,1f);
    }
    public void setPosition(Vector2 newPosition){
        position = newPosition;
    }
    public void translate(Vector2 translation) {
        if (translation.equals(Vector2.zero)){
            return;}
        position = position.add(translation);
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
    public Vector2 getScreenPosition(float parallaxFactor) {
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
}
