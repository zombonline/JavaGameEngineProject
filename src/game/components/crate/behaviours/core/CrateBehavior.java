package game.components.crate.behaviours.core;

import game.components.Collider;
import game.components.crate.core.Crate;

public interface CrateBehavior {
    void awake(Crate crate);
    void update(Crate crate);
    void onTouchTop(Collider other, Crate crate);
    void onTouchBottom(Collider other, Crate crate);
    void onExplosionNearby(Crate crate);

}
