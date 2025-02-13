package Utility;

public class Vector2 {

    public static final Vector2 up = new Vector2(0,-1);
    public static final Vector2 down = new Vector2(0,1);
    public static final Vector2 left = new Vector2(-1,0);
    public static final Vector2 right = new Vector2(1,0);
    public static final Vector2 zero = new Vector2(0,0);

    private float x, y;
    private float normalized;
    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Vector2(){
        this(0,0);
    }
    public float getX(){return x;}
    public float getY(){return y;}
    public void setX(float x){this.x = x;}
    public void setY(float y){this.y = y;}
    public Vector2 add(Vector2 v){
        return new Vector2(this.x + v.getX(), this.y + v.getY());
    }
    public Vector2 sub(Vector2 v){
        return new Vector2(this.x - v.getX(), this.y - v.getY());
    }
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

    @Override
    public String toString() {
        return this.x + ", " + this.y;
    }
}
