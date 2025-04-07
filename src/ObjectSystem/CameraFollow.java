package ObjectSystem;

import Main.Main;
import Main.Bounds;
import Main.DebugText;
import Main.GamePanel;
import Utility.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class CameraFollow extends Component{
    Camera camera;
    Transform transform;
    float minFollowStrength, maxFollowStrength;
    float smoothingSpeed;
    float followStrength = 1f;
    private Bounds bounds;
    boolean outOfXBounds = false, outOfYBounds = false;
    boolean outOfBoundsLastFrame = false;

    public Vector2 lookAhead = Vector2.zero;
    public CameraFollow(Bounds bounds, float minFollowStrength, float maxFollowStrength, float smoothingSpeed){
        camera = Main.camera;
        this.bounds = bounds;
        this.minFollowStrength = minFollowStrength;
        this.maxFollowStrength = maxFollowStrength;
        this.smoothingSpeed = smoothingSpeed;
    }
    @Override
    public void awake() {
        transform = getGameObject().transform;
    }
    @Override
    public void update() {
        Vector2 playerPos = transform.getScreenPosition().sub(transform.getScreenScale().div(2));

        Bounds screenBounds = new Bounds(
                (((float) GamePanel.width /2) + bounds.minX),
                (((float) GamePanel.width /2) + bounds.maxX),
                (((float) GamePanel.height /2) + bounds.minY),
                (((float) GamePanel.height /2) + bounds.maxY));

        float threshold = 0;
        if(outOfBoundsLastFrame){
            threshold = 75;
        }
        outOfXBounds = playerPos.getX()+transform.getScreenScale().getX() < screenBounds.minX + threshold + 200 ||
                playerPos.getX() > screenBounds.maxX - threshold;
        outOfYBounds = playerPos.getY() < screenBounds.minY+threshold || playerPos.getY() > screenBounds.maxY-threshold;
        DebugText.logPermanently("Out of X Bounds", String.valueOf(outOfXBounds));
        DebugText.logPermanently("Out of Y Bounds", String.valueOf(outOfYBounds));

        if (outOfXBounds || outOfYBounds ) {
            followStrength+=smoothingSpeed;
        }
        else{
            followStrength-=smoothingSpeed;
        }
        Vector2 cameraCentre = camera.getCameraCentrePosition();
        followStrength = Math.clamp(followStrength, minFollowStrength, maxFollowStrength);
        DebugText.logPermanently("Camera Follow Strength",String.valueOf(followStrength));
        Vector2 smoothedPosition = Vector2.lerp(cameraCentre, transform.getPosition().mul(GamePanel.WORLD_SCALE), followStrength);
        camera.setPosition(smoothedPosition);
        outOfBoundsLastFrame = outOfXBounds || outOfYBounds;
    }
    public static Map<String,Object> getDefaultValues() {
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounds" , new Bounds(-100,100,-100,100));
        defaultValues.put("minFollowStrength", 0.01f);
        defaultValues.put("maxFollowStrength", 0.08f);
        defaultValues.put("smoothingSpeed", .001f);
        return defaultValues;
    }

    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        g2d.setColor(new Color(255,255,0,100));

        Bounds screenBounds = new Bounds(
                (((float) GamePanel.width /2) + bounds.minX),
                (((float) GamePanel.width /2) + bounds.maxX),
                (((float) GamePanel.height /2) + bounds.minY),
                (((float) GamePanel.height /2) + bounds.maxY));
        g2d.drawRect((int) screenBounds.minX, (int) screenBounds.minY,
                (int) (screenBounds.maxX - screenBounds.minX),
                (int) (screenBounds.maxY - screenBounds.minY));
    }
}
