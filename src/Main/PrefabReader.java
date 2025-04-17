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

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

public class PrefabReader {

    /**
     * Retrieves a {@link GameObject} from a JSON resource file located at the specified path.
     * The resource file is expected to define properties of the GameObject and its components
     * in JSON format. If the file cannot be found, or if an error occurs during parsing,
     * the method returns null.
     *
     * @param path the path to the JSON resource file containing the GameObject's data.
     *             The path should be accessible as a resource within the application.
     * @return a {@link GameObject} constructed based on the data from the JSON file,
     *         or null if the file is not found or an error occurs during parsing.
     */
    public static GameObject getObject(InputStream input)  {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try{
            rootNode = objectMapper.readTree(input);
        } catch (Exception e){
            return null;
        }
        JsonNode componentsNode = rootNode.get("components");
        GameObject newObject = new GameObject(rootNode.get("name").asText());

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
                            newObject.getTransform().setPosition(((Transform) component).getPosition());
                            newObject.getTransform().setRotation(((Transform) component).getRotation());
                            newObject.getTransform().setScale(((Transform) component).getScale());
                        } else {
                            newObject.addComponent(component);
                        }
                    }
                }
            }
        }
        return newObject;
    }

    /**
     * Constructs a specific component based on the provided name and values.
     * The method dynamically uses the name to determine which type of component
     * to build. If the name does not match any known component type, it returns null.
     * @param name the name of the component to build.
     * @param values a JsonNode containing configuration data for the component.
     *               The structure and fields of the node depend on the specific
     *               component being created.
     * @return a Component object of the specified type initialized with provided data,
     *         or null if the name does not match any component type.
     */
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
            case "levelExit" -> new LevelExit();
            case "playerComboTracker" -> new PlayerComboTracker();
            case "playerDeathHandler" -> new PlayerDeathHandler();
            default -> null;
        };
    }

    /**
     * Builds and returns a Transform object based on the given JSON node values.
     * If any of the "position", "rotation", or "scale" fields are missing in the provided JSON node,
     * default values will be used.
     *
     * @param values a JsonNode containing the transform properties such as "position", "rotation", and "scale".
     *               Each property is expected to be an array of floats representing vector values.
     * @return a Transform object initialized with the provided or default values for position, rotation, and scale.
     */
    private static Transform buildTransform(JsonNode values){
        Map<String,Object> defaultValues = Transform.getDefaultValues();
        Vector2 position = (Vector2) (values.has("position") ? new Vector2(getFloatListFromJSONNode(values.get("position"))) : defaultValues.get("position"));
        Vector2 rotation = (Vector2) (values.has("rotation") ? new Vector2(getFloatListFromJSONNode(values.get("rotation"))) : defaultValues.get("rotation"));
        Vector2 scale = (Vector2) (values.has("scale") ? new Vector2(getFloatListFromJSONNode(values.get("scale"))) : defaultValues.get("scale"));
        return new Transform(position, rotation, scale);
    }

    /**
     * Constructs and returns a SpriteRenderer object based on the properties provided
     * in the given JSON node. If the JSON node is missing any attributes, default
     * values will be used.
     *
     * @param values a JsonNode containing the properties for the SpriteRenderer.
     *               Expected fields include:
     *               - "spriteImage": a string representing the path to the sprite image.
     *               - "offset": an array of floats representing the offset vector.
     * @return a SpriteRenderer object initialized with the provided or default values
     *         for its sprite image and offset.
     */
    private static SpriteRenderer buildSpriteRenderer(JsonNode values){
        Map<String,Object> defaultValues = SpriteRenderer.getDefaultValues();
        BufferedImage spriteImage = AssetLoader.getInstance().getImage((values.has("spriteImage") ? values.get("spriteImage").asText() : defaultValues.get("spriteImage").toString()));
        Vector2 offset = (Vector2) (values.has("offset") ? new Vector2(getFloatListFromJSONNode(values.get("offset"))) : defaultValues.get("offset"));
        return new SpriteRenderer(spriteImage, offset);
    }

    /**
     * Constructs and returns a Collider object using the provided JSON node to configure its properties.
     * If any of the expected fields are missing in the JSON node, default values will be used.
     *
     * @param values a JsonNode containing the properties for the Collider. Expected fields include:
     *               - "isStatic": a boolean indicating if the collider is static.
     *               - "collisionLayer": a string representing the collision layer.
     *               - "collisionMask": an array of strings representing the collision mask layers.
     *               - "size": an array of floats representing the size vector.
     *               - "offset": an array of floats representing the offset vector.
     *               - "isTrigger": a boolean indicating if the collider is a trigger.
     * @return a Collider object initialized with the provided or default values for the specified properties.
     */
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

    /**
     * Constructs and returns a Player object using the properties specified in
     * the provided JSON node. If any of the required fields ("speed") are missing
     * in the JSON node, default values are used.
     *
     * @param values a JsonNode containing the attributes for the Player.
     *               Expected fields include:
     *               - "speed": a float representing the player's movement speed.
     *               If the field is not provided, the default speed value is used.
     * @return a Player object initialized with the provided or default properties.
     */
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
        if(startingAnim.isEmpty()){
            return new SpriteAnimator();
        }
        return new SpriteAnimator(AssetLoader.getInstance().getAnimation(startingAnim));
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
        Map<String,Object> defaultValues = CrateExplosive.getDefaultValues();
        float bounceStrength = (float) (values.has("bounceStrength") ? (float) values.get("bounceStrength").asDouble() : defaultValues.get("bounceStrength"));
        float explosionScale = (float) (values.has("explosionScale") ? (float) values.get("explosionScale").asDouble() : defaultValues.get("explosionScale"));
        return new CrateExplosive(bounceStrength, explosionScale);
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


