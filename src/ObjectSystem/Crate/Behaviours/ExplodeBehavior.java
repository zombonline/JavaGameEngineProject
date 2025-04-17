package ObjectSystem.Crate.Behaviours;


import Main.AssetLoader;
import Main.Assets;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.Explosion;
import ObjectSystem.SpriteAnimator;
import ObjectSystem.GameObject;

import java.util.ArrayList;
import java.util.List;

public class ExplodeBehavior implements CrateBehavior {
    boolean triggered = false;
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
        trigger(crate);
    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {
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
            public void onAnimationComlete() {
                explode(crate);
            }
        });
        animator.loadAnimation(Assets.Animations.CRATE_EXPLOSIVE_TRIGGER);
    }

    public void explode(Crate crate){
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
    private List<ExplodeBehavior.ExplodeListener> listeners = new ArrayList<>();
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
