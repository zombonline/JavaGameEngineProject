package ObjectSystem.Crate.Behaviours;

import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.Player;
import ObjectSystem.PlayerComboTracker;
import ObjectSystem.Rigidbody;

import javax.sound.midi.VoiceStatus;
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

    @Override
    public void awake(Crate crate) {

    }

    @Override
    public void update(Crate crate) {

    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        if(other.getComponent(Player.class)==null){return;}
        if (other.getComponent(Rigidbody.class).velocityLastFrame.getY() < crate.requiredHitStrength) return;
        takeHit(other);
    }
    @Override
    public void onTouchBottom(Collider other, Crate crate) {
        if(other.getComponent(Player.class)==null){return;}
        if (other.getComponent(Rigidbody.class).velocityLastFrame.getY() > -crate.requiredHitStrength) return;
        takeHit(other);
    }

    @Override
    public void onExplosionNearby(Crate crate) {
    }

    private void takeHit(Collider other){
        if(!active){return;}
        currentHitPoints--;
        notifyHitTaken();
        if(currentHitPoints<=0){
            notifyHitsReachedZero();
        }
    }

    public interface HitCounterListener
    {
        void onHit(int current, int start);
        void onHitsReachedZero();
    }
    private List<HitCounterBehavior.HitCounterListener> listeners = new ArrayList<>();
    public void addListener(HitCounterBehavior.HitCounterListener listener) {
        listeners.add(listener);
    }
    public void removeListener(HitCounterBehavior.HitCounterListener listener) {
        listeners.remove(listener);
    }
    public void notifyHitTaken() {
        for (HitCounterBehavior.HitCounterListener listener : listeners) {
            listener.onHit(currentHitPoints,startingHitPoints);
        }
    }
    public void notifyHitsReachedZero(){
        for(HitCounterBehavior.HitCounterListener listener : listeners) {
            listener.onHitsReachedZero();
        }
    }

}
