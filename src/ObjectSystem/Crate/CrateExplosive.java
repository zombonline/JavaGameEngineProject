package ObjectSystem.Crate;


import Main.GamePanel;
import Main.SessionManager;
import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.ExplodeBehavior;
import ObjectSystem.PlayerComboTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateExplosive extends Crate {
    public CrateExplosive(float bounceStrength, float explosionScale) {
        super(true, List.of(
                new BounceBehavior(bounceStrength),
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
