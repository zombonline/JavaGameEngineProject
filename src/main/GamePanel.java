package main;

import game.components.core.Component;
import core.asset.Assets;
import core.utils.DebugText;
import core.asset.AssetLoader;
import core.scene.SessionManager;
import core.ui.GameUI;
import game.entities.GameObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    public static int WORLD_SCALE = Math.min(Main.width, Main.height)/12;
    public static final int FPS =60;
    private static double deltaTime = 0;
    Thread gameThread;
    BufferedImage gamePanelBackground;
    static boolean running = false;
    private static boolean gamePaused = false;
    public void startGameThread(){
        SessionManager.LoadLevelByInt(3);
        running = true;
        if(gameThread == null){
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
    public static void setGamePaused(boolean val) {
        gamePaused = val;
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
            currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // Convert to seconds
            delta += (currentTime - lastTime) / drawInterval; // Accumulate time in terms of drawInterval
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1) {
                try {
                    awake(); // Call awake (only new objects will run this)
                    start();
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
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().gameObjectsToAwake);
        for (GameObject gameObject : snapshot) {
            if(gameObject==null){continue;}
            gameObject.awake();
            SessionManager.getCurrentLevel().activeGameObjects.add(gameObject);
            SessionManager.getCurrentLevel().gameObjectsToStart.add(gameObject);
        }
        SessionManager.getCurrentLevel().gameObjectsToAwake.removeAll(snapshot);
    }
    public void start(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().gameObjectsToStart);
        for (GameObject gameObject : snapshot) {
            if(gameObject==null){continue;}
            gameObject.start();
        }
        SessionManager.getCurrentLevel().gameObjectsToStart.removeAll(snapshot);
    }
    public void update(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().activeGameObjects);
        for (GameObject gameObject : snapshot) {
            if(gameObject==null){continue;}
            gameObject.update();
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawBackground(g2d);
        if(SessionManager.getCurrentLevel()!=null) {
            ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().activeGameObjects);
            for (GameObject gameObject : snapshot) {
                if(gameObject==null){continue;}
                gameObject.draw(g2d);
            }
        }
        DebugText.drawDebugText(g);
        GameUI.getInstance().drawUI(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        try {
            if(gamePanelBackground == null){
                gamePanelBackground = AssetLoader.getInstance().getImage(Assets.Images.BACKGROUND);
            }
            g2d.drawImage(gamePanelBackground, 0, 0, getWidth(), getHeight(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroyObjects(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        try {
            for (GameObject gameObject :SessionManager.getCurrentLevel().gameObjectsToDestroy) {
                if(gameObject==null){continue;}
                for (Component component : gameObject.getAllComponents()) {
                    component.onDestroy();
                }
                SessionManager.getCurrentLevel().activeGameObjects.remove(gameObject);
            }
            SessionManager.getCurrentLevel().gameObjectsToDestroy.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
