package ObjectSystem;

import Main.GamePanel;
import Main.Key;
import Main.KeyHandler;
import Utility.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Player extends Component{
    KeyHandler keyHandler;
    public float speed;
    Rigidbody rb;

    private Key leftKey, rightKey, aKey, dkey, jumpKey;

    public Player(KeyHandler keyHandler, float speed){
        this.keyHandler = keyHandler;
        this.speed = speed;
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


    private void jump(){
        rb.velocity = Vector2.zero;
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

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("keyHandler", new KeyHandler());
        defaultValues.put("speed",1);
        return defaultValues;
    }
}
