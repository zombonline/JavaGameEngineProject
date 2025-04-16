package ObjectSystem;

import Main.Main;
import Main.Bounds;
import Main.DebugText;
import Main.GamePanel;
import Utility.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class CameraFollow extends Component{
    Camera camera;
    Transform transform;
    Rigidbody rb;
    float minFollowStrength, maxFollowStrength;
    float smoothingSpeed;
    float followStrength = 1f;
    private Bounds bounds;
    boolean outOfXBounds = false, outOfYBounds = false;
    boolean outOfBoundsLastFrame = false;
    float threshold = 0;
    double lookAhead;


    public CameraFollow(Bounds bounds, float minFollowStrength, float maxFollowStrength, float smoothingSpeed){
        camera = Main.camera;
        this.bounds = bounds;
        this.minFollowStrength = minFollowStrength;
        this.maxFollowStrength = maxFollowStrength;
        this.smoothingSpeed = smoothingSpeed;
    }
    @Override
    public void awake() {

        transform = getGameObject().getTransform();
        rb = getComponent(Rigidbody.class);
    }
    @Override
    public void update() {
        float lookAheadTarget = rb.velocity.getX();
        lookAhead = (lookAhead * (1.0 - 0.025f)) + (lookAheadTarget * 0.025f);
        Vector2 playerPos = new Vector2(transform.getScreenPosition().getX()+(lookAhead*GamePanel.WORLD_SCALE),transform.getScreenPosition().getY());
        Bounds screenBounds = new Bounds(
                (((float) Main.width /2) + bounds.minX),
                (((float) Main.width /2) + bounds.maxX),
                (((float) Main.height /2) + bounds.minY),
                (((float) Main.height /2) + bounds.maxY));

        if(outOfBoundsLastFrame){
            threshold = 75;
        } else{
            threshold = 0;
        }
        outOfXBounds = playerPos.getX() < screenBounds.minX + threshold||
                playerPos.getX() > screenBounds.maxX - threshold;
        outOfYBounds = playerPos.getY() < screenBounds.minY+threshold || playerPos.getY() > screenBounds.maxY-threshold;
        DebugText.logPermanently("Out of X Bounds", String.valueOf(outOfXBounds));
        DebugText.logPermanently("Out of Y Bounds", String.valueOf(outOfYBounds));

        if (outOfXBounds || outOfYBounds ) {
            followStrength+=smoothingSpeed * GamePanel.getDeltaTime();
        }
        else{
            followStrength-=smoothingSpeed * GamePanel.getDeltaTime();
        }

        Vector2 cameraCentre = camera.getCameraCentrePosition();
        followStrength = Math.clamp(followStrength, 0, maxFollowStrength);
        Vector2 target = new Vector2(transform.getPosition().getX()+lookAhead,transform.getPosition().getY()).mul(GamePanel.WORLD_SCALE);
        DebugText.logPermanently("Left: ", Boolean.toString(target.getX() < cameraCentre.getX()));

//        //There is some dodgy offset happening here, this is a dirty fix that I'll probably leave in :(
//        if(target.getX()<cameraCentre.getX()){
//            followStrength*=0.9375f;
//        }
        DebugText.logPermanently("Camera Follow Strength", String.format("%.3f", followStrength));
        Vector2 smoothedPosition = Vector2.lerp(cameraCentre, target, followStrength);
        camera.setPosition(smoothedPosition);
        outOfBoundsLastFrame = outOfXBounds;
    }
    public static Map<String,Object> getDefaultValues() {
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounds" , new Bounds(-100,100,-100,100));
        defaultValues.put("minFollowStrength", 0.01f);
        defaultValues.put("maxFollowStrength", 0.08f);
        defaultValues.put("smoothingSpeed", 1f);
        return defaultValues;
    }

    @Override
    public void draw(Graphics2D g2d) {
//        super.draw(g2d);
//        g2d.setColor(new Color(255,255,0,100));
//        Vector2 playerPos = transform.getPosition().add(transform.getScale()).mul(GamePanel.WORLD_SCALE);
//        g2d.fillOval((int) playerPos.getX()-5,(int) playerPos.getY()-5,10,10);
//        Vector2 screenCenter = new Vector2(Main.width /2,Main.height /2);
//        g2d.fillOval((int) screenCenter.getX()-5,(int)screenCenter.getY()-5,10,10);
//
//        Bounds screenBounds = new Bounds(
//                (((float) Main.width /2) + bounds.minX + threshold),
//                (((float) Main.width /2) + bounds.maxX - threshold),
//                (((float) Main.height /2) + bounds.minY),
//                (((float) Main.height /2) + bounds.maxY));
//        g2d.drawRect((int) screenBounds.minX, (int) screenBounds.minY,
//                (int) (screenBounds.maxX - screenBounds.minX),
//                (int) (screenBounds.maxY - screenBounds.minY));
    }
}
