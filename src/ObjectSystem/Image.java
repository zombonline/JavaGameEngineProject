package ObjectSystem;

import Main.GamePanel;
import Main.KeyHandler;
import Main.Main;
import Utility.Vector2;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;

public class Image extends Component {
    BufferedImage sprite;
    Vector2 screenPosition, screenScale;
    KeyHandler keyHandler;
    public Image(BufferedImage sprite, Vector2 screenPosition, Vector2 screenScale) {
        this.sprite = sprite;
        this.screenPosition = screenPosition;
        this.screenScale = screenScale;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked");
                if (isMouseInBounds(e.getPoint())) {
                    System.out.println("Mouse clicked on Image at: " + e.getPoint());
                    onImageClick(e); // Custom behavior when image is clicked
                }
            }
        });

    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.drawImage(sprite, (int) screenPosition.getX(), (int) screenPosition.getY(), (int) screenScale.getX(), (int) screenScale.getY(), null);
    }

    public boolean isMouseInBounds(Point mousePosition) {
        int x = (int) screenPosition.getX();
        int y = (int) screenPosition.getY();
        int width = (int) screenScale.getX();
        int height = (int) screenScale.getY();
        return mousePosition.x >= x && mousePosition.x <= x + width &&
                mousePosition.y >= y && mousePosition.y <= y + height;
    }
    // Define custom behavior when the image is clicked
    private void onImageClick(MouseEvent e) {
        // Example: Print a message or trigger some logic
        System.out.println("Image clicked at: " + e.getPoint());
    }

}
