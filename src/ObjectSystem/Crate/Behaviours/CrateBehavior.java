package ObjectSystem.Crate.Behaviours;

import Main.DebugText;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;

public interface CrateBehavior {
    void awake(Crate crate);
    void update(Crate crate);
    void onTouchTop(Collider other, Crate crate);
    void onTouchBottom(Collider other, Crate crate);
    void onExplosionNearby(Crate crate);

}
