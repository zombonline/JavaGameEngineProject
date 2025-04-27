package game.components.crate;


import core.asset.Assets;
import core.audio.SFXPlayer;
import core.scene.SessionManager;
import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.behaviours.HitCounterBehavior;
import game.components.crate.core.Crate;
import game.entities.GameObject;
import game.components.player.PlayerComboTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateBounce extends Crate {
    public CrateBounce(float bounceStrength, int hitsToDestroy) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, false),
                new HitCounterBehavior(hitsToDestroy),
                new DestroyedByExplosionBehaviour()
        ));

        getBehavior(HitCounterBehavior.class).addListener(
                new HitCounterBehavior.HitCounterListener() {
                    @Override
                    public void onHit(int current, int start) {
                        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
                        if(current>0){
                            SFXPlayer.playSound(Assets.SFXClips.CRATE_BOUNCE);
                        }
                    }

                    @Override
                    public void onHitsReachedZero() {
                        SFXPlayer.playSound(Assets.SFXClips.CRATE_DESTROYED);
                        GameObject.destroy(gameObject);
                    }
                }
        );
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 10f);
        defaultValues.put("hitsToDestroy",1);
        return defaultValues;
    }
}
