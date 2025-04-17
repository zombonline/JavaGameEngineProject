package ObjectSystem;

import Main.GamePanel;
import ObjectSystem.Crate.Crate;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Explosion extends Component{
    private static final String DESTROY_EVENT_KEY = "destroy";

    float scale;
    private SpriteAnimator spriteAnimator;
    private Collider collider;
    private List<GameObject> affectedObjects = new ArrayList<>();

    private SpriteAnimator.AnimatorListener animatorListener;
    private Collider.CollisionListener collisionListener;

    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpCollisionListener();
        setUpAnimatorListener();
    }

    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
        spriteAnimator = fetchRequiredComponent(SpriteAnimator.class);
    }

    private void setUpAnimatorListener() {
        animatorListener = new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {
                if(eventKey.equals(DESTROY_EVENT_KEY)) {
                    destroyObjectsInRange();
                }
            }
            @Override
            public void onAnimationComlete() {
            }
        };
        spriteAnimator.addListener(animatorListener);
    }

    private void setUpCollisionListener() {
        collisionListener = new Collider.CollisionListener() {
            @Override
            public void onCollisionEnter(Collider other) {
                affectedObjects.add(other.gameObject);
            }
            @Override
            public void onCollisionExit(Collider other) {
                affectedObjects.remove(other.gameObject);
            }
            @Override
            public void onCollisionStay(Collider other) {
            }
        };
        collider.addListener(collisionListener);
    }

    public void setScale(float scale){
        this.scale = scale;
        gameObject.getTransform().setScale(Vector2.one.mul(scale));
    }
    private void destroyObjectsInRange(){
        for (GameObject obj : affectedObjects) {
            for(Component c : obj.getAllComponents()){
                if(c instanceof ExplosionListener){
                    ((ExplosionListener) c).onTriggered();
                }
            }
        }
        GameObject.destroy(gameObject);
    }

    @Override
    public void onDestroy() {
        collider.removeListener(collisionListener);
        spriteAnimator.removeListener(animatorListener);
    }
    public interface ExplosionListener{
        void onTriggered();
    }
}
