package Utility;

import java.util.ArrayList;

public class Vector2 {

    public static final Vector2 up = new Vector2(0,-1);
    public static final Vector2 down = new Vector2(0,1);
    public static final Vector2 left = new Vector2(-1,0);
    public static final Vector2 right = new Vector2(1,0);
    public static final Vector2 zero = new Vector2(0,0);
    public static final Vector2 one = new Vector2(1,1);
    private float x, y;
    public Vector2(double x, double y){
        this.x = (float) x;
        this.y = (float)y;
    }
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2(String vals){
        try {
            String[] parts = vals.split(",");
            this.x = Float.parseFloat(parts[0]);
            this.y = Float.parseFloat(parts[1]);
        } catch (Exception e){
            System.out.println("Unable to parse string to create Vector2.");
        }
    }
    public Vector2(ArrayList vals){
        this.x = (float) (vals.getFirst());
        this.y = (float) (vals.getLast());
    }

    public Vector2(){
        this(0,0);
    }
    public float getX(){return x;}
    public float getY(){return y;}
    public double getMag(){return Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y,2));}
    public Vector2 getNormalized(){return new Vector2(this.x/getMag(),this.y/getMag());}
    public void setX(float x){this.x = x;}
    public void setY(float y){this.y = y;}
    public Vector2 add(Vector2 v){
        return new Vector2(this.x + v.getX(), this.y + v.getY());
    }
    public Vector2 sub(Vector2 v){
        return new Vector2(this.x - v.getX(), this.y - v.getY());
    }
    public Vector2 mul(double d){return  new Vector2((this.x * d), this.y*d);}
    public Vector2 mul(float f){
        return new Vector2(this.x * f, this.y * f);
    }
    public Vector2 mul(Vector2 v){
        return new Vector2(this.x * v.getX(), this.y * v.getY());
    }
    public Vector2 div(float f){
        return new Vector2(this.x / f, this.y / f);
    }
    public Vector2 div(Vector2 v){
        return new Vector2(this.x / v.getX(), this.y / v.getY());
    }
    public Vector2 applyMax(Vector2 v){
        return new Vector2(Math.min(this.x, v.getX()), Math.min(this.y, v.getY()));
    }
    public static double dist(Vector2 a, Vector2 b){
        return a.sub(b).getMag();
    }
    public static Vector2 lerp(Vector2 a, Vector2 b, float t) {
        t = Math.max(0, Math.min(1, t)); // Clamp between 0 and 1
        if (t == 0) return a;
        if (t == 1) return b;

        float lerpedX = (1 - t) * a.getX() + t * b.getX();
        float lerpedY = (1 - t) * a.getY() + t * b.getY();

        return new Vector2(lerpedX, lerpedY);
    }

    @Override
    public String toString() {
        return this.x + ", " + this.y;
    }
    public boolean equals(Vector2 val){
        return (this.x == val.getX() && this.y == val.getY());
    }
    public float distanceTo(Vector2 other) {
        float dx = other.getX() - this.getX();
        float dy = other.getY() - this.getY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    public  float toAngle() {
        return (float) Math.toDegrees(Math.atan2(this.getX(), this.getY()));
    }
    public Vector2 abs(){
        return new Vector2(Math.abs(this.x),Math.abs(this.y));
    }
    public Vector2 invert() {
        return new Vector2(-this.getX() ,-this.getY());
    }

}
