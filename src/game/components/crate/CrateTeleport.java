package game.components.crate;

import game.components.crate.behaviours.BounceBehavior;
import game.components.crate.behaviours.DestroyedByExplosionBehaviour;
import game.components.crate.core.Crate;

import java.util.List;

public class CrateTeleport extends Crate {
    public CrateTeleport(float bounceStrength) {
        super(true, List.of(
                new BounceBehavior(bounceStrength, true),
                new DestroyedByExplosionBehaviour()
        ));
    }
}
