package ObjectSystem.Crate;

import Main.DebugText;
import ObjectSystem.Collider;
import ObjectSystem.Component;
import ObjectSystem.Crate.Behaviours.CrateBehavior;

import java.util.List;

public class Crate extends Component {
    Collider collider;
    Collider.CollisionListener listener;
    boolean breakable;
    public float requiredHitStrength = .01f;

    protected List<CrateBehavior> behaviors;

    public Crate(boolean breakable, List<CrateBehavior> behaviors) {
        this.breakable = breakable;
        this.behaviors = behaviors;
    }

    public <T extends CrateBehavior> T getBehavior(Class<T> type) {
        for (CrateBehavior behavior : behaviors) {
            if (type.isInstance(behavior)) {
                return (T) behavior;
            }
        }
        return null;
    }

    @Override
    public void awake() {
        SetupCollisionEvents();
        for(CrateBehavior b : behaviors) {b.awake(Crate.this);}
    }

    public void update(){
        for(CrateBehavior b : behaviors) {b.update(Crate.this);}

    }

    public void onExplosionNearby(){
        for(CrateBehavior b : behaviors) {b.onExplosionNearby(Crate.this);}
    }

    private void SetupCollisionEvents() {
        collider = getComponent(Collider.class);
        if (collider != null) {
            listener = new Collider.CollisionListener() {
                @Override
                public void onCollisionEnter(Collider other) {
                    double otherBottom = Math.floor(other.getBounds().maxY * 10) / 10;
                    double colliderTop = Math.floor(collider.getBounds().minY * 10) / 10;
                    if (otherBottom <= colliderTop + 0.25f) {
                        for (CrateBehavior b : behaviors) {b.onTouchTop(other, Crate.this);}
                    }

                    double playerTop = Math.floor(other.getBounds().minY * 10) / 10;
                    double colliderBottom = Math.floor(collider.getBounds().maxY * 10) / 10;
                    if (playerTop >= colliderBottom - 0.25f) {
                        for (CrateBehavior b : behaviors) {b.onTouchBottom(other, Crate.this);}
                    }



                }

                @Override
                public void onCollisionExit(Collider other) {}

                @Override
                public void onCollisionStay(Collider other) {}
            };
            collider.addListener(listener);
        }
    }
}
