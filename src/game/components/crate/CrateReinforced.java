package game.components.crate;

import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.core.Crate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateReinforced extends Crate {
    public CrateReinforced(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, false),
                new DestroyedByExplosionBehaviour()
        ));
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 10f);
        return defaultValues;
    }
}
