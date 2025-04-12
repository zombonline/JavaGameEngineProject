package ObjectSystem;

import Main.DebugText;
import ObjectSystem.Crate.Crate;
import ObjectSystem.Crate.CrateExplosive;
import Utility.Vector2;

import java.util.ArrayList;

public class Explosion extends Component{
    float scale = 3.25f;
    SpriteAnimator spriteAnimator;
    SpriteAnimator.AnimatorListener animatorListener;

    @Override
    public void awake() {
        super.awake();
        gameObject.transform.setScale(Vector2.one.mul(scale));
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
    private void destroyObjectsInRange(){
        for (Collider collision : getComponent(Rigidbody.class).allCollisions) {
            DebugText.logTemporarily(collision.gameObject.name + " in range of explosion");
            if (collision.getComponent(Crate.class) != null) {
                collision.getComponent(Crate.class).onExplosionNearby();
            }
        }
        GameObject.destroy(gameObject);
    }
}
