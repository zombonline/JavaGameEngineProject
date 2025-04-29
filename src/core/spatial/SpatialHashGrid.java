package core.spatial;

import java.util.*;
import game.components.Collider;
import game.enums.CollisionLayer;
import core.utils.DebugText;
import core.utils.Vector2;


public class SpatialHashGrid {
    private int cellSize = 8;
    private final Map<Integer, List<Collider>> grid = new HashMap<>();
    public SpatialHashGrid(int cellSize){
        this.cellSize = cellSize;
        DebugText.logTemporarily("New SpatialHashGrid with cellSize: " + cellSize);
    }

    public int hash(Vector2 pos){
        int x = (int) Math.floor((pos.getX()/cellSize));
        int y = (int) Math.floor((pos.getY()/cellSize));
        return Objects.hash(x,y);
    }

    public void insert(Collider collider){
        int cellKey = hash(collider.getColliderPosition());
        List<Collider> cell = grid.computeIfAbsent(cellKey, k -> new ArrayList<>());

        if(cell.contains(collider)) {
            return;
        }
        cell.add(collider);

    }

    public void remove(Collider collider, int cellKey){
        List<Collider> cell = grid.get(cellKey);
        if(cell==null) return;
        cell.remove(collider);
        if(cell.isEmpty()){
            grid.remove(cellKey);
        }
    }

    public void remove(Collider collider) {
        remove(collider, hash(collider.getColliderPosition()));
    }

    public void clear(){
        grid.clear();
    }

    public List<Collider> getNearby(Vector2 pos, List<CollisionLayer> layerMask) {
        List<Collider> nearbyColliders = new ArrayList<>();
        for (Collider collider : getNearby(pos)) {
            if (layerMask.contains(collider.getCollisionLayer())) {
                nearbyColliders.add(collider);
            }
        }
        return nearbyColliders;
    }
    public List<Collider> getNearby(Vector2 pos) {
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
