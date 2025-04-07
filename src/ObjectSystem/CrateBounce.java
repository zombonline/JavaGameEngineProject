package ObjectSystem;

import Main.DebugText;
import Utility.Vector2;

import java.util.HashMap;
import java.util.Map;

public class CrateBounce extends Crate{
    float bounceStrength;
    public CrateBounce(float bounceStrength){
        super(true);
        this.bounceStrength = bounceStrength;
    }

    @Override
    public void awake() {
        super.awake();
    }

    @Override
    public void onCrateTouchTop(Collider other) {
        if(!other.gameObject.name.equals("Player")){ return; }
        DebugText.logTemporarily(" Player velocity: " + other.getComponent(Rigidbody.class).velocity.getY());
        if(other.getComponent(Rigidbody.class).velocity.getY()<0.1f){return;}
        other.getComponent(Rigidbody.class).velocity = Vector2.zero;
        other.getComponent(Rigidbody.class).addForce(Vector2.up.mul(bounceStrength));
        GameObject.destroy(gameObject);
    }

    @Override
    public void onCrateTouchBottom(Collider other) {
        if(!other.gameObject.name.equals("Player")){ return; }
        if(other.getComponent(Rigidbody.class).velocity.getY()>-0.1f){ return;}
        other.getComponent(Rigidbody.class).velocity = Vector2.zero;
        other.getComponent(Rigidbody.class).addForce(Vector2.down.mul(.25f));
        GameObject.destroy(gameObject);
    }

    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", .25f);
        return defaultValues;
    }
}
