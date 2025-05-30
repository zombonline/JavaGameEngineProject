package game.components;
import game.components.crate.core.Crate;
import game.entities.GameObject;
import main.GamePanel;
import core.utils.DebugText;
import core.utils.Raycast;
import core.utils.Vector2;
import game.components.core.Component;

import java.util.*;
import java.util.List;

public class Rigidbody extends Component {
    Collider rbCollider;

    public Vector2 velocity, velocityLastFrame;
    final float drag;
    public final Vector2 maxVelocity;
    public final float restitution;
    public final float gravityScale;
    private final List<Vector2> forces = new ArrayList<>();

    private boolean isOnMovingPlatform = false;
    private final boolean isKinematic;
    private boolean isGrounded = false, wasGroundedLastFrame = false;
    private final ArrayList<Collider> groundedColliders = new ArrayList<>();
    private final float mass;

    public Rigidbody(float drag, float gravityScale, float restitution, Vector2 maxVelocity, boolean isKinematic, float mass) {
        this.drag = drag;
        this.gravityScale = gravityScale;
        this.restitution = restitution;
        this.maxVelocity = maxVelocity;
        this.velocity = Vector2.zero;
        this.velocityLastFrame = Vector2.zero;
        this.isKinematic = isKinematic;
        this.mass = mass;
    }

    @Override
    public void awake() {
        rbCollider = getComponent(Collider.class);
    }

    public void update(){
        wasGroundedLastFrame = isGrounded;
        if(isKinematic) return;
        groundCheck();
        velocityLastFrame = velocity;
        for (Vector2 force : forces) {
            velocity = velocity.add(force);
        }
        forces.clear();
        double horizontalAfterDrag = velocity.getX() * 1/(1+drag * GamePanel.getDeltaTime());
        velocity = new Vector2(horizontalAfterDrag, velocity.getY());
        if(!isGrounded && !isOnMovingPlatform){
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
        getGameObject().getTransform().translate(velocity.div(60));
        if(isGrounded && velocity.getY() > 0){
            velocity.setY(0);
        }
    }

    public void groundCheck() {
        Vector2 rayOrigin1 = new Vector2(rbCollider.getBounds().minX + 0.2f, rbCollider.getBounds().maxY + 0.001f);
        Vector2 rayOrigin2 = new Vector2(rbCollider.getBounds().maxX - 0.2f, rbCollider.getBounds().maxY + 0.001f);

        Raycast raycast1 = new Raycast(rayOrigin1, .00000001f, 0, 100, rbCollider.getCollisionMask(), true);
        Raycast raycast2 = new Raycast(rayOrigin2, .00000001f, 0, 100, rbCollider.getCollisionMask(), true);
        ArrayList<Collider> touching = new ArrayList<>();

        //CHECK IF THESE ARE TRIGGERS
        if (raycast1.checkForCollision() != null) {
            if (!Collider.getContactNormal(rbCollider, raycast1.checkForCollision().getCollider()).equals(Vector2.down)) {
                touching.add(raycast1.checkForCollision().getCollider());
            }
        }
        if (raycast2.checkForCollision() != null) {
            if (!Collider.getContactNormal(rbCollider, raycast2.checkForCollision().getCollider()).equals(Vector2.down)) {
                touching.add(raycast2.checkForCollision().getCollider());
            }
        }

        groundedColliders.clear();
        groundedColliders.addAll(touching);
        for(Collider collider : groundedColliders){
            rbCollider.addExternalCollision(collider);
        }
        isGrounded = !groundedColliders.isEmpty();
        DebugText.logPermanently("Player Rigidbody Grounded", Boolean.toString(isGrounded));
    }

    private void limitVelocity(Vector2 direction) {
        Vector2 halfSize = rbCollider.getColliderSize().div(2);
        Vector2 pos = gameObject.getTransform().getPosition();
        float offsetAmount = direction.getX() != 0 ? halfSize.getX() : halfSize.getY();
        Vector2 rayOffset = direction.mul(offsetAmount);
        Vector2 perp = new Vector2(-direction.getY(), direction.getX()).mul(halfSize).mul(0.8f);

        Vector2 rayOrigin1 = pos.add(rayOffset).add(perp);
        Vector2 rayOrigin2 = pos.add(rayOffset).sub(perp);

        float angle = (float) Math.toDegrees(Math.atan2(direction.getY(), direction.getX()));
        int angleInt = ((int) angle + 360) % 360;

        double currentVel = (int) direction.getX() == 0 ? Math.abs(velocity.getY()) : Math.abs(velocity.getX());
        currentVel = currentVel / 60;
        ArrayList<Raycast> rays = new ArrayList<>();
        rays.add(new Raycast(rayOrigin1, currentVel, angle, 50, rbCollider.getCollisionMask(), true));
        rays.add(new Raycast(rayOrigin2, currentVel, angle, 50, rbCollider.getCollisionMask(), true));
        ArrayList<Raycast.Hit> hits = new ArrayList<>();
        for (Raycast ray : rays) {
            Raycast.Hit hit = ray.checkForCollision();
            if (hit != null && !hit.getCollider().isTrigger() && hit.getCollider() != rbCollider) {
                hits.add(hit);
            }
        }
        if (hits.isEmpty()) { return; }
        Raycast.Hit closestHit = null;
        double closestDistance = currentVel;
        for (Raycast.Hit hit : hits) {
            double distance = switch (angleInt) {
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
        if (closestHit != null ) {
            if(closestHit.getCollider().hasComponent(Rigidbody.class)){return;}

            double adjustedVel = (float) (closestDistance / GamePanel.getDeltaTime());

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

    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("drag",0.9f);
        defaultValues.put("restitution", 0f);
        defaultValues.put("gravityScale", 1f);
        defaultValues.put("maxVelocity", null);
        defaultValues.put("isKinematic", false);
        defaultValues.put("mass", 1f);
        return defaultValues;
    }
    public void handleCollisions(List<Collider> colliders){
        colliders.removeIf(Collider::isTrigger);
        if(colliders.isEmpty()){return;}
        Vector2 maxOverlap = new Vector2(0, 0);
        Collider strongestCollider = colliders.getFirst();
        for (Collider c : colliders) {
            Vector2 o = Collider.getOverlap(rbCollider,c);
            if (o.getMag() > maxOverlap.getMag()) {
                maxOverlap = o;
                strongestCollider = c;
            }
        }
        handleCollision(strongestCollider);
    }

    public void handleCollision(Collider other){
        if(isKinematic){return;}

        //clone the position so overlap can be resolved then setpos can be called at the end
        Vector2 thisPosCopy = new Vector2(getGameObject().getTransform().getPosition());
        Vector2 otherPos = other.getGameObject().getTransform().getPosition();
        Vector2 overlap = Collider.getOverlap(rbCollider,other);
        double dx = thisPosCopy.getX() - otherPos.getX();
        double dy = thisPosCopy.getY() - otherPos.getY();

        float moveFraction = 1;
        if(other.hasComponent(Rigidbody.class) && !other.getComponent(Rigidbody.class).isKinematic()){
            float otherMass = other.getGameObject().getComponent(Rigidbody.class).getMass();
            float totalMass = mass + otherMass;
            moveFraction = otherMass /totalMass;
        }
        if (overlap.getX() < overlap.getY()) {
            // X-axis collision (left/right)
            double resolution = overlap.getX() * moveFraction;
            if (dx > 0) {
                // Player is to the right of the object
                thisPosCopy.setX(thisPosCopy.getX() + resolution);
            } else {
                // Player is to the left of the object
                thisPosCopy.setX(thisPosCopy.getX() - resolution);
            }
        } else {
            // Y-axis collision (top/bottom)
            double resolution = overlap.getY() * moveFraction;
            if (dy > 0) {
                // Player is below the object
                thisPosCopy.setY(thisPosCopy.getY() + resolution);
            } else {
                // Player is above the object (standing on it)
                thisPosCopy.setY(thisPosCopy.getY() - resolution);
            }
        }
        getGameObject().getTransform().setPosition(thisPosCopy);
    }
    public void addForce(Vector2 force) {
        forces.add(force);  // Store forces instead of applying them immediately
    }
    public void clearForces(){
        forces.clear();
    }
    //Getters
    public List<Collider> getGroundedColliders() {
        return groundedColliders;
    }
    public boolean isGrounded() {
        return isGrounded;
    }
    public float getMass(){
        return mass;
    }
    public boolean isKinematic() {
        return isKinematic;
    }

    public boolean isOnMovingPlatform() {
        return isOnMovingPlatform;
    }

    public void setOnMovingPlatform(boolean onMovingPlatform) {
        isOnMovingPlatform = onMovingPlatform;
    }
}
