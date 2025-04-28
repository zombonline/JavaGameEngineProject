package game.components.crate.behaviours;


import core.asset.AssetLoader;
import core.asset.Assets;
import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.Explosion;
import game.components.crate.behaviours.core.CrateBehavior;
import game.components.rendering.SpriteAnimator;
import game.entities.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ExplodeBehavior implements CrateBehavior {
    boolean triggered = false;
    boolean exploded = false;
    public boolean active = true;
    float explosionScale;
    public ExplodeBehavior(float explosionScale) {
        this.explosionScale = explosionScale;
    }

    @Override
    public void awake(Crate crate) {

    }

    @Override
    public void update(Crate crate) {

    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        if(!Crate.checkVelocityValidTop(other,crate)) {return;}
        trigger(crate);
    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {
        if(!Crate.checkVelocityValidBottom(other,crate)) {return;}
        trigger(crate);
    }
    @Override
    public void onExplosionNearby(Crate crate){
        explode(crate);
    }

    private void trigger(Crate crate) {
        if(!active){return;}
        if (triggered) return;
        notifyTrigger();
        triggered = true;
        SpriteAnimator animator = crate.getComponent(SpriteAnimator.class);
        animator.addListener(new SpriteAnimator.AnimatorListener() {
            @Override
            public void onAnimationEvent(String eventKey) {}

            @Override
            public void onAnimationComplete() {
                explode(crate);
            }
        });
        animator.loadAnimation(Assets.Animations.CRATE_EXPLOSIVE_TRIGGER);
    }

    public void explode(Crate crate){
        if(exploded){return;}
        exploded=true;
        notifyExplode();
        GameObject explosion = AssetLoader.getInstance().getPrefab(Assets.Prefabs.EXPLOSION);
        explosion.getComponent(Explosion.class).setScale(explosionScale);
        explosion.getTransform().setPosition(crate.getGameObject().getTransform().getPosition());
        GameObject.destroy(crate.getGameObject());
    }

    public interface ExplodeListener {
        void onTrigger();
        void onExplode();
    }
    private final List<ExplodeBehavior.ExplodeListener> listeners = new ArrayList<>();
    public void addListener(ExplodeBehavior.ExplodeListener listener) {
        listeners.add(listener);
    }
    public void removeListener(ExplodeBehavior.ExplodeListener listener) {
        listeners.remove(listener);
    }
    public void notifyTrigger() {
        for (ExplodeBehavior.ExplodeListener listener : listeners) {
            listener.onTrigger();
        }
    }
    public void notifyExplode() {
        for (ExplodeBehavior.ExplodeListener listener : listeners) {
            listener.onExplode();
        }
    }

}
