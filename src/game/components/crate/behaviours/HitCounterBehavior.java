package game.components.crate.behaviours;

import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.crate.behaviours.core.CrateBehavior;
import game.components.player.Player;

import java.util.ArrayList;
import java.util.List;

public class HitCounterBehavior implements CrateBehavior {
    int currentHitPoints;
    int startingHitPoints;
    public boolean active = true;

    public HitCounterBehavior(int startingHitPoints) {
        this.startingHitPoints = startingHitPoints;
        this.currentHitPoints = startingHitPoints;
    }

    @Override public void awake(Crate crate) {}
    @Override public void update(Crate crate) {}

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        if(other.getComponent(Player.class)==null){return;}
        if (!Crate.checkVelocityValidTop(other, crate)) {
            return;
        }
        takeHit(other);
    }
    @Override
    public void onTouchBottom(Collider other, Crate crate) {
        if(other.getComponent(Player.class)==null){return;}
        if(!Crate.checkVelocityValidBottom(other, crate)) {
            return;
        }
        takeHit(other);
    }

    @Override public void onExplosionNearby(Crate crate) {}

    private void takeHit(Collider other){
        if(!active){return;}
        currentHitPoints--;
        notifyHitTaken(other);
        if(currentHitPoints<=0){
            notifyHitsReachedZero(other);
        }
    }

    public interface HitCounterListener
    {
        void onHit(int current, int start, Collider other);
        void onHitsReachedZero(Collider other);
    }
    private List<HitCounterBehavior.HitCounterListener> listeners = new ArrayList<>();
    public void addListener(HitCounterBehavior.HitCounterListener listener) {
        listeners.add(listener);
    }
    public void removeListener(HitCounterBehavior.HitCounterListener listener) {
        listeners.remove(listener);
    }
    public void notifyHitTaken(Collider other) {
        for (HitCounterBehavior.HitCounterListener listener : listeners) {
            listener.onHit(currentHitPoints,startingHitPoints,other);
        }
    }
    public void notifyHitsReachedZero(Collider other) {
        for(HitCounterBehavior.HitCounterListener listener : listeners) {
            listener.onHitsReachedZero(other);
        }
    }

}
