package Main;

import java.awt.*;
import java.awt.image.BufferedImage;


public class GameUI {
    static BufferedImage crateIcon = AssetLoader.getInstance().getImage(Assets.Images.CRATE_BASIC);

    public static void drawUI(Graphics2D g2d){
        g2d.setColor(Color.white);
        g2d.drawImage(crateIcon,(int)Main.width-75,0, 50, 50, null);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.drawString( GamePanel.currentLevel.getCratesDestroyed() + "/" + GamePanel.currentLevel.getCratesToDestroy(),(int)Main.width-100,35);
        drawResults(g2d);
    }

    public static void drawResults(Graphics2D g2d){
        g2d.setColor(Color.white);
        int resultsboxX = 50;
        int resultsboxY = 50;
        int resultsboxWidth = Main.width-100;
        int resultsboxHeight = Main.height-100;
        g2d.fillRoundRect(resultsboxX,resultsboxY, resultsboxWidth, resultsboxHeight, 10, 10);
        
        g2d.setStroke(new BasicStroke(3)); // Set the stroke size to 3
        g2d.setColor(Color.black);
        g2d.drawRoundRect(resultsboxX, resultsboxY, resultsboxWidth, resultsboxHeight, 10, 10);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(String.valueOf("RESULTS"));
        int textHeight = metrics.getHeight();
        g2d.drawString("RESULTS", resultsboxX-(resultsboxWidth/2)-(textWidth/2), resultsboxY+(resultsboxHeight/2)-(textHeight/2));
    }
}
