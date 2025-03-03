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
    public Vector2 offset;
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
    public void draw(Graphics2D g2d){
        if(this.spriteImage == null){return;}
        Vector2 screenPos = getGameObject().transform.getScreenPosition().sub(camera.getPosition()).add(offset.mul(GamePanel.WORLD_SCALE));

        if (isVisible(screenPos)) {
            int w = GamePanel.WORLD_SCALE;
            int h = (int) (GamePanel.WORLD_SCALE*gameObject.transform.getScale().getY());
            g2d.drawImage(spriteImage,(int) screenPos.getX(), (int) screenPos.getY(), w, h,null);
        }
    }
    private boolean isVisible(Vector2 screenPos) {
        int w = GamePanel.WORLD_SCALE;
        int h = GamePanel.WORLD_SCALE;

        return screenPos.getX() + w > 0 &&
                screenPos.getY() + h > 0 &&
                screenPos.getX() < gamePanel.getWidth() &&
                screenPos.getY() < gamePanel.getHeight();
    }
}
