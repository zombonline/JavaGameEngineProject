package Entity;
import Main.GamePanel;
import Main.SpatialHashGrid;
import Utility.Vector2;
import java.util.ArrayList;
import java.util.List;

public class Collider extends Component{
    private boolean isStatic;
    private CollisionLayer collisionLayer;
    private ArrayList<CollisionLayer> collisionMask;
    Vector2 size, offset, colliderPosition;
    private int previousCellKey = Integer.MIN_VALUE;

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

        previousCellKey = SpatialHashGrid.hash(colliderPosition);
        SpatialHashGrid.insert(this);
    }

    @Override
    public void update() {
        super.update();
        if(!isStatic){
            this.colliderPosition = getGameObject().transform.getPosition().add(offset);
        }
        setSpatialHashGridCell();
        checkCollidersNearby();
    }

    private void checkCollidersNearby() {
        List<Collider> nearbyColliders = SpatialHashGrid.getNearby(colliderPosition, collisionMask);
        for(int i = 0; i < nearbyColliders.size(); i++){
            if(nearbyColliders.get(i) == this){continue;}
            if(checkIfColliding(nearbyColliders.get(i))){
                System.out.println(nearbyColliders.get(i).gameObject.name + " is colliding with " + this.getGameObject().name);
            }
        }
    }

    private boolean checkIfColliding(Collider other){
        double distanceBetween = colliderPosition.sub(other.getColliderPosition()).getMag();

        float aHalfWidth = this.size.getX()/2;
        float aHalfHeight = this.size.getY()/2;

        float axMin = this.colliderPosition.getX()-aHalfWidth;
        float axMax = this.colliderPosition.getX()+aHalfWidth;
        float ayMin = this.colliderPosition.getY()-aHalfHeight;
        float ayMax = this.colliderPosition.getY()+aHalfHeight;

        float bHalfWidth = this.size.getX()/2;
        float bHalfHeight = this.size.getY()/2;

        float bxMin = other.getColliderPosition().getX()-bHalfWidth;
        float bxMax = other.getColliderPosition().getX()+bHalfWidth;
        float byMin = other.getColliderPosition().getY()-bHalfHeight;
        float byMax = other.getColliderPosition().getY()+bHalfHeight;

        boolean xOverlap = axMax > bxMin && axMin < bxMax;
        boolean yOverlap = ayMax > byMin && ayMin < byMax;

        return xOverlap && yOverlap;
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
}
