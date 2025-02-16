package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import Utility.Vector2;

import java.awt.*;

public class Player extends Component{
    KeyHandler keyHandler;
    private float speed;


    public Player(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
        setDefaultValues();
    }
    public void setDefaultValues(){
        this.speed = 5;
    }

    public void update(){

        Vector2 movement = Vector2.zero;

        if(keyHandler.up){movement = movement.add(Vector2.up);}
        if(keyHandler.down){movement = movement.add(Vector2.down);}
        if(keyHandler.left){movement = movement.add(Vector2.left);}
        if(keyHandler.right){movement = movement.add(Vector2.right);}
        if(movement.equals(Vector2.zero)){return;}
        gameObject.transform.translate(movement.getNormalized().mul(speed*GamePanel.getDeltaTime()));
    }
    public void draw(Graphics2D g2d){
    }
}
