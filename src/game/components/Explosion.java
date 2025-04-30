package game.components;

import core.asset.Assets;
import core.audio.SFXPlayer;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.rendering.SpriteAnimator;
import game.entities.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Explosion extends Component {
    private static final String DESTROY_EVENT_KEY = "destroy";

    float scale;
    private SpriteAnimator spriteAnimator;
    private Collider collider;
    private final List<GameObject> affectedObjects = new ArrayList<>();

    private SpriteAnimator.AnimatorListener animatorListener;
    private Collider.CollisionListener collisionListener;

    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpCollisionListener();
        setUpAnimatorListener();
        SFXPlayer.playSound(Assets.SFXClips.EXPLOSION);
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
            public void onAnimationComplete() {
                GameObject.destroy(gameObject);
            }
        };
        spriteAnimator.addListener(animatorListener);
    }

    private void setUpCollisionListener() {
        collisionListener = new Collider.CollisionListener() {
            @Override
            public void onCollisionEnter(Collider other, Vector2 contactNormal) {
                affectedObjects.add(other.getGameObject());
            }
            @Override
            public void onCollisionExit(Collider other, Vector2 contactNormal) {
                affectedObjects.remove(other.getGameObject());
            }
            @Override
            public void onCollisionStay(Collider other, Vector2 contactNormal) {
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
