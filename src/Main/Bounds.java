package Main;

import Utility.Vector2;

import java.util.ArrayList;

public class Bounds {
    public float minX, maxX, minY, maxY;

    public Bounds(float minX, float maxX, float minY, float maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    public Bounds(Vector2 pos, Vector2 size){
        float halfWidth = size.getX()/2;
        float halfHeight = size.getY()/2;

        this.minX = pos.getX()-halfWidth;
        this.maxX = pos.getX()+halfWidth;
        this.minY = pos.getY()-halfHeight;
        this.maxY = pos.getY()+halfHeight;
    }
    public Bounds(ArrayList<Float> vals){
        this.minX = (float) (vals.getFirst());
        this.maxX = (float) (vals.get(1));
        this.minY = (float) (vals.get(2));
        this.maxY = (float) (vals.get(3));
    }
    @Override
    public String toString() {
        return "minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY;
    }
}
