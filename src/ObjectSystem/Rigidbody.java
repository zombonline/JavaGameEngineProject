package ObjectSystem;
import Utility.CollisionLayer;
import Utility.Raycast;
import Utility.Vector2;

import java.util.*;

public class Rigidbody extends Component{
    public float mass;
    public Vector2 velocity;
    float drag;
    public Vector2 angularVelocity;
    public Vector2 maxVelocity;
    public Vector2 maxAngularVelocity;
    public float restitution;
    public float gravityScale;
    private List<Vector2> forces = new ArrayList<>();
    ArrayList<Collider> allCollisionsLastFrame = new ArrayList<Collider>();

    private boolean isKinematic;

    public void updateTouchingColliders(){
        ArrayList<Collider> allCollisions = new ArrayList<Collider>();
        allCollisions.addAll(below);
        allCollisions.addAll(left);
        allCollisions.addAll(right);
        allCollisions.addAll(above);
        allCollisions = new ArrayList<>(new HashSet<>(allCollisions));

        for(Collider collider : allCollisions){
            if(!allCollisionsLastFrame.contains(collider)){
                collider.notifyCollisionEnter(rbCollider);
            }
            collider.notifyCollisionStay(rbCollider);
        }
        for(Collider collider : allCollisionsLastFrame){
            if(!allCollisions.contains(collider)){
                collider.notifyCollisionExit(rbCollider);
            }
        }
        allCollisionsLastFrame = new ArrayList<>(allCollisions);
    }

    ArrayList<Collider> above = new ArrayList<Collider>(),
            below = new ArrayList<Collider>(),
            left = new ArrayList<Collider>(),
            right = new ArrayList<Collider>();
    Collider rbCollider;

    public Rigidbody(float drag, float gravityScale, float restitution, Vector2 maxVelocity, boolean isKinematic) {
        this.drag = drag;
        this.gravityScale = gravityScale;
        this.restitution = restitution;
        this.maxVelocity = maxVelocity;
        this.velocity = Vector2.zero;
        this.isKinematic = isKinematic;
    }

    @Override
    public void awake() {
        rbCollider = getComponent(Collider.class);
    }

    public void update(){
        for (Vector2 force : forces) {
            velocity = velocity.add(force);
        }
        forces.clear();
        velocity = new Vector2(velocity.getX()*drag, velocity.getY());
        if(!isGrounded()){velocity = velocity.add(Vector2.down.mul(gravityScale));}
        if(maxVelocity != null){
            if(Math.abs(velocity.getX()) > maxVelocity.getX()){
                velocity.setX(Math.signum(velocity.getX())*maxVelocity.getX());
            }
            if(Math.abs(velocity.getY()) > maxVelocity.getY()){
                velocity.setY(Math.signum(velocity.getY())*maxVelocity.getY());
            }
        }
        CheckForNewCollider(Vector2.down);
        CheckForNewCollider(Vector2.up);
        CheckForNewCollider(Vector2.left);
        CheckForNewCollider(Vector2.right);
        updateTouchingColliders();
        if(isKinematic){return;}
        getGameObject().transform.translate(velocity);

    }

    public boolean isGrounded(){
        Vector2 rayOrigin1 = new Vector2(rbCollider.getBounds().minX+0.1f,rbCollider.getBounds().maxY+0.1f);
        Vector2 rayOrigin2 = new Vector2(rbCollider.getBounds().maxX-0.1f, rbCollider.getBounds().maxY+0.1f);
        ArrayList<CollisionLayer> mask = new ArrayList<CollisionLayer>();
        mask.add(CollisionLayer.DEFAULT);
        Raycast raycast1 = new Raycast(rayOrigin1,.1f,0,10, mask);
        Raycast raycast2 = new Raycast(rayOrigin2,.1f,0,10, mask);
        return raycast1.checkForCollision() != null || raycast2.checkForCollision() != null;
    }

    private void CheckForNewCollider(Vector2 direction) {
        Vector2 halfSize = rbCollider.getColliderSize().div(2);
        Vector2 pos = gameObject.transform.getPosition();
        Vector2 perp = new Vector2(-direction.getY(), direction.getX()).mul(halfSize).mul(0.8f);

        Vector2 rayOrigin1 = pos.add(direction.mul(halfSize.getMag() - .01f)).add(perp);
        Vector2 rayOrigin2 = pos.add(direction.mul(halfSize.getMag() - 0.1f)).sub(perp);
        float angle = (float) Math.toDegrees(Math.atan2(direction.getX(), direction.getY()));

        ArrayList<Raycast> rays = new ArrayList<Raycast>();
        rays.add(new Raycast(rayOrigin1, .0001f, angle, 30, rbCollider.getCollisionMask()));
        rays.add(new Raycast(rayOrigin2, .0001f, angle, 30, rbCollider.getCollisionMask()));

        ArrayList<Collider> hits = new ArrayList<>();
        for(Raycast ray :rays){
            Collider hit = ray.checkForCollision();
            if (hit!=null){
                hits.add(hit);
            }
        }
        if (hits.isEmpty()) {
            clearColliders(direction);
            return;
        }
        boolean stopVelocity = true;
        for(Collider hit : hits) {
            switch ((int) direction.toAngle()) {
                case 0:
                    below.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit, below)){stopVelocity = false;}
                    break;
                case 90:
                    right.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit, right)){stopVelocity = false;}
                    break;
                case -90:
                    left.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit, left)){stopVelocity = false;}
                    break;
                case 180:
                    above.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit, above)){stopVelocity = false;}
                    break;
            }
        }
        if(stopVelocity && !isKinematic){
            hits.removeIf(Collider::isTrigger);
            if(hits.isEmpty()){return;}
            float overlap;
            switch ((int) direction.toAngle()) {
                case 0:
                    overlap = getGameObject().transform.getPosition().getY() + halfSize.getY() - hits.getFirst().getBounds().minY;
                    if(velocity.getY() > 0 && overlap>=0){velocity.setY(0);}
                    break;
                case 90:
                    overlap = getGameObject().transform.getPosition().getX() + halfSize.getX() - hits.getFirst().getBounds().minX;
                    if(velocity.getX() > 0 && overlap>= 0){velocity.setX(0);}
                    break;
                case -90:
                    overlap = hits.getFirst().getBounds().maxX - (getGameObject().transform.getPosition().getX() - halfSize.getX());
                    if (velocity.getX() < 0 && overlap >= 0) { velocity.setX(0); }
                    break;
                case 180:
                    overlap = hits.getFirst().getBounds().maxY - (getGameObject().transform.getPosition().getY() - halfSize.getY());
                    if (velocity.getY() < 0 && overlap >= 0) { addForce(new Vector2(0, -velocity.getY())); }
                    break;
            }
        }

    }

    private boolean isInlist(Collider hit, ArrayList<Collider> hits) {
        if (!hits.contains(hit)) {
            hits.add(hit);
            return false;
        }
        return true;
    }
    private void clearColliders(Vector2 direction) {
        switch ((int) direction.toAngle()) {
            case 0: below.clear(); break;
            case 90: right.clear(); break;
            case -90: left.clear(); break;
            case 180: above.clear(); break;
        }
    }

    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("drag",0.9f);
        defaultValues.put("restitution", 0f);
        defaultValues.put("gravityScale", 0.01f);
        defaultValues.put("maxVelocity", null);
        defaultValues.put("isKinematic", false);
        return defaultValues;
    }

    public void handleCollisions(List<Collider> colliders){
        colliders.removeIf(Collider::isTrigger);

        if(colliders.isEmpty()){return;}
        Vector2 maxOverlap = new Vector2(0, 0);
        Collider strongestCollider = colliders.getFirst();
        for (Collider c : colliders) {
            Vector2 o = rbCollider.getOverlap(c);
            if (o.getMag() > maxOverlap.getMag()) {
                maxOverlap = o;
                strongestCollider = c;
            }
        }
        handleCollision(strongestCollider);
    }

    public void handleCollision(Collider other){
        if(isKinematic){return;}
        Vector2 thisPos = getGameObject().transform.getPosition();
        Vector2 otherPos = other.getGameObject().transform.getPosition();
        Vector2 overlap = rbCollider.getOverlap(other);
        float dx = thisPos.getX() - otherPos.getX();
        float dy = thisPos.getY() - otherPos.getY();
        if (overlap.getX() < overlap.getY()) {
            // X-axis collision (left/right)
            if (dx > 0) {
                // Player is to the right of the object
                thisPos.setX(thisPos.getX() + overlap.getX());
            } else {
                // Player is to the left of the object
                thisPos.setX(thisPos.getX() - overlap.getX());
            }
        } else {
            // Y-axis collision (top/bottom)
            if (dy > 0) {
                // Player is below the object
                thisPos.setY(thisPos.getY() + overlap.getY());
            } else {
                // Player is above the object (standing on it)
                thisPos.setY(thisPos.getY() - overlap.getY());
            }
        }
        getGameObject().transform.setPosition(thisPos);
    }
    public void addForce(Vector2 force) {
        forces.add(force);  // Store forces instead of applying them immediately
    }

}
