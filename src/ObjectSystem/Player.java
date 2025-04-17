package ObjectSystem;

import Main.*;
import Utility.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class Player extends Component{
    KeyHandler keyHandler;
    public float speed;
    Rigidbody rb;
    private Key leftKey, rightKey, aKey, dkey, jumpKey;
    Collider col;

    private boolean canMove = true;

    SpriteAnimator animator;
    public float coyoteTime = 0.025f, coyoteTimer;
    public float pressTime = 0.125f, pressTimer;

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
        pressTimer = pressTime;
    }
    @Override
    public void awake() {
        super.awake();
        rb = getComponent(Rigidbody.class);
        col = getComponent(Collider.class);
        animator = getComponent(SpriteAnimator.class);

    }
    public void update(){
        if(canMove) {


            int xMovement = 0;
            if (leftKey.isHeld() || aKey.isHeld()) {
                xMovement -= 1;
            }
            if (rightKey.isHeld() || dkey.isHeld()) {
                xMovement += 1;
            }
            rb.addForce(new Vector2(xMovement, 0).mul(speed * GamePanel.getDeltaTime()));
            if (rb.isGrounded()) {
                coyoteTimer = coyoteTime;
            }
            if (coyoteTimer > 0 && pressTimer > 0) {
                rb.velocity.setY(0);
                rb.addForce(Vector2.up.mul(16));
                pressTimer = 0;
                coyoteTimer = 0;
            }
            pressTimer -= GamePanel.getDeltaTime();
            coyoteTimer -= GamePanel.getDeltaTime();
        }
        DebugText.logPermanently("Player Position", gameObject.getTransform().getPosition().toDp(2).toString());
        DebugText.logPermanently("Player Velocity", (getComponent(Rigidbody.class).velocity.toDp(2).toString()));
    }
    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("keyHandler", new KeyHandler());
        defaultValues.put("speed",1);
        return defaultValues;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }
}
