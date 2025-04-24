package game.components.crate;


import core.scene.SessionManager;
import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.ExplodeBehavior;
import game.components.crate.core.Crate;
import game.components.player.PlayerComboTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateExplosive extends Crate {
    public CrateExplosive(float bounceStrength, float explosionScale) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, true),
                new ExplodeBehavior(explosionScale)
        ));
        getBehavior(ExplodeBehavior.class).addListener(
                new ExplodeBehavior.ExplodeListener() {

                    @Override
                    public void onTrigger() {
                        System.out.println("DISABLING BOUNCE");
                        getBehavior(BounceBehavior.class).active = false;
                    }

                    @Override
                    public void onExplode() {
                        SessionManager.getCurrentLevel().getObjectByName("Player").getComponent(PlayerComboTracker.class).onCrateHit();
                    }
                }
        );
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 20f);
        defaultValues.put("explosionScale", 3.2f);
        return defaultValues;
    }
}
