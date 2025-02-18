package Utility;

import Entity.Collider;
import Entity.CollisionLayer;
import Main.Bounds;
import Main.SpatialHashGrid;

import javax.management.loading.MLet;
import java.util.ArrayList;
import java.util.List;

public class Raycast {
    private Vector2 origin;
    private float length;
    private float rotation;
    private float checkFrequency;
    private ArrayList<CollisionLayer> mask;
    public Raycast(Vector2 origin, float length, float rotation, float checkFrequency){
        this.origin = origin;
        this.length = length;
        this.rotation = rotation;
        this.checkFrequency = checkFrequency;
    }

    public Collider checkForCollision(){
        Vector2 targetPoint = new Vector2(this.origin.getX() + Math.cos(this.rotation) * this.length,this.origin.getY() + Math.cos(this.rotation) * this.length);
        for(int i = 1; i <= checkFrequency; i++){
            Vector2 lerpedPosition = Vector2.lerp(this.origin, targetPoint, i/checkFrequency);
            Collider collider = checkForColliderAtPoint(lerpedPosition, mask);
            if(collider!= null){
                return collider;
            }
        }
        return null;
    }

    //I SHOULD PROBABLY MAKE A RAYCASTING CLASS AND STICK THIS IN THERE
    public static Collider checkForColliderAtPoint(Vector2 point, ArrayList<CollisionLayer> mask){
        List<Collider> nearbyColliders = SpatialHashGrid.getNearby(point, mask);
        Collider result = null;
        for (Collider nearbyCollider : nearbyColliders) {
            Bounds b = nearbyCollider.getBounds();

            if(point.getX() > b.minX && point.getX() < b.maxX && point.getY() > b.minY && point.getY() < b.maxY){
                result = nearbyCollider;
                break;
            }
        }
        return result;
    }

}
