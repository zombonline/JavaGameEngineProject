package ObjectSystem;

import Main.Main;
import Main.Bounds;
import Main.GamePanel;
import Utility.Vector2;
import com.sun.jdi.FloatType;

import java.awt.*;
import java.io.Console;
import java.util.HashMap;
import java.util.Map;


public class CameraFollow extends Component{
    Camera camera;
    Transform transform;
    private float minBoundsFollowStrength;
    private float boundsFollowStrengthScale;
    private float idleFollowStrength;
    private float idleFollowMaxDist;
    private Bounds bounds;
    float followEnterThreshold = 10f;
    float followExitThreshold = 10f;

    boolean outOfXBounds = false, outOfYBounds = false;
    boolean outOfBoundsLastFrame = false;

    public Vector2 lookAhead = Vector2.zero;
    public CameraFollow(Bounds bounds, float minBoundsFollowStrength, float boundsFollowStrengthScale, float idleFollowStrength, float idleFollowMaxDist){
        camera = Main.camera;
        this.bounds = bounds;
        this.minBoundsFollowStrength = minBoundsFollowStrength;
        this.boundsFollowStrengthScale = boundsFollowStrengthScale;
        this.idleFollowStrength = idleFollowStrength;
        this.idleFollowMaxDist = idleFollowMaxDist;
    }
    @Override
    public void awake() {
        transform = getGameObject().transform;
    }
    @Override
    public void update() {
        Vector2 playerPos = transform.getScreenPosition();

        Bounds screenBounds = new Bounds(
                (((float) GamePanel.width /2) + bounds.minX),
                (((float) GamePanel.width /2) + bounds.maxX),
                (((float) GamePanel.height /2) + bounds.minY),
                (((float) GamePanel.height /2) + bounds.maxY));

        float threshold = 0;
        if(outOfBoundsLastFrame){
            threshold = 75;
        }
        outOfXBounds = playerPos.getX()+(transform.getScreenScale().getX()/2) < screenBounds.minX+threshold || playerPos.getX()+(transform.getScreenScale().getX()/2) > screenBounds.maxX-threshold;
        outOfYBounds = playerPos.getY() < screenBounds.minY+threshold || playerPos.getY() > screenBounds.maxY-threshold;
        if (outOfXBounds || outOfYBounds ) {
            Vector2 cameraCentre = camera.getCameraCentrePosition();
            if (Vector2.dist(cameraCentre,transform.getPosition().mul(GamePanel.WORLD_SCALE)) < idleFollowMaxDist){return;}
            float dynamicFollowStrength = Math.max(.005f, (float) (idleFollowStrength*2 * GamePanel.getDeltaTime()));
            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, transform.getPosition().mul(GamePanel.WORLD_SCALE), dynamicFollowStrength);
            camera.setPosition(smoothedPosition);
        }
        else{
            Vector2 cameraCentre = camera.getCameraCentrePosition();
            if (Vector2.dist(cameraCentre,transform.getPosition().mul(GamePanel.WORLD_SCALE)) < idleFollowMaxDist){return;}
            float dynamicFollowStrength = Math.max(.001f, (float) (idleFollowStrength * GamePanel.getDeltaTime()));
            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, transform.getPosition().mul(GamePanel.WORLD_SCALE), dynamicFollowStrength);
            camera.setPosition(smoothedPosition);
        }
        outOfBoundsLastFrame = outOfXBounds || outOfYBounds;
    }
    public static Map<String,Object> getDefaultValues() {
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounds" , new Bounds(-100,100,-100,100));
        defaultValues.put("minBoundsFollowStrength", 0.9);
        defaultValues.put("boundsFollowStrengthScale", 0.03);
        defaultValues.put("idleFollowStrength", 0.01);
        defaultValues.put("idleFollowMaxDist", 6.0);
        return defaultValues;
    }

//    @Override
//    public void draw(Graphics2D g2d) {
//        super.draw(g2d);
//        g2d.setColor(new Color(255,255,0,100));
//
//        Bounds screenBounds = new Bounds(
//                (((float) GamePanel.width /2) + bounds.minX),
//                (((float) GamePanel.width /2) + bounds.maxX),
//                (((float) GamePanel.height /2) + bounds.minY),
//                (((float) GamePanel.height /2) + bounds.maxY));
//        g2d.drawRect((int) screenBounds.minX, (int) screenBounds.minY,
//                (int) (screenBounds.maxX - screenBounds.minX),
//                (int) (screenBounds.maxY - screenBounds.minY));
//    }
}
