package ObjectSystem.Crate;

import ObjectSystem.Crate.Behaviours.BounceBehavior;
import ObjectSystem.Crate.Behaviours.CrateBehavior;
import ObjectSystem.Crate.Behaviours.DestroyedByExplosionBehaviour;
import ObjectSystem.Crate.Behaviours.HitCounterBehavior;

import java.util.List;

public class CrateTeleport extends Crate{
    public CrateTeleport(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, true),
                new DestroyedByExplosionBehaviour()
        ));
    }
}
