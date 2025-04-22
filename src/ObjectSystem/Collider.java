package ObjectSystem;
import Main.*;
import Utility.CollisionLayer;
import Utility.Vector2;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Collider extends Component{
    public interface CollisionListener {
        void onCollisionEnter(Collider other);
        void onCollisionExit(Collider other);
        void onCollisionStay(Collider other);
    }
    private List<CollisionListener> listeners = new ArrayList<>();
    public void addListener(CollisionListener listener) {
        listeners.add(listener);
    }
    public void removeListener(CollisionListener listener) {
        listeners.remove(listener);
    }
    public void notifyCollisionEnter(Collider other) {
        for (CollisionListener listener : listeners) {
            listener.onCollisionEnter(other);
        }
//        handleCollisionLog(other);
    }

    private void handleCollisionLog(Collider other) {
        //check has codes so only one prints
        if(gameObject.getName().charAt(0) == other.gameObject.getName().charAt(0)){
            if (System.identityHashCode(this) < System.identityHashCode(other)) {
                DebugText.logTemporarily("COLLISION: " + gameObject.getName() + "(" + gameObject.getTransform().getPosition().toDp(2) + ") - " + other.gameObject.getName() + "(" + other.gameObject.getTransform().getPosition().toDp(2) + ")");
            }
        } else {
            if(gameObject.getName().charAt(0) < other.gameObject.getName().charAt(0)){
                DebugText.logTemporarily("COLLISION: " + gameObject.getName() + "(" + gameObject.getTransform().getPosition().toDp(2) + ") - " + other.gameObject.getName() + "(" + other.gameObject.getTransform().getPosition().toDp(2) + ")");
            }
        }
    }

    public void notifyCollisionStay(Collider other){
        for (CollisionListener listener : listeners) {
            listener.onCollisionStay(other);
        }
    }
    public void notifyCollisionExit(Collider other){
        for (CollisionListener listener : listeners) {
            listener.onCollisionExit(other);
        }
    }

    private boolean isStatic;
    private CollisionLayer collisionLayer;
    private ArrayList<CollisionLayer> collisionMask;
    private Vector2 size, offset, colliderPosition;
    private int previousCellKey = Integer.MIN_VALUE;
    private Bounds bounds;
    private boolean isTrigger;

    ArrayList<Collider> allCollisions = new ArrayList<>(), allCollisionsLastFrame = new ArrayList<Collider>();

    public Collider(boolean isStatic, CollisionLayer collisionLayer, ArrayList<CollisionLayer> collisionMask, Vector2 size, Vector2 offset, boolean isTrigger) {
        this.isStatic = isStatic;
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
        this.size = size;
        this.offset = offset;
        this.isTrigger = isTrigger;
    }

    @Override
    public void awake() {
        colliderPosition = getGameObject().getTransform().getPosition().add(offset);
        this.bounds = new Bounds(colliderPosition, size);
        previousCellKey = SessionManager.getCurrentLevel().spatialHashGrid.hash(colliderPosition);
        SessionManager.getCurrentLevel().spatialHashGrid.insert(this);
    }
    @Override
    public void update() {
        super.update();
        if(!isStatic){
            this.colliderPosition = getGameObject().getTransform().getPosition().add(offset);
        }
        Vector2 scaledSize = size.mul(gameObject.getTransform().getScale());
        this.bounds = new Bounds(colliderPosition, scaledSize);
        setSpatialHashGridCell();
        checkCollidersNearby();
    }
    @Override
    public void onDestroy(){
        SessionManager.getCurrentLevel().spatialHashGrid.remove(this);
        allCollisions.clear();
        allCollisionsLastFrame.clear();
    }

    private void checkCollidersNearby() {
        List<Collider> nearbyColliders = SessionManager.getCurrentLevel().spatialHashGrid.getNearby(colliderPosition, collisionMask);
        List<Collider> colliding = new ArrayList<Collider>();
        for (Collider nearbyCollider : nearbyColliders) {
            if (nearbyCollider == this) {
                continue;
            }
            Vector2 overlap = getOverlap(nearbyCollider);
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

    public void updateTouchingColliders() {
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


    public Vector2 getOverlap(Collider other) {
        Bounds otherBounds = other.getBounds();
        float overlapX = Math.max(0, Math.min(bounds.maxX, otherBounds.maxX) - Math.max(bounds.minX, otherBounds.minX));
        float overlapY = Math.max(0, Math.min(bounds.maxY, otherBounds.maxY) - Math.max(bounds.minY, otherBounds.minY));
        return new Vector2(overlapX, overlapY);
    }

    private void setSpatialHashGridCell() {
        int newCellKey = SessionManager.getCurrentLevel().spatialHashGrid.hash(colliderPosition);
        if (newCellKey != previousCellKey) { // Only update if the cell changes
            SessionManager.getCurrentLevel().spatialHashGrid.remove(this, previousCellKey);    // Remove from old cell
            SessionManager.getCurrentLevel().spatialHashGrid.insert(this);    // Insert into new cell
            previousCellKey = newCellKey;    // Update last known cell
        }
    }
    public boolean isTrigger(){
        return isTrigger;
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
    public ArrayList<CollisionLayer> getCollisionMask(){
        return this.collisionMask;
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        g2d.setColor(new Color(255,0,0,100)); // Set color of the square
        Vector2 scaledSize = size.mul(gameObject.getTransform().getScreenScale());
        Vector2 drawPos = gameObject.getTransform().getScreenPosition().add(offset.mul(GamePanel.WORLD_SCALE)).sub(scaledSize.div(2));
//        g2d.fillRect((int) drawPos.getX(), (int) drawPos.getY(), (int)scaledSize.getX(), (int)scaledSize.getY());
    }

    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("isStatic", false);
        defaultValues.put("collisionLayer", CollisionLayer.DEFAULT);
        List<CollisionLayer> collisionMask = new ArrayList<CollisionLayer>();
        collisionMask.add(CollisionLayer.DEFAULT);
        defaultValues.put("collisionMask", collisionMask);
        defaultValues.put("size", Vector2.one);
        defaultValues.put("offset", Vector2.zero);
        defaultValues.put("isTrigger", false);
        return defaultValues;
    }


}
