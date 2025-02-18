package Entity;

import Utility.Raycast;
import Utility.Vector2;
import com.sun.source.tree.NewArrayTree;

import java.util.ArrayList;

public class Rigidbody extends Component{
    public float mass;
    public float friction;
    public Vector2 velocity;
    float drag;
    public Vector2 angularVelocity;
    public Vector2 maxVelocity;
    public Vector2 maxAngularVelocity;
    public float restitution;
    public float gravityScale;
    private boolean isGrounded;

    public Rigidbody(){
        this.mass = 1;
        this.friction = 0.5f;
        this.velocity = Vector2.zero;
        this.drag = 0.9f;
        this.angularVelocity = Vector2.zero;
        this.maxVelocity = new Vector2(1,5);
        this.maxAngularVelocity = Vector2.zero;
        this.restitution = 0f;
        this.gravityScale = .01f;
    }

    public void update(){
        velocity = new Vector2(velocity.getX()*drag, velocity.getY());
        if(!checkGround()){
            velocity = velocity.add(Vector2.down.mul(gravityScale));
        }
        velocity = velocity.applyMax(maxVelocity);
        getGameObject().transform.translate(velocity);
    }

    public void handleCollision(Vector2 overlap, Transform other){
        Vector2 thisPos = getGameObject().transform.getPosition();
        Vector2 otherPos = other.getPosition();
        // Determine direction of collision
        float dx = thisPos.getX() - otherPos.getX();
        float dy = thisPos.getY() - otherPos.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            // X-axis collision (left/right)
            if (dx > 0) {
                // Player is to the right of the object
                thisPos.setX(thisPos.getX() + overlap.getX());
            } else {
                // Player is to the left of the object
                thisPos.setX(thisPos.getX() - overlap.getX());
            }
            velocity.setX(Math.abs(velocity.getX()) > 0.1 ? -velocity.getX() * restitution : 0);
        } else {
            // Y-axis collision (top/bottom)
            if (dy > 0) {
                // Player is below the object
                thisPos.setY(thisPos.getY() + overlap.getY());
            } else {
                // Player is above the object (standing on it)
                thisPos.setY(thisPos.getY() - overlap.getY());
            }
            velocity.setY(Math.abs(velocity.getY()) > 0.1 ? -velocity.getY() * restitution : 0);

        }
        getGameObject().transform.setPosition(thisPos);
    }
    public void addForce(Vector2 force){
        this.velocity = this.velocity.add(force);
    }
    public boolean checkGround(){
        Collider collider = getGameObject().getComponent(Collider.class);
        if(collider!= null){
            float x = collider.getColliderPosition().getX();
            float y = collider.getColliderPosition().getY() + collider.getColliderSize().getY()/2;
            Vector2 bottomPos = new Vector2(x,y);
            bottomPos = bottomPos.add(Vector2.down.mul(.025f));

            ArrayList<CollisionLayer> mask = new ArrayList<CollisionLayer>();
            mask.add(CollisionLayer.values()[0]);
            new Raycast(new Vector2(getGameObject().transform.getPosition().getX(),collider.getBounds().maxY+0.01f),.01f,90, 10);
            Collider collider1 = Collider.checkForColliderAtPoint(bottomPos, mask);
            if(collider1!=null){
                return true;
            }
        }
        return false;
    }

}
