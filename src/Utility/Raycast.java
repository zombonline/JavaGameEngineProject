package Utility;

import ObjectSystem.Collider;
import Main.Bounds;
import Main.SpatialHashGrid;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Raycast {
    private Vector2 origin;
    private float length;
    private float rotation;
    private float checkFrequency;
    private ArrayList<CollisionLayer> mask;
    public Raycast(Vector2 origin, float length, float rotation, float checkFrequency, ArrayList<CollisionLayer> mask){
        this.origin = origin;
        this.length = length;
        this.rotation = rotation;
        this.checkFrequency = checkFrequency;
        this.mask = mask;
    }

    public Collider checkForCollision(){
        float epsilon = 1e-6f;
        double radians = Math.toRadians(this.rotation);
        double targetX = this.origin.getX() + Math.cos(radians) * this.length;
        double targetY = this.origin.getY() + Math.sin(radians) * this.length;
        if (Math.abs(targetX) < epsilon) targetX = 0;
        if (Math.abs(targetY) < epsilon) targetY = 0;
        Vector2 targetPoint = new Vector2(targetX,targetY);
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
    public Collider checkForColliderAtPoint(Vector2 point, ArrayList<CollisionLayer> mask){
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
