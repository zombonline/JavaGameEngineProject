package ObjectSystem.Crate;


import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.ExplodeBehavior;

import java.util.List;

public class CrateExplosive extends Crate {
    public CrateExplosive(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength),
                new ExplodeBehavior()
        ));
    }
}
