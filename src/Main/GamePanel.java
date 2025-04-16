package Main;

import ObjectSystem.*;
import ObjectSystem.Component;
import ObjectSystem.Image;
import Utility.Vector2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable{
    public static final int WORLD_SCALE = 100;
    public static final int FPS = 60;
    private static double deltaTime = 0;
    Thread gameThread;

    public static LevelData currentLevel;

    boolean running = false;
    boolean paused = false;
    public void startGameThread(String level){
        currentLevel = LevelLoader.parse(level);
        running = true;
        if(gameThread == null){
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
    public static double getDeltaTime() {
        return deltaTime;
    }
    @Override
    public void run() {
        gameThread = Thread.currentThread();
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        while (running) {
            while (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentTime = System.nanoTime();
            //if thread has run for 5 seconds


            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            delta += (currentTime - lastTime) / drawInterval; // Accumulate time in terms of drawInterval
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1) {
                try {
                    awake(); // Call awake (only new objects will run this)
                    update(); // Update game logic
                    repaint(); // Render the game
                    destroyObjects(); //Remove objects marked for destroy at end of frame
                } catch (Exception e) {
                    e.printStackTrace();
                }
                delta--; // Decrement delta by 1
                drawCount++;
            }
            if (timer >= 1000000000) {
                DebugText.logPermanently("FPS", String.valueOf(drawCount));

                drawCount = 0;
                timer = 0;
            }
            // Precise throttling to maintain 60 FPS
            long sleepTime = (long) ((lastTime - System.nanoTime() + drawInterval) / 1_000_000); // Calculate sleep time in milliseconds
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime); // Sleep for the calculated time
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    public void awake(){
        try {
            for (GameObject gameObject : currentLevel.gameObjectsToAwake) {
                gameObject.awake();
                currentLevel.activeGameObjects.add(gameObject);
            }
            currentLevel.gameObjectsToAwake.clear();
        } catch (Exception e) {
            return;
        }
    }
    public void update(){
        try {
            for (GameObject gameObject : currentLevel.activeGameObjects) {
                if (!running) {
                    return;
                }
                gameObject.update();
            }
        } catch (Exception e) {
            return;
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        try {
            for (GameObject gameObject : currentLevel.activeGameObjects) {
                gameObject.draw(g2d);
            }
            DebugText.drawDebugText(g);
            GameUI.drawUI(g2d);
        } catch (Exception e) {
            return;
        }
    }
    public void destroyObjects(){
        try {
            for (GameObject gameObject :currentLevel.gameObjectsToDestroy) {
                for (Component component : gameObject.getAllComponents()) {
                    component.onDestroy();
                }
                currentLevel.activeGameObjects.remove(gameObject);
            }
            currentLevel.gameObjectsToDestroy.clear();
        } catch (Exception e) {
            return;
        }
    }
}
