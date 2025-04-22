package ObjectSystem.Crate;

import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.DestroyedByExplosionBehaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateReinforced extends Crate{
    public CrateReinforced(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength),
                new DestroyedByExplosionBehaviour()
        ));
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounceStrength", 10f);
        return defaultValues;
    }
}
