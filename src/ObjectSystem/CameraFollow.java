package ObjectSystem;

import Main.Main;
import Main.Bounds;
import Main.GamePanel;
import Utility.Vector2;
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
    float followEnterThreshold = 50f;
    float followExitThreshold = 50f;

    boolean outOfXBounds = false, outOfYBounds = false;

    public Vector2 lookAhead = Vector2.right.mul(4f);
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
        Vector2 cameraCentre = camera.getCameraCentrePosition();
        Vector2 playerPos = transform.getScreenPosition().add(lookAhead.mul(GamePanel.WORLD_SCALE));

        float xThreshold = outOfXBounds ? followEnterThreshold : followExitThreshold;
        float yThreshold = outOfYBounds ? followEnterThreshold : followExitThreshold;
        outOfXBounds = playerPos.getX() < cameraCentre.getX() + (bounds.minX-xThreshold) || playerPos.getX() > cameraCentre.getX() + (bounds.maxX+xThreshold);
        outOfYBounds = playerPos.getY() < cameraCentre.getY() + (bounds.minY - yThreshold) || playerPos.getY() > cameraCentre.getY() + (bounds.maxY + yThreshold);

        if (outOfXBounds || outOfYBounds) {
            float distanceX = Math.abs(playerPos.getX() - cameraCentre.getX());
            float distanceY = Math.abs(playerPos.getY() - cameraCentre.getY());
            float dynamicFollowStrength = Math.max(minBoundsFollowStrength, boundsFollowStrengthScale * Math.max(distanceX, distanceY));
            System.out.println("STRONG: " + dynamicFollowStrength);
            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, playerPos, (float) (dynamicFollowStrength * GamePanel.getDeltaTime()));
            camera.setPosition(smoothedPosition);
        }
        else{
            System.out.println("WEAK");

            if (Vector2.dist(cameraCentre,playerPos) < idleFollowMaxDist){return;}
            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, playerPos, (float) (idleFollowStrength * GamePanel.getDeltaTime()));
            camera.setPosition(smoothedPosition);
        }
    }
    public static Map<String,Object> getDefaultValues() {
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("bounds" , new Bounds(-100,100,-100,100));
        defaultValues.put("minBoundsFollowStrength", 0.9);
        defaultValues.put("boundsFollowStrengthScale", 0.0003);
        defaultValues.put("idleFollowStrength", 0.01);
        defaultValues.put("idleFollowMaxDist", 6.0);
        return defaultValues;
    }
}
