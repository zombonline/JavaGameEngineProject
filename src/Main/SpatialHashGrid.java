package Main;

import java.awt.color.ICC_ColorSpace;
import java.sql.ClientInfoStatus;
import java.util.*;
import Entity.Collider;
import Entity.CollisionLayer;
import Utility.Vector2;


public class SpatialHashGrid {
    private static int cellSize = 3;
    private static Map<Integer, List<Collider>> grid = new HashMap<>();;


    public static int hash(Vector2 pos){
        int x = (int) Math.floor((pos.getX()/cellSize));
        int y = (int) Math.floor((pos.getY()/cellSize));
        return Objects.hash(x,y);
    }

    public static void insert(Collider collider){
        int cellKey = hash(collider.getColliderPosition());
        List<Collider> cell = grid.computeIfAbsent(cellKey, k -> new ArrayList<>());

        if(cell.contains(collider)) {
            return;
        }
        cell.add(collider);

    }

    public static void remove(Collider collider, int cellKey){
        List<Collider> cell = grid.get(cellKey);
        if(cell==null){
            System.out.println("cell is null");
            return;
        }
        cell.remove(collider);
        if(cell.isEmpty()){
            grid.remove(cellKey);
        }
    }

    public static void remove(Collider collider){
        int cellKey = hash(collider.getColliderPosition());
        List<Collider> cell = grid.get(cellKey);
        if(cell==null){
            System.out.println("cell is null");
            return;
        }
        cell.remove(collider);
        if(cell.isEmpty()){
            grid.remove(cellKey);
        }
    }
    public static List<Collider> getNearby(Vector2 pos, ArrayList<CollisionLayer> layerMask) {
        List<Collider> nearbyColliders = new ArrayList<>();
        for (Collider collider : getNearby(pos)) {
            if (layerMask.contains(collider.getCollisionLayer())) {
                nearbyColliders.add(collider);
            }
        }
        return nearbyColliders;
    }
    public static List<Collider> getNearby(Vector2 pos) {
        List<Collider> nearbyColliders = new ArrayList<>();
        int baseX = (int) Math.floor(pos.getX() / cellSize);
        int baseY = (int) Math.floor(pos.getY() / cellSize);
        //return cell the collider is inside and the eight surrounding cells
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int cellKey = Objects.hash(baseX + dx, baseY + dy);
                nearbyColliders.addAll(grid.getOrDefault(cellKey, Collections.emptyList()));
            }
        }
        return nearbyColliders;
    }
}
