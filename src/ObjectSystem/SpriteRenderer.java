package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import static Main.Main.gamePanel;

public class SpriteRenderer extends Component {
    public BufferedImage spriteImage;
    public Vector2 offset;
    private float parallaxFactor = 1f;
    private boolean flipHorizontally = false, flipVertically = false;


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
//        System.out.println("Running draw for SpriteRenderer component " + this.hashCode() + " for GameObject " + gameObject.getName()+ " " + gameObject.hashCode());
        if (this.spriteImage == null) {
            return;
        }

        if (gameObject.getName().equals("Background Crate")) {
            parallaxFactor = 0.7f;
        }

        // Use a custom parallax factor (e.g., 0.5f for half-speed movement)
        Vector2 screenPos = gameObject.getTransform().getScreenPosition(parallaxFactor).add(offset.mul(GamePanel.WORLD_SCALE));

        if (isVisible(screenPos)) {
            int w = (int) (GamePanel.WORLD_SCALE * gameObject.getTransform().getScale().getX());
            int h = (int) (GamePanel.WORLD_SCALE * gameObject.getTransform().getScale().getY());

            // Translate to the correct screen position first
            g2d.translate(screenPos.getX(), screenPos.getY());

            // Apply transformations for flipping (only if necessary)
            if (flipHorizontally) {
                g2d.translate(w, 0);  // Move to the right of the image
                g2d.scale(-1, 1);  // Flip horizontally
            }
            if (flipVertically) {
                g2d.translate(0, h);  // Move to the bottom of the image
                g2d.scale(1, -1);  // Flip vertically
            }

            // Draw the image at the translated position with the specified transformations
            g2d.drawImage(spriteImage, 0, 0, w, h, null);

            // Reset transformations to avoid affecting subsequent drawings
            if (flipHorizontally) {
                g2d.scale(-1, 1);  // Reset horizontal scale
                g2d.translate(-w, 0);  // Reset translation
            }
            if (flipVertically) {
                g2d.scale(1, -1);  // Reset vertical scale
                g2d.translate(0, -h);  // Reset translation
            }

            // Reset the translation back to the original position
            g2d.translate(-screenPos.getX(), -screenPos.getY());
        }
    }

    private boolean isVisible(Vector2 screenPos) {
        int w = (int) (GamePanel.WORLD_SCALE*gameObject.getTransform().getScale().getX());
        int h = (int) (GamePanel.WORLD_SCALE*gameObject.getTransform().getScale().getY());

        return screenPos.getX() + w > 0 &&
                screenPos.getY() + h > 0 &&
                screenPos.getX() < gamePanel.getWidth() &&
                screenPos.getY() < gamePanel.getHeight();
    }

    public void setFlipHorizontally(boolean flipHorizontally) {
        this.flipHorizontally = flipHorizontally;
    }
    public void setFlipVertically(boolean flipVertically) {
        this.flipVertically = flipVertically;
    }
}
