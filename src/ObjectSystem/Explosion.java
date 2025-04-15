package ObjectSystem;

import Main.DebugText;
import ObjectSystem.Crate.Crate;
import Utility.Vector2;

import java.util.ArrayList;


public class Explosion extends Component{
    float scale = 3.25f;
    SpriteAnimator spriteAnimator;
    SpriteAnimator.AnimatorListener animatorListener;
    Collider collider;
    ArrayList<GameObject> objectsInRange = new ArrayList<>();
    @Override
    public void awake() {
        super.awake();
        collider = getComponent(Collider.class);
        if(collider!=null){
            Collider.CollisionListener listener = new Collider.CollisionListener() {
                @Override
                public void onCollisionEnter(Collider other) {
                    objectsInRange.add(other.gameObject);
                }
                @Override
                public void onCollisionExit(Collider other) {
                    objectsInRange.remove(other.gameObject);
                }
                @Override
                public void onCollisionStay(Collider other) {
                }
            };
            collider.addListener(listener);
        }

        spriteAnimator = getComponent(SpriteAnimator.class);
        animatorListener = new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {
                if(eventKey.equals("destroy")) {
                    DebugText.logTemporarily("Explosion destroying surrounding items");
                    destroyObjectsInRange();
                }
            }
            @Override
            public void onAnimationComlete() {
            }
        };
        spriteAnimator.addListener(animatorListener);
    }
    public void setScale(float scale){
        this.scale = scale;
        gameObject.transform.setScale(Vector2.one.mul(scale));
    }
    private void destroyObjectsInRange(){
        for (GameObject objectInRange : objectsInRange) {
            DebugText.logTemporarily(gameObject.name + " in range of explosion");
            if (objectInRange.getComponent(Crate.class) != null) {
                objectInRange.getComponent(Crate.class).onExplosionNearby();
            }
        }
        GameObject.destroy(gameObject);
    }
}
