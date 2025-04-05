package ObjectSystem;

import Main.GamePanel;
import Main.Main;
import Utility.Vector2;

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
}
