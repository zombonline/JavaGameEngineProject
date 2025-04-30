package core.utils;

import game.enums.CollisionLayer;
import core.scene.SessionManager;
import game.components.Collider;

import java.util.ArrayList;
import java.util.List;

public class Raycast {
    private final Vector2 origin;
    private final double length;
    private final float rotation;
    private final float checkFrequency;
    private final List<CollisionLayer> mask;
    private final boolean ignoreTriggers;
    public Raycast(Vector2 origin, double length, float rotation, float checkFrequency, List<CollisionLayer> mask, boolean ignoreTriggers){
        this.origin = origin;
        this.length = length;
        this.rotation = rotation;
        this.checkFrequency = checkFrequency;
        this.mask = mask;
        this.ignoreTriggers = ignoreTriggers;
    }

    public static class Hit {
        final Vector2 hitPoint;
        final Collider collider;
        public Hit(Vector2 hitPoint, Collider collider){
            this.hitPoint = hitPoint;
            this.collider = collider;
        }
        public Vector2 getHitPoint(){
            return this.hitPoint;
        }
        public Collider getCollider(){
            return this.collider;
        }
    }

    public Hit checkForCollision(){
        float epsilon = 1e-9f;
        double radians = Math.toRadians(this.rotation);
        double targetX = this.origin.getX() + Math.cos(radians) * this.length;
        double targetY = this.origin.getY() + Math.sin(radians) * this.length;
        if (Math.abs(targetX) < epsilon) targetX = 0;
        if (Math.abs(targetY) < epsilon) targetY = 0;
        Vector2 targetPoint = new Vector2(targetX,targetY);
        for(int i = 1; i <= checkFrequency; i++){
            Vector2 lerpedPosition = Vector2.lerp(this.origin, targetPoint, (float) i / checkFrequency);
            Collider collider = checkForColliderAtPoint(lerpedPosition, mask);
            if(collider!= null){
                return new Hit(lerpedPosition, collider);
            }
        }
        return null;
    }
    public Collider checkForColliderAtPoint(Vector2 point, List<CollisionLayer> mask){
        List<Collider> nearbyColliders = SessionManager.getCurrentLevel().spatialHashGrid.getNearby(point, mask);
        Collider result = null;
        for (Collider nearbyCollider : nearbyColliders) {
            if(nearbyCollider.isTrigger() && ignoreTriggers) {continue;}
            Bounds b = nearbyCollider.getBounds();
            if(point.getX() > b.minX && point.getX() < b.maxX && point.getY() > b.minY && point.getY() < b.maxY){
                result = nearbyCollider;
                break;
            }
        }
        return result;
    }

}
