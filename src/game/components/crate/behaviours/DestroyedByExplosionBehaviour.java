package game.components.crate.behaviours;


import core.scene.SessionManager;
import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.crate.behaviours.core.CrateBehavior;
import game.entities.GameObject;
import game.components.player.PlayerComboTracker;

import java.util.ArrayList;
import java.util.List;

public class DestroyedByExplosionBehaviour implements CrateBehavior {
    @Override public void awake(Crate crate) {}
    @Override public void update(Crate crate) {}
    @Override public void onTouchTop(Collider other, Crate crate) {}
    @Override public void onTouchBottom(Collider other, Crate crate) {}

    @Override
    public void onExplosionNearby(Crate crate) {
        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
        notifyDestroyed();
        GameObject.destroy(crate.getGameObject());
    }

    public interface DestroyedByExplosionListener
    {
        void onDestroyed();
    }
    private final List<DestroyedByExplosionListener> listeners = new ArrayList<>();
    public void addListener(DestroyedByExplosionListener listener) {
        listeners.add(listener);
    }
    public void removeListener(HitCounterBehavior.HitCounterListener listener) {
        listeners.remove(listener);
    }
    public void notifyDestroyed(){
        for(DestroyedByExplosionListener listener : listeners) {
            listener.onDestroyed();
        }
    }
}
