package ObjectSystem;
import Main.Bounds;
import Main.GamePanel;
import Main.SpatialHashGrid;
import Utility.CollisionLayer;
import Utility.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Collider extends Component{
    private boolean isStatic;
    private CollisionLayer collisionLayer;
    private ArrayList<CollisionLayer> collisionMask;
    private Vector2 size, offset, colliderPosition;
    private int previousCellKey = Integer.MIN_VALUE;
    private Bounds bounds;

    public Collider(boolean isStatic, CollisionLayer collisionLayer, ArrayList<CollisionLayer> collisionMask, Vector2 size, Vector2 offset) {
        this.isStatic = isStatic;
        this.collisionLayer = collisionLayer;
        this.collisionMask = collisionMask;
        this.size = size.mul(GamePanel.WORLD_SCALE);
        this.offset = offset.mul(GamePanel.WORLD_SCALE);
    }

    public Collider(){
        this.isStatic = false;
        collisionLayer = CollisionLayer.values()[0];
        collisionMask =  new ArrayList<CollisionLayer>();
        collisionMask.add(CollisionLayer.values()[0]);
        size = Vector2.one;
        offset = Vector2.zero;
    }

    @Override
    public void awake() {
        super.awake();
        colliderPosition = getGameObject().transform.getPosition().add(offset);
        this.bounds = new Bounds(colliderPosition, size);
        previousCellKey = SpatialHashGrid.hash(colliderPosition);
        SpatialHashGrid.insert(this);
    }

    @Override
    public void update() {
        super.update();
        if(!isStatic){
            this.colliderPosition = getGameObject().transform.getPosition().add(offset);
        }
        this.bounds = new Bounds(colliderPosition, size);

        setSpatialHashGridCell();
        checkCollidersNearby();
    }

//    @Override
//    public void draw(Graphics2D g2d) {
//        super.draw(g2d);
//        g2d.setColor(Color.RED); // Set color of the square
//        int x = (int)getGameObject().transform.getScreenPosition().getX();
//        int y = (int)getGameObject().transform.getScreenPosition().getY();
//        int w = (int)size.getX()*GamePanel.WORLD_SCALE;
//        int h = (int)size.getY()*GamePanel.WORLD_SCALE;
//        g2d.fillRect(x, y, w, h); // Draw the square
//    }

    private void checkCollidersNearby() {
        List<Collider> nearbyColliders = SpatialHashGrid.getNearby(colliderPosition, collisionMask);
        List<Collider> colliding = new ArrayList<Collider>();
        for (Collider nearbyCollider : nearbyColliders) {
            if (nearbyCollider == this) {
                continue;
            }
            Vector2 overlap = getOverlap(nearbyCollider);
            if (overlap.getY() <= 0 || overlap.getX() <= 0) {
                continue;
            }
            if(getGameObject().getComponent(Rigidbody.class)!=null){
                colliding.add(nearbyCollider);
            }
        }
        if(colliding.isEmpty()){return;}
        this.getGameObject().getComponent(Rigidbody.class).handleCollisions(colliding);
    }

    public Vector2 getOverlap(Collider other) {
        Bounds otherBounds = other.getBounds();
        float overlapX = Math.max(0, Math.min(bounds.maxX, otherBounds.maxX) - Math.max(bounds.minX, otherBounds.minX));
        float overlapY = Math.max(0, Math.min(bounds.maxY, otherBounds.maxY) - Math.max(bounds.minY, otherBounds.minY));
        return new Vector2(overlapX, overlapY);
    }

    private void setSpatialHashGridCell() {
        int newCellKey = SpatialHashGrid.hash(colliderPosition);
        if (newCellKey != previousCellKey) { // Only update if the cell changes
            SpatialHashGrid.remove(this, previousCellKey);    // Remove from old cell
            SpatialHashGrid.insert(this);    // Insert into new cell
            previousCellKey = newCellKey;    // Update last known cell
        }
    }

    public CollisionLayer getCollisionLayer() {
        return collisionLayer;
    }
    public Vector2 getColliderPosition(){
        return colliderPosition;
    }
    public Vector2 getColliderSize(){
        return size;
    }
    public Bounds getBounds(){
        return this.bounds;
    }
}
