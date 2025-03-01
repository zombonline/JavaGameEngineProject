package ObjectSystem;

import Main.Main;
import Main.Bounds;
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
        Vector2 playerPos = transform.getScreenPosition();


        boolean outOfXBounds = playerPos.getX() < cameraCentre.getX() + bounds.minX || playerPos.getX() > cameraCentre.getX() + bounds.maxX;
        boolean outOfYBounds = playerPos.getY() < cameraCentre.getY() + bounds.minY || playerPos.getY() > cameraCentre.getY() + bounds.maxY;

        if (outOfXBounds || outOfYBounds) {
            float distanceX = Math.abs(playerPos.getX() - cameraCentre.getX());
            float distanceY = Math.abs(playerPos.getY() - cameraCentre.getY());
            float dynamicFollowStrength = Math.min(minBoundsFollowStrength, boundsFollowStrengthScale * Math.max(distanceX, distanceY));

            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, playerPos, dynamicFollowStrength);
            camera.setPosition(smoothedPosition);
        }
        else{
            if (Vector2.dist(cameraCentre,playerPos) < idleFollowMaxDist){return;}
            Vector2 smoothedPosition = Vector2.lerp(cameraCentre, playerPos, idleFollowStrength);
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
