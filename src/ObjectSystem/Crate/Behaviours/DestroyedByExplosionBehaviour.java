package ObjectSystem.Crate.Behaviours;

import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.GameObject;

public class DestroyedByExplosionBehaviour implements CrateBehavior{
    @Override
    public void awake(Crate crate) {

    }

    @Override
    public void update(Crate crate) {

    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {

    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {

    }

    @Override
    public void onExplosionNearby(Crate crate) {
        GameObject.destroy(crate.getGameObject());
    }
}
