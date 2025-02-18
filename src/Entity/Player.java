package Entity;

import Main.GamePanel;
import Main.Key;
import Main.KeyHandler;
import Utility.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends Component{
    KeyHandler keyHandler;
    private float speed;
    Rigidbody rb;

    private Key leftKey;
    private Key rightKey;
    private Key jumpKey;

    int xMovement = 0;
    public Player(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
        setDefaultValues();

        leftKey = keyHandler.addKey(KeyEvent.VK_LEFT);
        rightKey = keyHandler.addKey(KeyEvent.VK_RIGHT);
        jumpKey = keyHandler.addKey(KeyEvent.VK_SPACE,
                this::jump,
                ()->{});

    }
    public void setDefaultValues(){
        this.speed = 1;
    }

    private void jump(){
        System.out.println("Jump");
        rb.addForce(Vector2.up.mul(.25f));
    }
    @Override
    public void awake() {
        super.awake();
        rb = getGameObject().getComponent(Rigidbody.class);
    }
    public void update(){
        int xMovement= 0;
        if(leftKey.isHeld()){xMovement-=1;}
        if(rightKey.isHeld()){xMovement+=1;}
        rb.addForce(new Vector2(xMovement, 0).mul(speed*GamePanel.getDeltaTime()));
    }
    public void draw(Graphics2D g2d){
    }
}
