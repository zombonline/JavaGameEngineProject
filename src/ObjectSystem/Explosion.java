package ObjectSystem;

import Utility.Vector2;

import java.util.ArrayList;

public class Explosion extends Component{
    float scale = 3.5f;
    SpriteRenderer spriteRenderer;
    SpriteAnimator spriteAnimator;
    SpriteAnimator.AnimatorListener animatorListener;
    Collider collider;
    Collider.CollisionListener listener;

    ArrayList<GameObject> objectsInRange = new ArrayList<>();

    @Override
    public void awake() {
        super.awake();

        gameObject.transform.setScale(Vector2.one.mul(scale));

        spriteAnimator = getComponent(SpriteAnimator.class);
        animatorListener = new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {}
            @Override
            public void onAnimationComlete() {
                for(GameObject object : objectsInRange){
                    if(object.getComponent(Crate.class)!=null){
                        if(object.getComponent(CrateExplosive.class)!=null){
                            object.getComponent(CrateExplosive.class).ExplodeImmediate();
                        } else {
                            GameObject.destroy(object);
                        }
                    }
                }
                GameObject.destroy(gameObject);
            }
        };
        spriteAnimator.addListener(animatorListener);

        collider = getComponent(Collider.class);
        collider = getComponent(Collider.class);
        if(collider!=null){
            listener = new Collider.CollisionListener() {
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
    }

}
