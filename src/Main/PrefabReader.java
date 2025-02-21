package Main;

import ObjectSystem.*;
import Utility.CollisionLayer;
import Utility.Vector2;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PrefabReader {
    public static GameObject getObject(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = PrefabReader.class.getResourceAsStream("/Resources/" + fileName);
        if (inputStream == null) {
            System.out.println("DEBUG: Could not find test.json in resources.");
        }
        JsonNode rootNode = objectMapper.readTree(inputStream);
        JsonNode componentsNode = rootNode.get("components");
        GameObject newObject = GameObject.createNew(rootNode.get("name").asText(), Vector2.zero);

        if (componentsNode != null && componentsNode.isObject()) {
            Iterator<String> fieldNames = componentsNode.fieldNames();
            while (fieldNames.hasNext()) {

                String componentName = fieldNames.next();
                JsonNode componentValue = componentsNode.get(componentName); // Get the corresponding value
                Component component = buildComponent(componentName, componentValue);
                if(component!= null){
                    newObject.addComponent(component);
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
            default -> null;
        };
    }
    private static SpriteRenderer buildSpriteRenderer(JsonNode values){
        Map<String,Object> defaultValues = SpriteRenderer.getDefaultValues();
        BufferedImage spriteImage = getBufferedImageFromString ((values.has("spriteImage") ? values.get("spriteImage").asText() : defaultValues.get("spriteImage").toString()));
        return new SpriteRenderer(spriteImage);
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
        return new Collider(isStatic, collisionLayer, collisionMask, size, offset);
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
        Vector2 offset = (Vector2) (values.has("offset") ? new Vector2(getFloatListFromJSONNode(values.get("offset"))) : defaultValues.get("offset"));
        return new CameraFollow(new Bounds(-100,100,-100,150),0.9f, 0.0003f,0.01f,6f);
    }



    private static SpriteAnimator buildSpriteAnimator(JsonNode values){
        return new SpriteAnimator();
    }
    private static Rigidbody buildRigidbody(JsonNode values){
        Map<String,Object> defaultValues = Rigidbody.getDefaultValues();
        float drag = (float) (values.has("drag") ? (float) values.get("drag").asDouble() : defaultValues.get("drag"));
        float restitution = (float) (values.has("restitution") ? (float) values.get("restitution").asDouble() : defaultValues.get("restitution"));
        float gravityScale = (float) (values.has("gravityScale") ? (float) values.get("gravityScale").asDouble() : defaultValues.get("gravityScale"));
        Vector2 maxVelocity = (Vector2) (values.has("maxVelocity") ? new Vector2(getFloatListFromJSONNode(values.get("maxVelocity"))) : defaultValues.get("maxVelocity"));

        return new Rigidbody(drag,gravityScale,restitution,maxVelocity);
    }

    private static BufferedImage getBufferedImageFromString(String val){
        try {
            return ImageIO.read(PrefabReader.class.getResourceAsStream("/Resources/"+val));
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


