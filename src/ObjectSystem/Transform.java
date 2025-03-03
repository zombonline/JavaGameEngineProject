package ObjectSystem;

import Main.GamePanel;
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
    public Vector2 getPosition(){
        return this.position;
    }
    public  Vector2 getScreenPosition(){
        return position.mul(GamePanel.WORLD_SCALE);
    }
}
