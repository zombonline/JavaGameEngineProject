package Main;

import ObjectSystem.*;
import Utility.CollisionLayer;
import Utility.Vector2;
import com.fasterxml.jackson.databind.DatabindException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    public static final int WORLD_SCALE = 64;
    int tileCountAcross = 150;
    int tileCountDown = 15;
    int width = WORLD_SCALE * tileCountAcross;
    int height = WORLD_SCALE * tileCountDown;

    int FPS = 60;
    private static double deltaTime = 0;

    Thread gameThread;

    //TEMP GAME OBJECTS
//    KeyHandler keyHandler = new KeyHandler();
    GameObject player;
    ArrayList<GameObject> tiles = new ArrayList<GameObject>();
    public GamePanel() {
        this.setPreferredSize(new Dimension(960, 960));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }
    public void startGameThread() throws IOException, DatabindException {
//        player = new GameObject("Player Scrimbo").addComponent(player = new Player(keyHandler));
//        player.getGameObject().addComponent(new SpriteRenderer(
//                ImageIO.read(getClass().getResourceAsStream("/Resources/scrimbo.png"))));
//        player.getGameObject().addComponent(new Rigidbody(0.9f,0.01f,0f, new Vector2(1,.5)));
//        ArrayList<CollisionLayer> mask = new ArrayList<>();
//        mask.add(CollisionLayer.DEFAULT);
//        player.getGameObject().addComponent(new Collider(false, CollisionLayer.DEFAULT,mask,Vector2.one,Vector2.zero));
//        player.getGameObject().addComponent(new SpriteAnimator());
//        player.getComponent(SpriteAnimator.class).loadAnimation("", "test.json");
//        player.getGameObject().addComponent(new CameraFollow(new Bounds(-100,100,-100,150),0.9f, 0.0003f,0.01f,6f));

        player = PrefabReader.getObject("prefab_player.json");
//        for(int i = 0; i < tileCountAcross; i++){
//            GameObject newTile = PrefabReader.getObject("prefab_basic_tile.json");
//            newTile.transform.setPosition(new Vector2(i,tileCountDown-1));
//            tiles.add(newTile);
//        }
        tiles = TMXParser.parse();
        gameThread = new Thread(this);
        gameThread.start();
    }
    public static double getDeltaTime() {
        return deltaTime;
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
        TMXParser.parse();
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
        player.awake();
        for(GameObject tile : tiles){
            tile.awake();
        }
    }
    public void update(){
        if(player == null){return;}

        player.update();
        for(GameObject tile : tiles){
            tile.update();
        }

    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if(player == null){return;}
        player.draw(g2d);
        for(GameObject tile : tiles){
            tile.draw(g2d);
        }
    }
}
