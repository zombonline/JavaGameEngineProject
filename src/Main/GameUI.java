package Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class GameUI {
    static BufferedImage crateIcon;

    static {
        try {
            crateIcon = ImageIO.read(Objects.requireNonNull(GameUI.class.getResourceAsStream(Assets.Images.CRATE_BASIC)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void drawUI(Graphics2D g2d){
        g2d.setColor(Color.white);
        g2d.drawImage(crateIcon,(int)Main.width-75,0, 50, 50, null);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.drawString( GamePanel.currentLevel.getCratesDestroyed() + "/" + GamePanel.currentLevel.getCratesToDestroy(),(int)Main.width-100,35);

    }
}
