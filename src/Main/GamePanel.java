package Main;

import ObjectSystem.*;
import ObjectSystem.Component;
import Utility.Vector2;
import com.fasterxml.jackson.databind.DatabindException;
import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    public static final int WORLD_SCALE = 100;
    public static final int width = 1864;
    public static final int height = 1024;
    int FPS = 60;
    private static double deltaTime = 0;
    Thread gameThread;

    GameObject player;
    public static ArrayList<GameObject> gameObjectsToAwake = new ArrayList<GameObject>();
    public static ArrayList<GameObject> activeGameObjects = new ArrayList<GameObject>();
    public static ArrayList<GameObject> gameObjectsToDestroy = new ArrayList<GameObject>();
    public GamePanel() {
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.lightGray);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }
    public void startGameThread() throws IOException, DatabindException {
        //temporary creating objects (will be done with a level file)
        TileMapReader.parse();
        player = PrefabReader.getObject("/Resources/Prefabs/prefab_player.json");
        gameThread = new Thread(this);
        gameThread.start();
    }
    public static double getDeltaTime() {
        return deltaTime;
    }
    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            delta += (currentTime - lastTime) / drawInterval; // Accumulate time in terms of drawInterval
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1) {
                awake(); // Call awake (only new objects will run this)
                update(); // Update game logic
                repaint(); // Render the game
                destroyObjects(); //Remove objects marked for destroy at end of frame
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
        for(GameObject gameObject : gameObjectsToAwake){
            gameObject.awake();
            activeGameObjects.add(gameObject);
        }
        gameObjectsToAwake.clear();
    }
    public void update(){
        for(GameObject gameObject : activeGameObjects){
            gameObject.update();
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDebugText(g);
        Graphics2D g2d = (Graphics2D) g;
        for(GameObject gameObject : activeGameObjects){
            gameObject.draw(g2d);
            Vector2 pos = gameObject.transform.getPosition().mul(GamePanel.WORLD_SCALE).sub(Main.camera.getPosition());
        }
    }
    public void drawDebugText(Graphics g){
        g.drawString("Position: " + player.transform.getPosition().toString(), 10,10);
        g.drawString("Velocity: " + player.getComponent(Rigidbody.class).velocity, 10,20);


    }
    public void destroyObjects(){
        for(GameObject gameObject : gameObjectsToDestroy){
            for(Component component : gameObject.getAllComponents()){
                component.onDestroy();
            }
            activeGameObjects.remove(gameObject);
        }
        gameObjectsToDestroy.clear();
    }
}
