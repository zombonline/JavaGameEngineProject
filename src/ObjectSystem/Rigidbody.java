package ObjectSystem;
import Main.DebugText;
import Main.GamePanel;
import Main.Main;
import Utility.CollisionLayer;
import Utility.Raycast;
import Utility.Vector2;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Rigidbody extends Component{
    public float mass;
    public Vector2 velocity, velocityLastFrame;
    float drag;
    public Vector2 angularVelocity;
    public Vector2 maxVelocity;
    public Vector2 maxAngularVelocity;
    public float restitution;
    public float gravityScale;
    private List<Vector2> forces = new ArrayList<>();
    ArrayList<Collider> allCollisions = new ArrayList<>(), allCollisionsLastFrame = new ArrayList<Collider>();
    private boolean isKinematic;

    public void updateTouchingColliders(){

        allCollisions = new ArrayList<>(new HashSet<>(allCollisions));

        for(Collider collider : allCollisions){
            if(!allCollisionsLastFrame.contains(collider)){
                DebugText.logTemporarily("Calling collision enter on " + collider.gameObject.name + " from " + gameObject.name);
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
        if(isKinematic){
            updateTouchingColliders();
            return;
        }
        velocityLastFrame = velocity;
        for (Vector2 force : forces) {
            velocity = velocity.add(force);
        }
        forces.clear();
        double horizontalAfterDrag = velocity.getX() * 1/(1+drag * GamePanel.getDeltaTime());
        velocity = new Vector2(horizontalAfterDrag, velocity.getY());
        if(!isGrounded()){
            velocity = velocity.add(Vector2.down.mul(gravityScale));
        }

        if(maxVelocity != null){
            if(Math.abs(velocity.getX()) > maxVelocity.getX()){
                velocity.setX(Math.signum(velocity.getX())*maxVelocity.getX());
            }
            if(Math.abs(velocity.getY()) > maxVelocity.getY()){
                velocity.setY(Math.signum(velocity.getY())*maxVelocity.getY());
            }
        }
        limitVelocity(Vector2.down);
        limitVelocity(Vector2.right);
        limitVelocity(Vector2.left);
        limitVelocity(Vector2.up);
        if(isKinematic){return;}
        getGameObject().transform.translate(velocity.div(60));
    }

    public boolean isGrounded(){
        Vector2 rayOrigin1 = new Vector2(rbCollider.getBounds().minX+0.2f,rbCollider.getBounds().maxY+0.001f);
        Vector2 rayOrigin2 = new Vector2(rbCollider.getBounds().maxX-0.2f, rbCollider.getBounds().maxY+0.001f);
        ArrayList<CollisionLayer> mask = new ArrayList<CollisionLayer>();
        mask.add(CollisionLayer.DEFAULT);
        Raycast raycast1 = new Raycast(rayOrigin1,.0001f,0,10, mask);
        Raycast raycast2 = new Raycast(rayOrigin2,.0001f,0,10, mask);
        boolean result = raycast1.checkForCollision() != null || raycast2.checkForCollision() != null;
        DebugText.logPermanently("Player Rigidbody Grounded", Boolean.toString(result));
        return raycast1.checkForCollision() != null || raycast2.checkForCollision() != null;
    }

    private void CheckForNewCollider(Vector2 direction) {
        Vector2 halfSize = rbCollider.getColliderSize().div(2);
        Vector2 pos = gameObject.transform.getPosition();
        Vector2 perp = new Vector2(-direction.getY(), direction.getX()).mul(halfSize).mul(0.8f);

        Vector2 rayOrigin1 = pos.add(direction.mul(halfSize.getMag() - .01f)).add(perp);
        Vector2 rayOrigin2 = pos.add(direction.mul(halfSize.getMag() - 0.01f)).sub(perp);
        float angle = (float) Math.toDegrees(Math.atan2(direction.getX(), direction.getY()));

        //ray length is based on the unsigned velocity in x or y
//        float rayLength = (int) direction.getX() == 0 ? Math.abs(velocity.getY()) : Math.abs(velocity.getX());

        ArrayList<Raycast> rays = new ArrayList<Raycast>();
        rays.add(new Raycast(rayOrigin1, .0001f, angle, 5, rbCollider.getCollisionMask()));
        rays.add(new Raycast(rayOrigin2, .0001f, angle, 5, rbCollider.getCollisionMask()));

        ArrayList<Raycast.Hit> hits = new ArrayList<>();
        for(Raycast ray :rays){
            Raycast.Hit hit = ray.checkForCollision();
            if (hit!=null && !hit.getCollider().isTrigger()){
                hits.add(hit);

            }
        }
        if (hits.isEmpty()) {
            clearColliders(direction);
            return;
        }
        boolean stopVelocity = true;
        for(Raycast.Hit hit : hits) {
            switch ((int) direction.toAngle()) {
                case 0:
                    below.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit.getCollider(), below)){stopVelocity = false;}
                    break;
                case 90:
                    right.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit.getCollider(), right)){stopVelocity = false;}
                    break;
                case -90:
                    left.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit.getCollider(), left)){stopVelocity = false;}
                    break;
                case 180:
                    above.removeIf(collider -> !hits.contains(collider));
                    if (!isInlist(hit.getCollider(), above)){stopVelocity = false;}
                    break;
            }
        }
        if(stopVelocity && !isKinematic){
            if(hits.isEmpty()){return;}
            float overlap;
            switch ((int) direction.toAngle()) {
                case 0:
                    overlap = getGameObject().transform.getPosition().getY() + halfSize.getY() - hits.getFirst().getCollider().getBounds().minY;
                    if(velocity.getY() > 0 && overlap>=0){
                        addForce(new Vector2(0, -velocity.getY()));
                        DebugText.logTemporarily("Pushing velocity upwards as something under");
                    }
                    break;
                case 90:
                    overlap = getGameObject().transform.getPosition().getX() + halfSize.getX() - hits.getFirst().getCollider().getBounds().minX;
                    if(velocity.getX() > 0 && overlap>= 0){velocity.setX(0);}
                    break;
                case -90:
                    overlap = hits.getFirst().getCollider().getBounds().maxX - (getGameObject().transform.getPosition().getX() - halfSize.getX());
                    if (velocity.getX() < 0 && overlap >= 0) { velocity.setX(0); }
                    break;
                case 180:
                    overlap = hits.getFirst().getCollider().getBounds().maxY - (getGameObject().transform.getPosition().getY() - halfSize.getY());
                    if (velocity.getY() < 0 && overlap >= 0) { addForce(new Vector2(0, -velocity.getY())); }
                    break;
            }
        }

    }

    private void limitVelocity(Vector2 direction) {
        Vector2 halfSize = rbCollider.getColliderSize().div(2);
        Vector2 pos = gameObject.transform.getPosition();
        float offsetAmount = direction.getX() != 0 ? halfSize.getX() : halfSize.getY();
        Vector2 rayOffset = direction.mul(offsetAmount);
        Vector2 perp = new Vector2(-direction.getY(), direction.getX()).mul(halfSize).mul(0.8f);

        Vector2 rayOrigin1 = pos.add(rayOffset).add(perp);
        Vector2 rayOrigin2 = pos.add(rayOffset).sub(perp);

        float angle = (float) Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));
        int angleInt = ((int) angle + 360) % 360;

        float currentVel = (int) direction.getX() == 0 ? Math.abs(velocity.getY()) : Math.abs(velocity.getX());
        currentVel = currentVel / 60;
        ArrayList<Raycast> rays = new ArrayList<>();
        rays.add(new Raycast(rayOrigin1, currentVel, angle, 50, rbCollider.getCollisionMask()));
        rays.add(new Raycast(rayOrigin2, currentVel, angle, 50, rbCollider.getCollisionMask()));

        ArrayList<Raycast.Hit> hits = new ArrayList<>();
        for (Raycast ray : rays) {
            Raycast.Hit hit = ray.checkForCollision();
            if (hit != null && !hit.getCollider().isTrigger() && hit.getCollider() != rbCollider) {
                hits.add(hit);
            }
        }
        if (hits.isEmpty()) { return; }
        Raycast.Hit closestHit = null;
        float closestDistance = currentVel;

        for (Raycast.Hit hit : hits) {
            float distance = switch (angleInt) {
                case 0 -> hit.getHitPoint().getX() - rayOrigin1.getX();
                case 90 -> hit.getHitPoint().getY() - rayOrigin1.getY();
                case 180 -> rayOrigin1.getX() - hit.getHitPoint().getX();
                case 270 -> rayOrigin1.getY() - hit.getHitPoint().getY();
                default -> 0;
            };

            if (distance < closestDistance && distance > 0) {
                closestDistance = distance;
                closestHit = hit;
            }
        }

        if (closestHit != null) {

            float adjustedVel = (float) (closestDistance / GamePanel.getDeltaTime());

            switch (angleInt) {
                case 0:
                    if (velocity.getX() > 0){
                        velocity.setX(adjustedVel);
                    }
                    break;
                case 90:
                    if (velocity.getY() > 0) {
                        velocity.setY(adjustedVel);
                    }
                    break;
                case 180:
                    if (velocity.getX() < 0){
                        velocity.setX(-adjustedVel);
                    }
                    break;
                case 270:
                    if (velocity.getY() < 0) {
                        velocity.setY(-adjustedVel);
                    }
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
        defaultValues.put("gravityScale", 1f);
        defaultValues.put("maxVelocity", null);
        defaultValues.put("isKinematic", false);
        return defaultValues;
    }

    public void handleCollisions(List<Collider> colliders){
        colliders.removeIf(Collider::isTrigger);
        allCollisions.removeIf(collider -> !colliders.contains(collider));
        updateTouchingColliders();

        if(gameObject.name.equals("Player")){
            DebugText.logPermanently("Player collisions: ", String.valueOf(allCollisions.size()));
        }
        if(colliders.isEmpty()){return;}
        Vector2 maxOverlap = new Vector2(0, 0);
        Collider strongestCollider = colliders.getFirst();
        for (Collider c : colliders) {
            allCollisions.add(c);
            Vector2 o = rbCollider.getOverlap(c);
            if (o.getMag() > maxOverlap.getMag()) {
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
        float dx = thisPos.getX() - otherPos.getX();
        float dy = thisPos.getY() - otherPos.getY();
        if (overlap.getX() < overlap.getY()) {
            // X-axis collision (left/right)
            if (dx > 0) {
//                left.add(other);
                // Player is to the right of the object
                if(isKinematic){return;}
                thisPos.setX(thisPos.getX() + overlap.getX());
            } else {
//                right.add(other);
                // Player is to the left of the object
                if(isKinematic){return;}
                thisPos.setX(thisPos.getX() - overlap.getX());
            }
        } else {
            // Y-axis collision (top/bottom)
            if (dy > 0) {
//                above.add(other);
                // Player is below the object
                if(isKinematic){return;}
                thisPos.setY(thisPos.getY() + overlap.getY());
            } else {
//                below.add(other);
                // Player is above the object (standing on it)
                if(isKinematic){return;}
                thisPos.setY(thisPos.getY() - overlap.getY());
            }
        }
        getGameObject().transform.setPosition(thisPos);
    }
    public void addForce(Vector2 force) {
        forces.add(force);  // Store forces instead of applying them immediately
    }
    public void clearForces(){
        forces.clear();
    }

}
