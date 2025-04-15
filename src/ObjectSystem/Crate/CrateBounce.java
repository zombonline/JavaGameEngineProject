package ObjectSystem.Crate;


import Main.GamePanel;
import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.DestroyedByExplosionBehaviour;
import ObjectSystem.Crate.Behaviours.ExplodeBehavior;
import ObjectSystem.Crate.Behaviours.HitCounterBehavior;
import ObjectSystem.GameObject;
import ObjectSystem.PlayerComboTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateBounce extends Crate {
    public CrateBounce(float bounceStrength, int hitsToDestroy) {
        super(true, List.of(
                new BounceBehavior(bounceStrength),
                new HitCounterBehavior(hitsToDestroy),
                new DestroyedByExplosionBehaviour()
        ));

        getBehavior(HitCounterBehavior.class).addListener(
                new HitCounterBehavior.HitCounterListener() {
                    @Override
                    public void onHit(int current, int start) {
                        GamePanel.currentLevel.getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
                    }

                    @Override
                    public void onHitsReachedZero() {
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
