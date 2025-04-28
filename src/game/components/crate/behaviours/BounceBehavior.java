package game.components.crate.behaviours;

import game.components.Collider;
import game.components.crate.core.Crate;
import game.components.crate.behaviours.core.CrateBehavior;
import game.components.player.Player;
import game.components.Rigidbody;
import core.utils.Vector2;

public class BounceBehavior implements CrateBehavior {
    private float bounceStrength;
    private boolean bouncePlayerOnly;
    public boolean active = true;

    public BounceBehavior(float bounceStrength, boolean bouncePlayerOnly) {
        this.bounceStrength = bounceStrength;
        this.bouncePlayerOnly = bouncePlayerOnly;
    }

    @Override
    public void awake(Crate crate) {

    }

    @Override
    public void update(Crate crate) {

    }

    @Override
    public void onTouchTop(Collider other, Crate crate) {
        if(!active){return;}
        if(bouncePlayerOnly && !other.hasComponent(Player.class)) {return;}
        if(!other.hasComponent(Rigidbody.class)){return;}
        if(!Crate.checkVelocityValidTop(other, crate)){return;}
        Rigidbody rb = other.getComponent(Rigidbody.class);
        rb.clearForces();
        rb.velocity.setY(0);
        rb.addForce(Vector2.up.mul(bounceStrength));
    }

    @Override
    public void onTouchBottom(Collider other, Crate crate) {
        if(!active){return;}
        if(bouncePlayerOnly && !other.hasComponent(Player.class)) {return;}
        if(!other.hasComponent(Rigidbody.class)){return;}
        if(!Crate.checkVelocityValidBottom(other, crate)){return;}
        Rigidbody rb = other.getComponent(Rigidbody.class);
        rb.clearForces();
        rb.velocity.setY(0);
        rb.addForce(Vector2.down.mul(0.25f));
    }

    @Override
    public void onExplosionNearby(Crate crate) {

    }


}
