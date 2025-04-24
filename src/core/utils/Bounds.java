package core.utils;

import java.util.ArrayList;

public class Bounds {
    public double minX, maxX, minY, maxY;

    public Bounds(double minX, double maxX, double minY, double maxY){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
    public Bounds(Vector2 pos, Vector2 size){
        double halfWidth = size.getX()/2;
        double halfHeight = size.getY()/2;

        this.minX = pos.getX()-halfWidth;
        this.maxX = pos.getX()+halfWidth;
        this.minY = pos.getY()-halfHeight;
        this.maxY = pos.getY()+halfHeight;
    }
    public Bounds(ArrayList<Double> vals){
        this.minX = (vals.getFirst());
        this.maxX = (vals.get(1));
        this.minY = (vals.get(2));
        this.maxY = (vals.get(3));
    }
    @Override
    public String toString() {
        return "minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY;
    }
}
