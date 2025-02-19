package ObjectSystem;

import Main.GamePanel;
import Main.Key;
import Main.KeyHandler;
import Utility.Vector2;

import java.awt.event.KeyEvent;

public class Player extends Component{
    KeyHandler keyHandler;
    private float speed;
    Rigidbody rb;

    private Key leftKey, rightKey, aKey, dkey, jumpKey;

    public Player(KeyHandler keyHandler){
        this.keyHandler = keyHandler;
        setDefaultValues();
        setUpControls(keyHandler);
    }

    private void setUpControls(KeyHandler keyHandler) {
        leftKey = keyHandler.addKey(KeyEvent.VK_LEFT);
        rightKey = keyHandler.addKey(KeyEvent.VK_RIGHT);
        aKey = keyHandler.addKey(KeyEvent.VK_A);
        dkey = keyHandler.addKey(KeyEvent.VK_D);
        jumpKey = keyHandler.addKey(KeyEvent.VK_SPACE,
                this::jump,
                ()->{});
    }

    public void setDefaultValues(){
        speed = 1;
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
        if(leftKey.isHeld() || aKey.isHeld()){xMovement-=1;}
        if(rightKey.isHeld() || dkey.isHeld()){xMovement+=1;}
        rb.addForce(new Vector2(xMovement, 0).mul(speed*GamePanel.getDeltaTime()));
    }
}
