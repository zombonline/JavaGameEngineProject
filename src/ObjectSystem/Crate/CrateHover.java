package ObjectSystem.Crate;

import Main.GamePanel;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.DestroyedByExplosionBehaviour;
import ObjectSystem.Crate.Behaviours.HitCounterBehavior;
import ObjectSystem.Crate.Behaviours.HoverBehavior;
import ObjectSystem.GameObject;
import ObjectSystem.PlayerComboTracker;
import ObjectSystem.Rigidbody;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateHover extends Crate{

    public CrateHover(float bounceStrength, int hitsToDestroy, float hoverSpeed, float hoverDistance ){
        super(true, List.of(
                new HoverBehavior(hoverSpeed, hoverDistance),
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
        defaultValues.put("hitsToDestroy",5);
        defaultValues.put("hoverSpeed", 0.4f);
        defaultValues.put("hoverDistance", 0.5f);
        return defaultValues;
    }

}
