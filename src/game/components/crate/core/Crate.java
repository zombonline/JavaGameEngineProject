package game.components.crate.core;

import core.scene.SessionManager;
import core.utils.DebugText;
import game.components.Collider;
import game.components.core.Component;
import game.components.crate.behaviours.core.CrateBehavior;
import game.components.Explosion;
import game.components.Rigidbody;


import java.util.List;

public class Crate extends Component implements Explosion.ExplosionListener {
    Collider collider;
    Collider.CollisionListener listener;
    boolean breakable, destroyed;
    public static float requiredHitStrength = 0.1f;

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
                public void onCollisionExit(Collider other) {
                }

                @Override
                public void onCollisionStay(Collider other) {}
            };
            collider.addListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        if(destroyed) return;   
        if(isBreakable()){
            destroyed = true;
            SessionManager.getCurrentLevel().incrementCratesDestroyed();
        }
    }

    public boolean isBreakable() {
        return breakable;
    }

    @Override
    public void onTriggered() {
        for(CrateBehavior b : behaviors) {b.onExplosionNearby(Crate.this);}
    }
    public static boolean checkVelocityValidBottom(Collider other, Crate crate) {
        boolean otherVelocityEnough = other.getComponent(Rigidbody.class).velocityLastFrame.getY() < -requiredHitStrength;
        boolean thisVelocityEnough = false;
        if(crate.hasComponent(Rigidbody.class)){
            thisVelocityEnough = crate.getComponent(Rigidbody.class).velocityLastFrame.getY() > requiredHitStrength;
        }
        return otherVelocityEnough || thisVelocityEnough;
    }
    public static boolean checkVelocityValidTop(Collider other, Crate crate) {
        boolean otherVelocityEnough = other.getComponent(Rigidbody.class).velocityLastFrame.getY() > requiredHitStrength;
        boolean thisVelocityEnough = false;
        if(crate.hasComponent(Rigidbody.class)) {
            thisVelocityEnough = crate.getComponent(Rigidbody.class).velocityLastFrame.getY() < -requiredHitStrength;
        }
        return otherVelocityEnough || thisVelocityEnough;
    }
}
