package Main;

import ObjectSystem.*;
import Utility.CollisionLayer;
import Utility.Vector2;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.nativeimage.c.struct.CPointerTo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PrefabReader {
    public static GameObject getObject(String path)  {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = PrefabReader.class.getResourceAsStream(path);

        if (inputStream == null) {
            System.out.println("DEBUG: Could not find " + path + " in resources.");
            return null;
        }
        JsonNode rootNode;
        try{
            rootNode = objectMapper.readTree(inputStream);
        } catch (Exception e){
            return null;
        }
        JsonNode componentsNode = rootNode.get("components");
        GameObject newObject = GameObject.createNew(rootNode.get("name").asText(), Vector2.zero);

        if (componentsNode != null && componentsNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = componentsNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String componentName = entry.getKey();
                JsonNode componentData = entry.getValue();

                if (componentData.isArray()) {
                    // Handle multiple components of the same type
                    for (JsonNode item : componentData) {
                        System.out.println("Creating " + componentName + " for " + rootNode.get("name"));
                        Component component = buildComponent(componentName, item);
                        if (component != null) {
                            newObject.addComponent(component);
                        }
                    }
                } else {
                    // Handle single component
                    System.out.println("Creating " + componentName + " for " + rootNode.get("name"));
                    Component component = buildComponent(componentName, componentData);
                    if (component != null) {
                        newObject.addComponent(component);
                    }
                }
            }
        }
        return newObject;
    }

    private static Component buildComponent(String name, JsonNode values) {
        return switch (name) {
            case "spriteRenderer" -> buildSpriteRenderer(values);
            case "collider" -> buildCollider(values);
            case "spriteAnimator" -> buildSpriteAnimator(values);
            case "rigidbody" -> buildRigidbody(values);
            case "player" -> buildPlayer(values);
            case "cameraFollow" -> buildCameraFollow(values);
            case "crateBounce" -> buildCrateBounce(values);
            case "crateHover" -> new CrateHover();
            case "crateExplosive" -> buildCrateExplosive(values);
            case "explosion" -> buildExplosion(values);
            default -> null;
        };
    }
    private static SpriteRenderer buildSpriteRenderer(JsonNode values){
        Map<String,Object> defaultValues = SpriteRenderer.getDefaultValues();
        BufferedImage spriteImage = getBufferedImageFromString ((values.has("spriteImage") ? values.get("spriteImage").asText() : defaultValues.get("spriteImage").toString()));
        Vector2 offset = (Vector2) (values.has("offset") ? new Vector2(getFloatListFromJSONNode(values.get("offset"))) : defaultValues.get("offset"));
        return new SpriteRenderer(spriteImage, offset);
    }



    private static Collider buildCollider(JsonNode values){
        Map<String,Object> defaultValues = Collider.getDefaultValues();

        boolean isStatic = (boolean) (values.has("isStatic") ? values.get("isStatic").asBoolean() : defaultValues.get("isStatic"));

        CollisionLayer collisionLayer = (CollisionLayer) (values.has("collisionLayer") ?
                CollisionLayer.valueOf(values.get("collisionLayer").asText())
                : defaultValues.get("collisionLayer"));
        ArrayList<CollisionLayer> collisionMask = (ArrayList<CollisionLayer>) (values.has("collisionMask") ?
                CollisionLayer.fromStringList(getStringListFromJSONNode(values.get("collisionMask")))
                : defaultValues.get("collisionMask"));
        Vector2 size = (Vector2) (values.has("size") ? new Vector2(getFloatListFromJSONNode(values.get("size"))) : defaultValues.get("size"));
        Vector2 offset = (Vector2) (values.has("offset") ? new Vector2(getFloatListFromJSONNode(values.get("offset"))) : defaultValues.get("offset"));
        boolean isTrigger = (boolean) (values.has("isTrigger") ? values.get("isTrigger").asBoolean() : defaultValues.get("isTrigger"));
        return new Collider(isStatic, collisionLayer, collisionMask, size, offset, isTrigger);
    }

    private static Player buildPlayer(JsonNode values){
        Map<String,Object> defaultValues = Player.getDefaultValues();
        float speed = (float) (values.has("speed") ? (float) values.get("speed").asDouble() : defaultValues.get("speed"));
        KeyHandler keyHandler = (KeyHandler) defaultValues.get("keyHandler");
        return new Player(keyHandler, speed);
    }

    private static CameraFollow buildCameraFollow(JsonNode values){
        Map<String,Object> defaultValues = CameraFollow.getDefaultValues();
        Bounds bounds = (Bounds) (values.has("bounds") ? new Bounds(getFloatListFromJSONNode(values.get("bounds"))) :
                defaultValues.get("bounds"));
        float minBoundsFollowStrength = (float) (values.has("minBoundsFollowStrength") ? (float) values.get("minBoundsFollowStrength").asDouble() : defaultValues.get("minBoundsFollowStrength"));
        float boundsFollowStrengthScale = (float) (values.has("boundsFollowStrengthScale") ? (float) values.get("boundsFollowStrengthScale").asDouble() : defaultValues.get("boundsFollowStrengthScale"));
        float idleFollowStrength = (float) (values.has("idleFollowStrength") ? (float) values.get("idleFollowStrength").asDouble() : defaultValues.get("idleFollowStrength"));
        float idleFollowMaxDist = (float) (values.has("idleFollowMaxDist") ? (float) values.get("idleFollowMaxDist").asDouble() : defaultValues.get("idleFollowMaxDist"));
        return new CameraFollow(bounds,minBoundsFollowStrength, boundsFollowStrengthScale,idleFollowStrength,idleFollowMaxDist);
    }

    private static SpriteAnimator buildSpriteAnimator(JsonNode values){
        Map<String,Object> defaultValues = Rigidbody.getDefaultValues();
        String startingAnim = (values.has("startingAnim") ?  values.get("startingAnim").asText() : "");
        return new SpriteAnimator(Assets.getAssetPath(startingAnim));
    }
    private static Rigidbody buildRigidbody(JsonNode values){
        Map<String,Object> defaultValues = Rigidbody.getDefaultValues();
        float drag = (float) (values.has("drag") ? (float) values.get("drag").asDouble() : defaultValues.get("drag"));
        float restitution = (float) (values.has("restitution") ? (float) values.get("restitution").asDouble() : defaultValues.get("restitution"));
        float gravityScale = (float) (values.has("gravityScale") ? (float) values.get("gravityScale").asDouble() : defaultValues.get("gravityScale"));
        Vector2 maxVelocity = (Vector2) (values.has("maxVelocity") ? new Vector2(getFloatListFromJSONNode(values.get("maxVelocity"))) : defaultValues.get("maxVelocity"));
        boolean isKinematic = (boolean) (values.has("isKinematic") ? values.get("isKinematic").asBoolean() : defaultValues.get("isKinematic"));

        return new Rigidbody(drag,gravityScale,restitution,maxVelocity,isKinematic);
    }
    public static CrateBounce buildCrateBounce(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = (float) (values.has("bounceStrength") ? (float) values.get("bounceStrength").asDouble() : defaultValues.get("bounceStrength"));
        return new CrateBounce(bounceStrength);
    }
    public static CrateExplosive buildCrateExplosive(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = (float) (values.has("bounceStrength") ? (float) values.get("bounceStrength").asDouble() : defaultValues.get("bounceStrength"));
        return new CrateExplosive(bounceStrength);
    }
    public static Explosion buildExplosion(JsonNode values){
        return new Explosion();
    }
    private static BufferedImage getBufferedImageFromString(String val){
        try {
            return ImageIO.read(PrefabReader.class.getResourceAsStream(val));
        } catch (Exception e){
            System.out.println("Couldn't create image from address");
            return null;
        }
    }
    private static ArrayList<Float> getFloatListFromJSONNode(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        if (!node.isArray()) {
            return null;
        }
        return mapper.convertValue(node, new TypeReference<ArrayList<Float>>() {});
    }
    private static ArrayList<String> getStringListFromJSONNode(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        if (!node.isArray()) {
            return null;
        }
        return mapper.convertValue(node, new TypeReference<ArrayList<String>>() {});
    }


}


