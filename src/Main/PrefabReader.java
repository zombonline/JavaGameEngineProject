package Main;

import ObjectSystem.*;
import Utility.CollisionLayer;
import Utility.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            case "sprite_renderer" -> buildSpriteRenderer(values);
            case "collider" -> buildCollider(values);
            case "sprite_animator" -> buildSpriteAnimator(values);
            case "rididbody" -> buildRigidbody(values);
            default -> null;
        };
    }
    private static SpriteRenderer buildSpriteRenderer(JsonNode values){
        BufferedImage spriteImage = null;
        if (!values.has("spriteImage")) {
            return new SpriteRenderer(null);
        }
        try{
            spriteImage = ImageIO.read(PrefabReader.class.getResourceAsStream("/Resources/"+values.get("spriteImage").asText()));
        } catch (Exception e){
            e.printStackTrace();
        }
        return new SpriteRenderer(spriteImage);
    }
    private static Collider buildCollider(JsonNode values){
        boolean isStatic = false;
        CollisionLayer collisionLayer = CollisionLayer.DEFAULT ;
        List<CollisionLayer> collisionMask = new ArrayList<CollisionLayer>();;
        collisionMask.add(CollisionLayer.DEFAULT);
        Vector2 size = Vector2.one; //multipled by world_scale
        Vector2 offset = Vector2.zero; //multiplied by world_scale
        return new Collider();
    }
    private static SpriteAnimator buildSpriteAnimator(JsonNode values){
        return new SpriteAnimator();
    }
    private static Rigidbody buildRigidbody(JsonNode values){
        float drag = 0;
        float restitution = 0;
        float gravityScale = 0;
        Vector2 maxVelocity = null;
        if(values.has("drag")) {drag = values.get("drag").floatValue();}
        if(values.has("restitution")) {restitution = values.get("restitution").floatValue();}
        if(values.has("gravityScale")) {gravityScale = values.get("gravityScale").floatValue();}
        if(values.has("maxVelocity")) {maxVelocity = new Vector2(values.get("maxVelocity").asText());}
        return new Rigidbody(drag,restitution,gravityScale,maxVelocity);
    }

    //MIGHT BE WORTH GIVING ALL COMPONENTS A GET DEFAULT VALUES FUNCTION SO THAT I AM NOT DEFINING THE DEFAULT VALUES IN THIS CALSS

    //
     //
     //
}


