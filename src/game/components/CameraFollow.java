package game.components;

import main.Main;
import game.components.core.Component;
import game.components.core.Transform;
import core.scene.SessionManager;
import core.utils.Bounds;
import core.utils.DebugText;
import main.GamePanel;
import core.utils.Vector2;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class CameraFollow extends Component {
    // Component references
    Transform transform;
    Rigidbody rb;

    // Variables
    private final float minFollowStrength;
    private final float maxFollowStrength;
    private final float smoothingSpeed;
    private float followStrength = 1f;
    private final Bounds bounds;
    private boolean outOfBoundsLastFrame = false;
    private double lookAhead;

    public CameraFollow(Bounds bounds, float minFollowStrength, float maxFollowStrength, float smoothingSpeed){
        this.bounds = bounds;
        this.minFollowStrength = minFollowStrength;
        this.maxFollowStrength = maxFollowStrength;
        this.smoothingSpeed = smoothingSpeed;
    }
    @Override
    public void awake() {
        getRequiredComponentReferences();
    }

    @Override
    protected void getRequiredComponentReferences() {
        transform = fetchRequiredComponent(Transform.class);
        rb = fetchRequiredComponent(Rigidbody.class);
    }
    @Override
    public void update() {
        lookAhead = calculateLookahead();
        boolean outOfBounds = isOutOfBounds();
        DebugText.logPermanently("Out of Bounds", String.valueOf(outOfBounds));

        float smoothingFactor = (float) (smoothingSpeed * GamePanel.getDeltaTime());
        followStrength += outOfBounds ? smoothingFactor : -smoothingFactor;

        followStrength = Math.clamp(followStrength, minFollowStrength, maxFollowStrength);
        Vector2 target = new Vector2(transform.getPosition().getX()+lookAhead,transform.getPosition().getY()).mul(GamePanel.WORLD_SCALE);

        DebugText.logPermanently("Camera Follow Strength", String.format("%.3f", followStrength));
        Vector2 cameraCentre = Main.camera.getCameraCentrePosition();
        Vector2 smoothedPosition = Vector2.lerp(cameraCentre, target, followStrength);
        Vector2 clampedPosition = clampToLevelBounds(smoothedPosition);
        DebugText.logPermanently("Camera Position", clampedPosition.toDp(2).toString());
        Main.camera.setPosition(clampedPosition);
        outOfBoundsLastFrame = outOfBounds;
    }

    private double calculateLookahead(){
        //get direction
        double lookAheadDirection = rb.velocity.getX();
        //lerp towards target direction
        return (lookAhead * (1.0 - 0.025f)) + (lookAheadDirection * 0.025f);
    }

    private boolean isOutOfBounds() {
        Vector2 target = new Vector2(transform.getScreenPosition().getX()+(lookAhead* GamePanel.WORLD_SCALE),transform.getScreenPosition().getY());
        Bounds screenBounds = new Bounds(
                (((float) Main.width /2) + bounds.minX),
                (((float) Main.width /2) + bounds.maxX),
                (((float) Main.height /2) + bounds.minY),
                (((float) Main.height /2) + bounds.maxY));

        float boundaryThreshold = outOfBoundsLastFrame ? 75 : 0;
        return (target.getX() < screenBounds.minX + boundaryThreshold ||
                target.getX() > screenBounds.maxX - boundaryThreshold ||
                target.getY() < screenBounds.minY + boundaryThreshold ||
                target.getY() > screenBounds.maxY - boundaryThreshold);
    }

    private static Vector2 clampToLevelBounds(Vector2 initialPosition){
        return new Vector2(
                Math.clamp(initialPosition.getX(),
                        Main.width/2- GamePanel.WORLD_SCALE/2,
                        SessionManager.getCurrentLevel().getWidth()* GamePanel.WORLD_SCALE),
                Math.clamp(initialPosition.getY(),
                        0 ,
                        SessionManager.getCurrentLevel().getHeight()* GamePanel.WORLD_SCALE));
    }

    @Override
    public void draw(Graphics2D g2d) {
//        super.draw(g2d);
//        g2d.setColor(new Color(255,255,0,100));
//        Vector2 playerPos = transform.getPosition().add(transform.getScale()).mul(Main.Main.GamePanel.WORLD_SCALE);
//        g2d.fillOval((int) playerPos.getX()-5,(int) playerPos.getY()-5,10,10);
//        Vector2 screenCenter = new Vector2(Main.Main.width /2,Main.Main.height /2);
//        g2d.fillOval((int) screenCenter.getX()-5,(int)screenCenter.getY()-5,10,10);
//
//        Bounds screenBounds = new Bounds(
//                (((float) Main.Main.width /2) + bounds.minX + threshold),
//                (((float) Main.Main.width /2) + bounds.maxX - threshold),
//                (((float) Main.Main.height /2) + bounds.minY),
//                (((float) Main.Main.height /2) + bounds.maxY));
//        g2d.drawRect((int) screenBounds.minX, (int) screenBounds.minY,
//                (int) (screenBounds.maxX - screenBounds.minX),
//                (int) (screenBounds.maxY - screenBounds.minY));
    }
    public static Map<String,Object> getDefaultValues() {
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounds" , new Bounds(-100,100,-100,100));
        defaultValues.put("minFollowStrength", 0.01f);
        defaultValues.put("maxFollowStrength", 0.08f);
        defaultValues.put("smoothingSpeed", 1f);
        return defaultValues;
    }

}
