package ObjectSystem;

import Main.GamePanel;
import Main.Main;
import Utility.CollisionLayer;
import Utility.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Main.Main.camera;
import static Main.Main.gamePanel;

public class SpriteRenderer extends Component {
    public BufferedImage spriteImage;
    public Vector2 offset;
    private float parallaxFactor = 1f;

    public SpriteRenderer(BufferedImage spriteImage, Vector2 offset){
        this.spriteImage = spriteImage;
        this.offset = offset;
    }
    public SpriteRenderer(BufferedImage spriteImage){
        this.spriteImage = spriteImage;
        this.offset = Vector2.zero;
    }
    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("spriteImage", null);
        defaultValues.put("offset", Vector2.zero);
        return defaultValues;
    }
    public void draw(Graphics2D g2d) {
        if (this.spriteImage == null) { return; }
        if(gameObject.name.equals("Background Crate")){
            parallaxFactor =0.7f;
        }
        // Use a custom parallax factor (e.g., 0.5f for half-speed movement)
        Vector2 screenPos = gameObject.transform.getScreenPosition(parallaxFactor).add(offset.mul(GamePanel.WORLD_SCALE));

        if (isVisible(screenPos)) {
            int w = (int) (GamePanel.WORLD_SCALE * gameObject.transform.getScale().getX());
            int h = (int) (GamePanel.WORLD_SCALE * gameObject.transform.getScale().getY());
            g2d.drawImage(spriteImage, (int) screenPos.getX(), (int) screenPos.getY(), w, h, null);
        }
    }
    private boolean isVisible(Vector2 screenPos) {
        int w = (int) (GamePanel.WORLD_SCALE*gameObject.transform.getScale().getX());
        int h = (int) (GamePanel.WORLD_SCALE*gameObject.transform.getScale().getY());

        return screenPos.getX() + w > 0 &&
                screenPos.getY() + h > 0 &&
                screenPos.getX() < gamePanel.getWidth() &&
                screenPos.getY() < gamePanel.getHeight();
    }
}
