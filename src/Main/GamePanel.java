package Main;

import Entity.GameObject;
import Entity.Player;
import Entity.Rigidbody;
import Entity.SpriteRenderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable{
    public final int tileSize = 32;
    int tileCountAcross = 10;
    int tileCountDown = 10;
    int width = tileSize * tileCountAcross;
    int height = tileSize * tileCountDown;

    int FPS = 60;

    KeyHandler keyHandler = new KeyHandler();

    Player player;
    Thread gameThread;


    public GamePanel() {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

    }
    public void startGameThread() throws IOException {
        player = new GameObject().addComponent(player = new Player(keyHandler));
        player.getGameObject().addComponent(new SpriteRenderer(
                ImageIO.read(getClass().getResourceAsStream("/Resources/scrimbo.png"))));
        player.getGameObject().addComponent(new Rigidbody());
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {
        double drawInterval = 1000000000.0/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while(gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if(delta >= 1){
                //update gameInfo
                update();
                //draw screen with updated info
                repaint();
                //sleep for rest of frame
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000){
                System.out.println("FPS: " + drawCount );
                drawCount = 0;
                timer = 0;
            }
        }
    }
    public void update(){
        player.getGameObject().update();

    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        player.getGameObject().draw(g2d);
    }
}
