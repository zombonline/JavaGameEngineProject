package ObjectSystem;

import Main.GamePanel;
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

    public SpriteRenderer(BufferedImage spriteImage){
        this.spriteImage = spriteImage;
    }
    public static Map<String,Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("spirteImage", null);
        return defaultValues;
    }
    public void draw(Graphics2D g2d){
        if(this.spriteImage == null){return;}
        Vector2 screenPos = getGameObject().transform.getScreenPosition().sub(camera.getPosition());

        if (isVisible(screenPos)) {
            int w = spriteImage.getWidth()*(GamePanel.WORLD_SCALE/spriteImage.getWidth());
            int h = spriteImage.getHeight()*(GamePanel.WORLD_SCALE/spriteImage.getHeight());
            g2d.drawImage(spriteImage,(int) screenPos.getX(), (int) screenPos.getY(), w, h,null);
        }
    }
    private boolean isVisible(Vector2 screenPos) {
        int w = spriteImage.getWidth() * GamePanel.WORLD_SCALE / spriteImage.getWidth();
        int h = spriteImage.getHeight() * GamePanel.WORLD_SCALE / spriteImage.getHeight();

        return screenPos.getX() + w > 0 &&
                screenPos.getY() + h > 0 &&
                screenPos.getX() < gamePanel.getWidth() &&
                screenPos.getY() < gamePanel.getHeight();
    }
}
