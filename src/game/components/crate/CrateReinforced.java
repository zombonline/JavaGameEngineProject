package game.components.crate;

import core.asset.Assets;
import core.audio.SFXPlayer;
import game.components.Collider;
import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.behaviours.HitCounterBehavior;
import game.components.crate.core.Crate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateReinforced extends Crate {
    public CrateReinforced(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, false),
                new DestroyedByExplosionBehaviour(),
                new HitCounterBehavior(500)
        ));
    }

    @Override
    public void awake() {
        super.awake();
        setUpHitCounterBehaviourListener();
    }

    public void setUpHitCounterBehaviourListener(){
        getBehavior(HitCounterBehavior.class).addListener(
                new HitCounterBehavior.HitCounterListener() {
                    @Override
                    public void onHit(int current, int start, Collider other) {
                        SFXPlayer.playSound(Assets.SFXClips.CRATE_REINFORCED_BOUNCE);
                    }

                    @Override
                    public void onHitsReachedZero(Collider other) {

                    }
                }
        );
    }
    public void setUpDestroyedByExplosionListener(){
        getBehavior(DestroyedByExplosionBehaviour.class).addListener(
                new DestroyedByExplosionBehaviour.DestroyedByExplosionListener() {
                    @Override
                    public void onDestroyed() {
                        SFXPlayer.playSound(Assets.SFXClips.CRATE_REINFORCED_DESTROYED);
                    }
                }
        );
    }

    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 10f);
        return defaultValues;
    }
}
