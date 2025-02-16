package Entity;

import Utility.Vector2;

public class Rigidbody extends Component{
    public float mass;
    public float friction;
    public Vector2 velocity;
    public Vector2 angularVelocity;
    public Vector2 maxVelocity;
    public Vector2 maxAngularVelocity;
    public float restitution;
    public float gravityScale;

    public Rigidbody(){
        this.mass = 1;
        this.friction = 0.5f;
        this.velocity = Vector2.zero;
        this.angularVelocity = Vector2.zero;
        this.maxVelocity = new Vector2(1,5);
        this.maxAngularVelocity = Vector2.zero;
        this.restitution = 0.5f;
        this.gravityScale = 0f;
    }

    public void update(){

        velocity = velocity.add(Vector2.down.mul(gravityScale));
        velocity = velocity.applyMax(maxVelocity);

    }
}
