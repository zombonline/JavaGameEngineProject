package game.components.player;

import core.asset.Assets;
import core.audio.SFXPlayer;
import core.utils.DebugText;
import core.utils.Vector2;
import core.input.Key;
import core.input.KeyHandler;
import game.components.Rigidbody;
import game.components.core.Component;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import main.GamePanel;

public class Player extends Component {
    //Component references
    private Rigidbody rb;
    private final KeyHandler keyHandler;

    //Variables
    private Key leftKey, rightKey, aKey, dKey, jumpKey, rKey;

    private boolean canMove = true;

    private final float speed;

    private final float jumpCoyoteTime;
    private float jumpCoyoteTimer;
    private final float jumpPressTime;
    private float jumpPressTimer;

    public Player(KeyHandler keyHandler, float speed, float jumpCoyoteTime, float jumpPressTime){
        this.keyHandler = keyHandler;
        this.speed = speed;
        this.jumpCoyoteTime = jumpCoyoteTime;
        this.jumpPressTime = jumpPressTime;
    }
    @Override
    public void awake() {
        super.awake();
        getRequiredComponentReferences();
        setUpControls(keyHandler);
    }

    @Override
    protected void getRequiredComponentReferences() {
        rb = fetchRequiredComponent(Rigidbody.class);
    }


    @Override
    public void update(){
        if(canMove) {
            handleHorizontalMovement();
            handleJump();
        }
        DebugText.logPermanently("Player Position", gameObject.getTransform().getPosition().toDp(2).toString());
        DebugText.logPermanently("Player Velocity", (getComponent(Rigidbody.class).velocity.toDp(2).toString()));
    }

    private void setUpControls(KeyHandler keyHandler) {
        leftKey = keyHandler.addKey(KeyEvent.VK_LEFT);
        rightKey = keyHandler.addKey(KeyEvent.VK_RIGHT);
        aKey = keyHandler.addKey(KeyEvent.VK_A);
        dKey = keyHandler.addKey(KeyEvent.VK_D);
        jumpKey = keyHandler.addKey(KeyEvent.VK_SPACE,
                this::jumpPress,
                ()->{});
        rKey = keyHandler.addKey(KeyEvent.VK_R,
                this::resetPlayerPress,
                ()->{});
    }

    //region Jump and movement logic
    private void jumpPress(){
        jumpPressTimer = jumpPressTime;
    }

    private void resetPlayerPress(){
        getComponent(PlayerDeathHandler.class).die();
    }

    private void handleJump() {
        if (rb.isGrounded()) {
            jumpCoyoteTimer = jumpCoyoteTime;
        }
        if (jumpCoyoteTimer > 0 && jumpPressTimer > 0 && rb.velocity.getY()>=0) {
            SFXPlayer.playSound(Assets.SFXClips.PLAYER_JUMP);
            rb.velocity.setY(0);
            rb.addForce(Vector2.up.mul(16));
            jumpPressTimer = 0;
            jumpCoyoteTimer = 0;
        }
        jumpPressTimer -= (float) GamePanel.getDeltaTime();
        jumpCoyoteTimer -= (float) GamePanel.getDeltaTime();
    }

    private void handleHorizontalMovement() {
        int xMovement = 0;
        if (leftKey.isHeld() || aKey.isHeld()) {
            xMovement -= 1;
        }
        if (rightKey.isHeld() || dKey.isHeld()) {
            xMovement += 1;
        }
        rb.addForce(new Vector2(xMovement, 0).mul(speed * GamePanel.getDeltaTime()));
    }
    //endregion

    //SETTERS
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("keyHandler", new KeyHandler());
        defaultValues.put("speed",1);
        defaultValues.put("jumpCoyoteTime",0.025f);
        defaultValues.put("jumpPressTime",0.125f);
        return defaultValues;
    }
}
