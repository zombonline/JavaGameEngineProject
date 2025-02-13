package Entity;

import Main.GamePanel;
import Main.KeyHandler;
import Utility.Vector2;

import java.awt.*;

public class Player extends Component{
    KeyHandler keyHandler;
    private int speed;


    public Player(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
        setDefaultValues();
    }
    public void setDefaultValues(){
        this.speed = 1;
    }

    public void update(){
        if(keyHandler.up){gameObject.transform.position = gameObject.transform.position.add(Vector2.up.mul(speed));}
        if(keyHandler.down){gameObject.transform.position = gameObject.transform.position.add(Vector2.down.mul(speed));}
        if(keyHandler.left){gameObject.transform.position = gameObject.transform.position.add(Vector2.left.mul(speed));}
        if(keyHandler.right){gameObject.transform.position = gameObject.transform.position.add(Vector2.right.mul(speed));}
    }

    public void draw(Graphics2D g2d){
    }
}
