package ObjectSystem.Crate.Behaviours;

import Main.DebugText;
import ObjectSystem.Collider;
import ObjectSystem.Crate.Crate;
import ObjectSystem.Rigidbody;
import Utility.Vector2;

public class BounceBehavior implements CrateBehavior {
    float bounceStrength;

    public BounceBehavior(float bounceStrength) {
        this.bounceStrength = bounceStrength;
    }

    @Override
    public void awake(Crate crate) {

    }

    @Override
    public void update(Crate crate) {

    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        if (!other.getGameObject().name.equals("Player")) return;
        if (other.getComponent(Rigidbody.class).velocityLastFrame.getY() < crate.requiredHitStrength) return;
        Rigidbody rb = other.getComponent(Rigidbody.class);
        rb.clearForces();
        rb.velocity.setY(0);
        rb.addForce(Vector2.up.mul(bounceStrength));
    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {
        if (!other.getGameObject().name.equals("Player")) return;
        if (other.getComponent(Rigidbody.class).velocityLastFrame.getY() > -crate.requiredHitStrength) return;
        Rigidbody rb = other.getComponent(Rigidbody.class);
        rb.clearForces();
        rb.velocity.setY(0);
        rb.addForce(Vector2.down.mul(0.25f));
    }

    @Override
    public void onExplosionNearby(Crate crate) {

    }
}
