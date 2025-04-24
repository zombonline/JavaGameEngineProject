package game.components.crate.behaviours;


import core.scene.SessionManager;
import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.crate.behaviours.core.CrateBehavior;
import game.entities.GameObject;
import game.components.player.PlayerComboTracker;

public class DestroyedByExplosionBehaviour implements CrateBehavior {
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
        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
        GameObject.destroy(crate.getGameObject());
    }
}
