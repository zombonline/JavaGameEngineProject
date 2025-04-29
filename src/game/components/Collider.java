package game.components;
import core.utils.Bounds;
import game.enums.CollisionLayer;
import core.utils.Vector2;
import core.scene.SessionManager;
import game.components.core.Component;
import java.util.*;
import java.util.List;

public class Collider extends Component {

    private final List<CollisionLayer> collisionMask;
    private final List<Collider> externallyAddedColliders = new ArrayList<>();
    List<Collider> allCollisions = new ArrayList<>(), allCollisionsLastFrame = new ArrayList<>();

    private final boolean isStatic;
    private final CollisionLayer collisionLayer;
    private final Vector2 size;
    private final Vector2 offset;
    private Vector2 colliderPosition;
    private int previousCellKey = Integer.MIN_VALUE;
    private Bounds bounds = new Bounds(1,1,1,1);
    private boolean isTrigger;



    public Collider(boolean isStatic, CollisionLayer collisionLayer, ArrayList<CollisionLayer> collisionMask, Vector2 size, Vector2 offset, boolean isTrigger) {
        this.isStatic = isStatic;
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
        this.size = size;
        this.offset = offset;
        this.isTrigger = isTrigger;
    }

    @Override
    public void update() {
        super.update();
        updateColliderPosition();
        updateBounds();
        setSpatialHashGridCell();
        checkCollidersNearby();
    }

    private void updateColliderPosition() {
        this.colliderPosition = getGameObject().getTransform().getPosition().add(offset);
    }

    @Override
    public void onDestroy(){
        SessionManager.getCurrentLevel().spatialHashGrid.remove(this);
        allCollisions.clear();
        allCollisionsLastFrame.clear();
    }

    private void updateBounds() {
        Vector2 scaledSize = size.mul(gameObject.getTransform().getScale());
        this.bounds = new Bounds(colliderPosition, scaledSize);
    }
    private void checkCollidersNearby() {
        if(isStatic){return;}
        List<Collider> nearbyColliders = SessionManager.getCurrentLevel().spatialHashGrid.getNearby(colliderPosition, collisionMask);

        List<Collider> colliding = new ArrayList<>();
        for (Collider nearbyCollider : nearbyColliders) {
            if (nearbyCollider == this) {
                continue;
            }
            Vector2 overlap = getOverlap(this, nearbyCollider);


            if (overlap.getY() <= 0 || overlap.getX() <= 0) {
                continue;
            }
            colliding.add(nearbyCollider);
            allCollisions.add(nearbyCollider);
        }
        allCollisions.removeIf(collider -> !colliding.contains(collider));
        updateTouchingColliders();
        if(this.getGameObject().getComponent(Rigidbody.class)!=null){
            this.getGameObject().getComponent(Rigidbody.class).handleCollisions(colliding);
        }
    }
    private void updateTouchingColliders() {
        if(isStatic){return;}

        allCollisions.addAll(externallyAddedColliders);
        externallyAddedColliders.clear();
        // Use a HashSet to eliminate duplicates without modifying the original list during iteration
        List<Collider> uniqueCollisions = new ArrayList<>(new HashSet<>(allCollisions));

        // Iterate over a copy or snapshot to avoid problematic modifications while looping
        for (Collider collider : uniqueCollisions) {
            // Check and handle "CollisionEnter" and "CollisionStay" events
            if (!allCollisionsLastFrame.contains(collider)) {
                collider.notifyCollisionEnter(this);
            }
            collider.notifyCollisionStay(this);
        }

        // Avoid modifying the original list; prepare another loop for exit collisions
        List<Collider> collisionsLastFrameCopy = new ArrayList<>(allCollisionsLastFrame);
        for (Collider collider : collisionsLastFrameCopy) {
            if (!uniqueCollisions.contains(collider)) {
                collider.notifyCollisionExit(this);
            }
        }

        // Update last frame collisions safely outside of loops
        allCollisionsLastFrame = new ArrayList<>(uniqueCollisions);
    }
    private void setSpatialHashGridCell() {
        int newCellKey = SessionManager.getCurrentLevel().spatialHashGrid.hash(colliderPosition);
        if (newCellKey != previousCellKey) { // Only update if the cell changes
            SessionManager.getCurrentLevel().spatialHashGrid.remove(this, previousCellKey);    // Remove from old cell
            SessionManager.getCurrentLevel().spatialHashGrid.insert(this);    // Insert into new cell
            previousCellKey = newCellKey;    // Update last known cell
        }
    }

    public void addExternalCollision(Collider collider) {
        externallyAddedColliders.add(collider);
    }

    //region Static methods
    public static Vector2 getContactNormal(Collider a, Collider b) {
        Bounds aBounds = a.bounds;
        Bounds bBounds = b.getBounds();

        // Centers
        double dx = ((aBounds.minX + aBounds.maxX) / 2) - ((bBounds.minX + bBounds.maxX) / 2);
        double dy = ((aBounds.minY + aBounds.maxY) / 2) - ((bBounds.minY + bBounds.maxY) / 2);

        // Half-sizes
        double halfWidthA = (aBounds.maxX - aBounds.minX) / 2;
        double halfWidthB = (bBounds.maxX - bBounds.minX) / 2;
        double halfHeightA = (aBounds.maxY - aBounds.minY) / 2;
        double halfHeightB = (bBounds.maxY - bBounds.minY) / 2;

        // Overlaps
        double overlapX = (halfWidthA + halfWidthB) - Math.abs(dx);
        double overlapY = (halfHeightA + halfHeightB) - Math.abs(dy);

        if (overlapX < overlapY) {
            return new Vector2(dx < 0 ? -1 : 1, 0); // Side collision
        } else {
            return new Vector2(0, dy < 0 ? -1 : 1); // Top or bottom
        }
    }
    public static Vector2 getOverlap(Collider a, Collider b) {
        Bounds aBounds = a.bounds;
        Bounds bBounds = b.getBounds();
        double overlapX = Math.max(0, Math.min(aBounds.maxX, bBounds.maxX) - Math.max(aBounds.minX, bBounds.minX));
        double overlapY = Math.max(0, Math.min(aBounds.maxY, bBounds.maxY) - Math.max(aBounds.minY, bBounds.minY));
        return new Vector2(overlapX, overlapY);
    }
    //endregion

    //region Getters and setters
    public boolean isTrigger(){
        return isTrigger;
    }
    public void setIsTrigger(boolean val){
        isTrigger = val;
    }

    public CollisionLayer getCollisionLayer() {
        return collisionLayer;
    }
    public Vector2 getColliderPosition(){
        return colliderPosition;
    }
    public Vector2 getColliderSize(){
        return size.mul(gameObject.getTransform().getScale());
    }
    public Bounds getBounds(){
        return this.bounds;
    }
    public List<CollisionLayer> getCollisionMask(){
        return this.collisionMask;
    }
    //endregion

    //region Collider Listener Logic
    public interface CollisionListener {
        void onCollisionEnter(Collider other, Vector2 contactNormal);
        void onCollisionExit(Collider other, Vector2 contactNormal);
        void onCollisionStay(Collider other, Vector2 contactNormal);
    }
    private final List<CollisionListener> listeners = new ArrayList<>();
    public void addListener(CollisionListener listener) {
        listeners.add(listener);
    }
    public void removeListener(CollisionListener listener) {
        listeners.remove(listener);
    }
    public void notifyCollisionEnter(Collider other) {
        for (CollisionListener listener : listeners) {
            listener.onCollisionEnter(other, getContactNormal(this, other));
        }
    }
    public void notifyCollisionStay(Collider other){
        for (CollisionListener listener : listeners) {
            listener.onCollisionStay(other, getContactNormal(this, other));
        }
    }
    public void notifyCollisionExit(Collider other){
        for (CollisionListener listener : listeners) {
            listener.onCollisionExit(other, getContactNormal(this, other));
        }
    }
    //endregion

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("isStatic", false);
        defaultValues.put("collisionLayer", CollisionLayer.DEFAULT);
        List<CollisionLayer> collisionMask = new ArrayList<>();
        collisionMask.add(CollisionLayer.DEFAULT);
        defaultValues.put("collisionMask", collisionMask);
        defaultValues.put("size", Vector2.one);
        defaultValues.put("offset", Vector2.zero);
        defaultValues.put("isTrigger", false);
        return defaultValues;
    }
}
