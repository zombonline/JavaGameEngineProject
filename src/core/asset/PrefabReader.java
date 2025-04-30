package core.asset;

import core.utils.Bounds;
import game.components.crate.core.Crate;
import game.enums.CollisionLayer;
import core.utils.Vector2;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.input.KeyHandler;
import game.components.*;
import game.components.core.Component;
import game.components.core.Transform;
import game.components.crate.*;
import game.components.player.Player;
import game.components.player.PlayerAnimation;
import game.components.player.PlayerComboTracker;
import game.components.player.PlayerDeathHandler;
import game.components.rendering.SpriteAnimator;
import game.components.rendering.SpriteRenderer;
import game.components.rendering.SubImageSpriteSetter;
import game.entities.GameObject;

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
     * @param input the path to the JSON resource file containing the GameObject's data.
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
                        newObject.addComponent(buildComponent(componentName, item));
                    }
                } else {
                    component = buildComponent(componentName, componentData);
                    if(component!= null){
                        if(component instanceof Transform){
                            newObject.getTransform().setPosition(((Transform) component).getPosition());
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
            case "crateReinforced" -> buildCrateReinforced(values);
            case "crateHover" -> buildCrateHover(values);
            case "crateMoving" -> buildCrateMoving(values);
            case "crateExplosive" -> buildCrateExplosive(values);
            case "crateTeleport" -> buildCrateTeleport(values);
            case "explosion" -> buildExplosion(values);
            case "playerAnimator" -> new PlayerAnimation();
            case "levelExit" -> new LevelExit();
            case "playerComboTracker" -> buildPlayerComboTracker(values);
            case "playerDeathHandler" -> new PlayerDeathHandler();
            case "npcDialogueHandler" -> new NPCDialogueHandler();
            case "subImageSpriteSetter" -> new SubImageSpriteSetter();
            case "crateScaffold" -> new CrateScaffold();
            case "crate" -> new Crate();
            case "crateMetalMoving" -> buildCrateMetalMoving(values);
            case "oneShotAnimationObject" -> new OneShotAnimationObject();
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
        Vector2 position = getVector2("position", values, defaultValues);
        Vector2 scale = getVector2("scale", values, defaultValues);
        return new Transform(position, scale);
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
        Vector2 offset = getVector2("offset", values, defaultValues);
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

        boolean isStatic = getBool("isStatic", values, defaultValues);

        CollisionLayer collisionLayer = (CollisionLayer) (values.has("collisionLayer") ?
                CollisionLayer.valueOf(values.get("collisionLayer").asText())
                : defaultValues.get("collisionLayer"));
        ArrayList<CollisionLayer> collisionMask = (ArrayList<CollisionLayer>) (values.has("collisionMask") ?
                CollisionLayer.fromStringList(getStringListFromJSONNode(values.get("collisionMask")))
                : defaultValues.get("collisionMask"));
        Vector2 size = getVector2("size", values, defaultValues);
        Vector2 offset = getVector2("offset", values, defaultValues);
        boolean isTrigger = getBool("isTrigger", values, defaultValues);
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
        float speed = getFloat("speed", values, defaultValues);
        KeyHandler keyHandler = (KeyHandler) defaultValues.get("keyHandler");
        float jumpPressTime = getFloat("jumpPressTime", values, defaultValues);
        float jumpCoyoteTime = getFloat("jumpCoyoteTime", values, defaultValues);
        return new Player(keyHandler, speed, jumpPressTime, jumpCoyoteTime);
    }

    private static CameraFollow buildCameraFollow(JsonNode values){
        Map<String,Object> defaultValues = CameraFollow.getDefaultValues();
        Bounds bounds = (Bounds) (values.has("bounds") ? new Bounds(getFloatListFromJSONNode(values.get("bounds"))) :
                defaultValues.get("bounds"));
        float minFollowStrength = getFloat("minFollowStrength", values, defaultValues);
        float maxFollowStrength = getFloat("maxFollowStrength", values, defaultValues);
        float smoothingSpeed = getFloat("smoothingSpeed", values, defaultValues);
        return new CameraFollow(bounds,minFollowStrength,maxFollowStrength,smoothingSpeed);
    }

    private static SpriteAnimator buildSpriteAnimator(JsonNode values){
        Map<String,Object> defaultValues = SpriteAnimator.getDefaultValues();
        String startingAnim = getString("startingAnim", values, defaultValues);
        System.out.println("startingAnim " + startingAnim);
        return new SpriteAnimator(AssetLoader.getInstance().getAnimation(startingAnim));
    }
    private static Rigidbody buildRigidbody(JsonNode values){
        Map<String,Object> defaultValues = Rigidbody.getDefaultValues();
        float drag = getFloat("drag", values, defaultValues);
        float restitution = getFloat("restitution", values, defaultValues);
        float gravityScale = getFloat("gravityScale", values, defaultValues);
        Vector2 maxVelocity = getVector2("maxVelocity", values, defaultValues);
        boolean isKinematic = getBool("isKinematic", values, defaultValues);
        float mass = getFloat("mass",values,defaultValues);
        return new Rigidbody(drag,gravityScale,restitution,maxVelocity,isKinematic, mass);
    }
    public static CrateBounce buildCrateBounce(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        int hitsToDestroy = getInt("hitsToDestroy", values, defaultValues);
        return new CrateBounce(bounceStrength, hitsToDestroy);
    }
    public static CrateReinforced buildCrateReinforced(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        return new CrateReinforced(bounceStrength);
    }
    public static CrateExplosive buildCrateExplosive(JsonNode values){
        Map<String,Object> defaultValues = CrateExplosive.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        float explosionScale = getFloat("explosionScale", values,defaultValues);
        return new CrateExplosive(bounceStrength, explosionScale);
    }
    public static CrateHover buildCrateHover(JsonNode values){
        Map<String,Object> defaultValues = CrateHover.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        int hitsToDestroy = getInt("hitsToDestroy", values, defaultValues);
        float moveSpeed = getFloat("moveSpeed", values, defaultValues);
        float moveDistance = getFloat("moveDistance", values, defaultValues);
        Vector2 direction = getVector2("direction", values, defaultValues);
        return new CrateHover(bounceStrength,hitsToDestroy,moveSpeed, moveDistance, direction);
    }
    public static CrateMoving buildCrateMoving(JsonNode values){
        Map<String,Object> defaultValues = CrateMoving.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        int hitsToDestroy = getInt("hitsToDestroy", values, defaultValues);
        float moveSpeed = getFloat("moveSpeed", values, defaultValues);
        float moveDistance = getFloat("moveDistance", values, defaultValues);
        Vector2 direction = getVector2("direction", values, defaultValues);
        BufferedImage horizontalSprite = getImage("horizontalSprite",values,defaultValues);
        BufferedImage verticalSprite = getImage("verticalSprite",values,defaultValues);
        return new CrateMoving(bounceStrength,hitsToDestroy,moveSpeed,moveDistance,direction,horizontalSprite,verticalSprite);
    }
    public static CrateMetalMoving buildCrateMetalMoving(JsonNode values){
        Map<String,Object> defaultValues = CrateMoving.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        int hitsToDestroy = getInt("hitsToDestroy", values, defaultValues);
        float moveSpeed = getFloat("moveSpeed", values, defaultValues);
        float moveDistance = getFloat("moveDistance", values, defaultValues);
        Vector2 direction = getVector2("direction", values, defaultValues);
        BufferedImage horizontalSprite = getImage("horizontalSprite",values,defaultValues);
        BufferedImage verticalSprite = getImage("verticalSprite",values,defaultValues);
        return new CrateMetalMoving(moveSpeed,moveDistance,direction,horizontalSprite,verticalSprite);
    }
    public static CrateTeleport buildCrateTeleport(JsonNode values){
        Map<String,Object> defaultValues = CrateBounce.getDefaultValues();
        float bounceStrength = getFloat("bounceStrength", values, defaultValues);
        return new CrateTeleport(bounceStrength);
    }
    public static Explosion buildExplosion(JsonNode values){
        return new Explosion();
    }
    private static ArrayList<Double> getFloatListFromJSONNode(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        if (!node.isArray()) {
            return null;
        }
        return mapper.convertValue(node, new TypeReference<ArrayList<Double>>() {});
    }
    private static PlayerComboTracker buildPlayerComboTracker(JsonNode values){
        Map<String,Object> defaultValues = PlayerComboTracker.getDefaultValues();
        float comboDisplayTime = getFloat("comboDisplayTime", values, defaultValues);
        float startFontSize = getFloat("startFontSize", values, defaultValues);
        float fontSizeIncrement = getFloat("fontSizeIncrement", values, defaultValues);
        return new PlayerComboTracker(comboDisplayTime, startFontSize, fontSizeIncrement);
    }
    private static ArrayList<String> getStringListFromJSONNode(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        if (!node.isArray()) {
            return null;
        }
        return mapper.convertValue(node, new TypeReference<ArrayList<String>>() {});
    }

    private static Vector2 getVector2(String propertyName, JsonNode values, Map<String,Object> defaultValues){
        return (Vector2) (values.has(propertyName) ? new Vector2(getFloatListFromJSONNode(values.get(propertyName))) : defaultValues.get(propertyName));
    }
    private static float getFloat(String propertyName, JsonNode values, Map<String,Object> defaultValues){
        return  (float) (values.has(propertyName) ? (float) values.get(propertyName).asDouble() : defaultValues.get(propertyName));
    }
    private static int getInt(String propertyName, JsonNode values, Map<String,Object> defaultValues){
        return  (values.has(propertyName) ? values.get(propertyName).asInt() : (int) defaultValues.get(propertyName));
    }
    private static boolean getBool(String propertyName, JsonNode values, Map<String,Object> defaultValues){
        return  (boolean) (values.has(propertyName) ? values.get(propertyName).asBoolean() : defaultValues.get(propertyName));
    }
    private static String getString(String propertyName, JsonNode values, Map<String,Object> defaultValues) {
        return  (values.has(propertyName) ? values.get(propertyName).asText() : defaultValues.get(propertyName).toString());
    }
    private static BufferedImage getImage(String propertyName, JsonNode values, Map<String,Object> defaultValues){
        return AssetLoader.getInstance().getImage(values.has(propertyName) ? values.get(propertyName).asText() : defaultValues.get(propertyName).toString());
    }
}


