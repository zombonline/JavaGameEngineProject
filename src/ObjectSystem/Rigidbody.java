package ObjectSystem;

import Utility.CollisionLayer;
import Utility.Raycast;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

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

    Collider rbCollider;

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

    @Override
    public void awake() {
        rbCollider = getComponent(Collider.class);
        System.out.println(rbCollider == null);
    }

    public void update(){
        velocity = new Vector2(velocity.getX()*drag, velocity.getY());
        velocity = velocity.add(Vector2.down.mul(gravityScale));
        velocity = velocity.applyMax(maxVelocity);
        getGameObject().transform.translate(velocity);
    }

    public void handleCollisions(List<Collider> colliders){
        if(colliders.isEmpty()){return;}
        Vector2 maxOverlap = new Vector2(0, 0);
        Collider strongestCollider = null;

        for (Collider c : colliders) {
            Vector2 o = rbCollider.getOverlap(c);
            if (o.getX() > maxOverlap.getX() || o.getY() > maxOverlap.getY()) {
                maxOverlap = o;
                strongestCollider = c;
            }
        }
        handleCollision(strongestCollider);
    }

    public void handleCollision(Collider other){
        Vector2 thisPos = getGameObject().transform.getPosition();
        Vector2 otherPos = other.getGameObject().transform.getPosition();
        Vector2 overlap = rbCollider.getOverlap(other);
        if(overlap.getX()<=0 || overlap.getY() <= 0){return;}
        System.out.println(overlap);
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
        System.out.println("Corrected overlap is now: " + rbCollider.getOverlap(other) );
    }
    public void addForce(Vector2 force){
        this.velocity = this.velocity.add(force);
    }
//    public boolean checkGround(){
////        Collider collider = getGameObject().getComponent(Collider.class);
////        if(collider!= null){
////            float x = collider.getColliderPosition().getX();
////            float y = collider.getBounds().maxY;
////            Vector2 rayPoint = new Vector2(x,y).add(Vector2.down.mul(.01f));
////            ArrayList<CollisionLayer> mask = new ArrayList<CollisionLayer>();
////            mask.add(CollisionLayer.values()[0]);
////            Raycast raycast = new Raycast(rayPoint,.01f,90, 10, mask);
////            Collider collider1 = raycast.checkForCollision();
////            if(collider1!=null){
////                return true;
////            }
////        }
//        return false;
//    }

}
