package game.components.crate.core;

import core.asset.AssetLoader;
import core.asset.Assets;
import core.asset.PrefabReader;
import core.scene.SessionManager;
import core.utils.Vector2;
import game.components.Collider;
import game.components.core.Component;
import game.components.crate.behaviours.core.CrateBehavior;
import game.components.Explosion;
import game.components.Rigidbody;
import game.entities.GameObject;


import java.util.ArrayList;
import java.util.List;

public class Crate extends Component implements Explosion.ExplosionListener {
    Collider collider;
    Collider.CollisionListener listener;
    final boolean breakable;
    boolean destroyed;
    public static final float requiredHitStrength = 0.00001f;

    protected final List<CrateBehavior> behaviors;

    public Crate(boolean breakable, List<CrateBehavior> behaviors) {
        this.breakable = breakable;
        this.behaviors = behaviors;
    }
    public Crate(){
        this.breakable = false;
        this.behaviors = new ArrayList<>();
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
        if(hasComponent(Rigidbody.class)){
            displayInteractableSprite();
        }
    }

    @Override
    public void update(){
        for(CrateBehavior b : behaviors) {b.update(Crate.this);}

    }

    private void displayInteractableSprite(){
        if(hasComponent(Rigidbody.class)){
            if(getComponent(Rigidbody.class).isKinematic()) return;
            GameObject glow = AssetLoader.getInstance().getPrefab(Assets.Prefabs.CRATE_INTERACTABLE_GLOW);
            glow.getTransform().setPosition(getGameObject().getTransform().getPosition());
            gameObject.getTransform().addChild(glow.getTransform());
        }
    }

    private void SetupCollisionEvents() {
        collider = getComponent(Collider.class);
        if (collider != null) {
            listener = new Collider.CollisionListener() {
                @Override
                public void onCollisionEnter(Collider other, Vector2 contactNormal) {
                    if(contactNormal.getY() >0){
                        for (CrateBehavior b : behaviors) {b.onTouchTop(other, Crate.this);}
                    } else if(contactNormal.getY()<0) {
                        for (CrateBehavior b : behaviors) {b.onTouchBottom(other, Crate.this);}
                    }
                }

                @Override
                public void onCollisionExit(Collider other, Vector2 contactNormal) {
                }

                @Override
                public void onCollisionStay(Collider other, Vector2 contactNormal) {}
            };
            collider.addListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        if(destroyed) return;   
        if(isBreakable()){
            GameObject crateBreak = AssetLoader.getInstance().getPrefab(Assets.Prefabs.CRATE_BREAK_BROWN);
            crateBreak.getTransform().setPosition(getGameObject().getTransform().getPosition());
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
        if(!other.hasComponent(Rigidbody.class)){return false;}
        boolean otherVelocityEnough = other.getComponent(Rigidbody.class).velocityLastFrame.getY() < -requiredHitStrength;
        boolean thisVelocityEnough = false;
        if(crate.hasComponent(Rigidbody.class)){
            thisVelocityEnough = crate.getComponent(Rigidbody.class).velocityLastFrame.getY() > requiredHitStrength;
        }
        return otherVelocityEnough || thisVelocityEnough;
    }
    public static boolean checkVelocityValidTop(Collider other, Crate crate) {
        if(!other.hasComponent(Rigidbody.class)){return false;}
        boolean otherVelocityEnough = other.getComponent(Rigidbody.class).velocityLastFrame.getY() > requiredHitStrength;
        boolean thisVelocityEnough = false;
        if(crate.hasComponent(Rigidbody.class)) {
            thisVelocityEnough = crate.getComponent(Rigidbody.class).velocityLastFrame.getY() < -requiredHitStrength;
        }
        return otherVelocityEnough || thisVelocityEnough;
    }
}
