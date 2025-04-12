package Main;

import ObjectSystem.*;
import ObjectSystem.Crate.CrateBounce;
import ObjectSystem.Crate.CrateExplosive;
import ObjectSystem.Crate.CrateHover;
import Utility.CollisionLayer;
import Utility.Vector2;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
                Component component = null;
                if (componentData.isArray()) {
                    for (JsonNode item : componentData) {
                        System.out.println("Creating " + componentName + " for " + rootNode.get("name"));
                        newObject.addComponent(buildComponent(componentName, item));
                    }
                } else {
                    System.out.println("Creating " + componentName + " for " + rootNode.get("name"));
                    component = buildComponent(componentName, componentData);
                    if(component!= null){
                        if(component instanceof Transform){
                            newObject.transform.setPosition(((Transform) component).getPosition());
                            newObject.transform.setRotation(((Transform) component).getRotation());
                            newObject.transform.setScale(((Transform) component).getScale());
                        } else {
                            newObject.addComponent(component);
                        }
                    }
                }
            }
        }
        return newObject;
    }

    private static Component buildComponent(String name, JsonNode values) {
        return switch (name) {
            case "transform" -> buildTransform(values);
            case "spriteRenderer" -> buildSpriteRenderer(values);
            case "collider" -> buildCollider(values);
            case "spriteAnimator" -> buildSpriteAnimator(values);
            case "rigidbody" -> buildRigidbody(values);
            case "player" -> buildPlayer(values);
            case "cameraFollow" -> buildCameraFollow(values);
            case "crateBounce" -> buildCrateBounce(values);
            case "crateHover" -> buildCrateHover(values);
            case "crateExplosive" -> buildCrateExplosive(values);
            case "explosion" -> buildExplosion(values);
            case "playerAnimator" -> new PlayerAnimation();
            default -> null;
        };
    }

    private static Transform buildTransform(JsonNode values){
        Map<String,Object> defaultValues = Transform.getDefaultValues();
        Vector2 position = (Vector2) (values.has("position") ? new Vector2(getFloatListFromJSONNode(values.get("position"))) : defaultValues.get("position"));
        Vector2 rotation = (Vector2) (values.has("rotation") ? new Vector2(getFloatListFromJSONNode(values.get("rotation"))) : defaultValues.get("rotation"));
        Vector2 scale = (Vector2) (values.has("scale") ? new Vector2(getFloatListFromJSONNode(values.get("scale"))) : defaultValues.get("scale"));
        return new Transform(position, rotation, scale);
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
        float minFollowStrength = (float) (values.has("minFollowStrength") ? (float) values.get("minFollowStrength").asDouble() : defaultValues.get("minFollowStrength"));
        float maxFollowStrength = (float) (values.has("maxFollowStrength") ? (float) values.get("maxFollowStrength").asDouble() : defaultValues.get("maxFollowStrength"));
        float smoothingSpeed = (float) (values.has("smoothingSpeed") ? (float) values.get("smoothingSpeed").asDouble() : defaultValues.get("smoothingSpeed"));
        return new CameraFollow(bounds,minFollowStrength,maxFollowStrength,smoothingSpeed);
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
        int hitsToDestroy = (values.has("hitsToDestroy") ? values.get("hitsToDestroy").asInt() : (int) defaultValues.get("hitsToDestroy"));
        return new CrateBounce(bounceStrength, hitsToDestroy);
    }
    public static CrateExplosive buildCrateExplosive(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = (float) (values.has("bounceStrength") ? (float) values.get("bounceStrength").asDouble() : defaultValues.get("bounceStrength"));
        return new CrateExplosive(bounceStrength);
    }
    public static CrateHover buildCrateHover(JsonNode values){
        Map<String,Object> defaultValues = CrateHover.getDefaultValues();
        float bounceStrength = (float) (values.has("bounceStrength") ? (float) values.get("bounceStrength").asDouble() : defaultValues.get("bounceStrength"));
        int hitsToDestroy = (values.has("hitsToDestroy") ? values.get("hitsToDestroy").asInt() : (int) defaultValues.get("hitsToDestroy"));
        float hoverSpeed = (float) (values.has("hoverSpeed") ? (float) values.get("hoverSpeed").asDouble() : defaultValues.get("hoverSpeed"));
        float hoverDistance = (float) (values.has("hoverDistance") ? (float) values.get("hoverDistance").asDouble() : defaultValues.get("hoverDistance"));
        return new CrateHover(bounceStrength,hitsToDestroy,hoverSpeed,hoverDistance);
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


