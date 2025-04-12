package ObjectSystem.Crate.Behaviours;


import Main.Assets;
import Main.PrefabReader;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.SpriteAnimator;
import ObjectSystem.GameObject;

public class ExplodeBehavior implements CrateBehavior {
    boolean triggered = false;

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
        if (triggered) return;
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
        GameObject explosion = PrefabReader.getObject(Assets.Prefabs.EXPLOSION);
        explosion.transform.setPosition(crate.getGameObject().transform.getPosition());
        GameObject.destroy(crate.getGameObject());
    }
}
