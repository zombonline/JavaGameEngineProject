package core.utils;

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
    public Vector2(Vector2 originalVector2){
        this.x = originalVector2.getX();
        this.y = originalVector2.getY();
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
        this.x = Float.parseFloat(vals.get(0).toString());
        this.y = Float.parseFloat(vals.get(1).toString());
    }

    public Vector2(){
        this(0,0);
    }
    public float getX(){return x;}
    public float getY(){return y;}
    public float getMag(){return (float) (Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y,2)));}
    public Vector2 getNormalized(){return new Vector2(this.x/getMag(),this.y/getMag());}
    public void setX(float x){this.x = x;}
    public void setY(float y){this.y = y;}
    public void setX(double x){this.x = (float) x;}
    public void setY(double y){this.y = (float) y;}
    public Vector2 add(Vector2 v){
        return new Vector2(this.x + v.getX(), this.y + v.getY());
    }
    public Vector2 sub(Vector2 v){
        return new Vector2(this.x - v.getX(), this.y - v.getY());
    }
    public Vector2 mul(float f){
        return new Vector2(this.x * f, this.y * f);
    }
    public Vector2 mul(double d){
        return new Vector2(this.x * d, this.y * d);
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
    public Vector2 applyMax(Vector2 v) {
        return new Vector2(Math.min(this.x, v.getX()), Math.min(this.y, v.getY()));
    }

    public Vector2 applyMin(Vector2 v) {
        return new Vector2(Math.max(this.x, v.getX()), Math.max(this.y, v.getY()));
    }

    public static float dist(Vector2 a, Vector2 b){
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
    public Vector2 truncate() { return new Vector2(Math.floor(this.x),Math.floor(this.y));}
    public Vector2 clamp(Vector2 max) {
        float clampedX = Math.max(-max.getX(), Math.min(this.getX(), max.getX()));
        float clampedY = Math.max(-max.getY(), Math.min(this.getY(), max.getY()));
        return new Vector2(clampedX, clampedY);
    }
    public float dot(Vector2 other) {
        return this.getX() * other.getX() + this.getY() * other.getY();
    }

    public Vector2 projectOnto(Vector2 other) {
        float dotProduct = this.dot(other);
        float otherMagnitudeSquared = other.getX() * other.getX() + other.getY() * other.getY();
        float scalar = dotProduct / otherMagnitudeSquared;
        return other.mul(scalar);
    }
    public Vector2 perpendicular() {
        return new Vector2(-this.getY(), this.getX());
    }
    public Vector2 toDp(int decimalPlaces){
        return new Vector2(
                (Math.floor(this.getX()*Math.pow(10,decimalPlaces))/Math.pow(10,decimalPlaces))
                , (Math.floor(this.getY()*Math.pow(10,decimalPlaces))/Math.pow(10,decimalPlaces)));
    }
    public Vector2 toSign(){
        return new Vector2(Math.signum(this.getX()), Math.signum(this.getY()));
    }
}
