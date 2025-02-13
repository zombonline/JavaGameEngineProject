package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteRenderer extends Component {
    BufferedImage spriteImage;

    public SpriteRenderer(BufferedImage spriteImage){
        this.spriteImage = spriteImage;
    }
    public void draw(Graphics2D g2d){
        g2d.drawImage(spriteImage,(int)gameObject.transform.position.getX(),(int)gameObject.transform.position.getY(),null);
    }
}
