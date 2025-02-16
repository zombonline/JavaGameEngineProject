package Main;

import Entity.*;
import Utility.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable{
    public static final int WORLD_SCALE = 32;
    int tileCountAcross = 15;
    int tileCountDown = 15;
    int width = WORLD_SCALE * tileCountAcross;
    int height = WORLD_SCALE * tileCountDown;

    int FPS = 60;
    private static double deltaTime = 0;

    public static double getDeltaTime() {
        return deltaTime;
    }

    KeyHandler keyHandler = new KeyHandler();
//    SpatialHashGrid spatialHashGrid = new SpatialHashGrid(10);
    Player player;
    GameObject brunge;
    Thread gameThread;


    public GamePanel() {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

    }
    public void startGameThread() throws IOException {
        player = new GameObject("Player Scrimbo").addComponent(player = new Player(keyHandler));
        player.getGameObject().addComponent(new SpriteRenderer(
                ImageIO.read(getClass().getResourceAsStream("/Resources/scrimbo.png"))));
        player.getGameObject().addComponent(new Rigidbody());
        player.getGameObject().addComponent(new Collider());
        System.out.println(player.getGameObject().transform.getPosition());


        brunge = GameObject.createNew("Brunge", new Vector2(9,9));
        brunge.addComponent(new Collider());
        brunge.addComponent(new SpriteRenderer(ImageIO.read(getClass().getResourceAsStream("/Resources/brunge.png"))));

        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS; // Time per frame in nanoseconds (16.67 ms for 60 FPS)
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        awake();

        while (gameThread != null) {
            currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            delta += (currentTime - lastTime) / drawInterval; // Accumulate time in terms of drawInterval
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                update(); // Update game logic
                repaint(); // Render the game
                delta--; // Decrement delta by 1
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }

            // Precise throttling to maintain 60 FPS
            long sleepTime = (long) ((lastTime - System.nanoTime() + drawInterval) / 1_000_000); // Calculate sleep time in milliseconds
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime); // Sleep for the calculated time
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void awake(){
        player.getGameObject().awake();
        brunge.awake();
    }
    public void update(){
        player.getGameObject().update();
        brunge.update();

    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        player.getGameObject().draw(g2d);
        brunge.draw(g2d);
    }
}
