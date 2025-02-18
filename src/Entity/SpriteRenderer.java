package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteRenderer extends Component {
    BufferedImage spriteImage;

    public SpriteRenderer(BufferedImage spriteImage){
        this.spriteImage = spriteImage;
    }
    public SpriteRenderer(){
        this.spriteImage = null;
    }
    public void draw(Graphics2D g2d){
        if(this.spriteImage == null){return;}
        g2d.drawImage(spriteImage,(int)gameObject.transform.getScreenPosition().getX(),(int)gameObject.transform.getScreenPosition().getY(),null);
    }
}
