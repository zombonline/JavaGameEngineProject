package ObjectSystem;

import Main.GamePanel;
import Main.Key;
import Main.KeyHandler;
import Utility.CollisionLayer;
import Utility.Raycast;
import Utility.Vector2;
import Main.DebugText;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Player extends Component{
    KeyHandler keyHandler;
    public float speed;
    Rigidbody rb;
    private Key leftKey, rightKey, aKey, dkey, jumpKey;
    Collider col;

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
        if(!isGrounded()){return;}
        rb.velocity.setY(0);
        rb.addForce(Vector2.up.mul(.25f));
    }
    @Override
    public void awake() {
        super.awake();
        rb = getComponent(Rigidbody.class);
        col = getComponent(Collider.class);
    }
    public void update(){
        int xMovement= 0;
        if(leftKey.isHeld() || aKey.isHeld()){xMovement-=1;}
        if(rightKey.isHeld() || dkey.isHeld()){xMovement+=1;}
        rb.addForce(new Vector2(xMovement, 0).mul(speed*GamePanel.getDeltaTime()));


        DebugText.logPermanently("Player Position", gameObject.transform.getPosition().toString());
        DebugText.logPermanently("Player Velocity", (getComponent(Rigidbody.class).velocity.mul(1000).truncate().div(1000)).toString());

    }
    public boolean isGrounded(){
        Vector2 rayOrigin1 = new Vector2(col.getBounds().minX,col.getBounds().maxY+0.1f);
        Vector2 rayOrigin2 = new Vector2(col.getBounds().maxX, col.getBounds().maxY+0.1f);
        ArrayList<CollisionLayer> mask = new ArrayList<CollisionLayer>();
        mask.add(CollisionLayer.DEFAULT);
        Raycast raycast1 = new Raycast(rayOrigin1,.1f,0,10, mask);
        Raycast raycast2 = new Raycast(rayOrigin2,.1f,0,10, mask);
        return raycast1.checkForCollision() != null || raycast2.checkForCollision() != null;
    }

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("keyHandler", new KeyHandler());
        defaultValues.put("speed",1);
        return defaultValues;
    }
}
