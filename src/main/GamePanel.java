package main;

import core.asset.AssetLoader;
import core.asset.Assets;
import core.audio.SFXPlayer;
import core.utils.DebugText;
import core.scene.SessionManager;
import core.ui.GameUI;
import game.entities.GameObject;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class GamePanel extends Canvas implements Runnable{
    public static final int WORLD_SCALE = Math.min(Main.width, Main.height)/12;
    private static final int FPS = 60;
    private static double deltaTime = 0;
    private static volatile boolean gamePaused = false;
    public void startGameThread(){
        this.createBufferStrategy(3);
        new Thread(this).start();
    }
    @Override
    public void run() {
        SessionManager.loadLevelByIndex(0);
        SFXPlayer.playSound(Assets.SFXClips.TRACK, true);
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        while (true) {
            currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1) {
                try {
                    handleAwake();
                    handleStart();
                    handleUpdate();
                    draw();
                    handleDestroy();
                } catch (Exception e) {
                    System.out.println("GameLoopError: " + e.getMessage());
                    e.printStackTrace();
                }
                delta--;
                drawCount++;
            }
            if (timer >= 1000000000) {
                DebugText.logPermanently("FPS", String.valueOf(drawCount));
                drawCount = 0;
                timer = 0;
            }
            long sleepTime = (long) ((lastTime - System.nanoTime() + drawInterval) / 1_000_000); // Calculate sleep time in milliseconds
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
    public void handleAwake(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().gameObjectsToAwake);
        for (GameObject gameObject : snapshot) {
            gameObject.awake();
            SessionManager.getCurrentLevel().activeGameObjects.add(gameObject);
            SessionManager.getCurrentLevel().gameObjectsToStart.add(gameObject);
        }
        SessionManager.getCurrentLevel().gameObjectsToAwake.removeAll(snapshot);
    }
    public void handleStart(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().gameObjectsToStart);
        for (GameObject gameObject : snapshot) {
            gameObject.start();
        }
        SessionManager.getCurrentLevel().gameObjectsToStart.removeAll(snapshot);
    }
    public void handleUpdate(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().activeGameObjects);
        for (GameObject gameObject : snapshot) {
            gameObject.update();
        }
    }
    public void draw() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        try {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            drawGame(g2d);
            GameUI.getInstance().drawUI(g2d);

        } finally {
            g.dispose();
        }
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }

    private static void drawGame(Graphics2D g2d) {
        if(SessionManager.getCurrentLevel()!=null) {
            ArrayList<GameObject> snapshot = new ArrayList<>(SessionManager.getCurrentLevel().activeGameObjects);
            for (GameObject gameObject : snapshot) {
                gameObject.draw(g2d);
            }
        }
    }

    public void handleDestroy(){
        if(SessionManager.getCurrentLevel()==null || gamePaused){return;}
        for (GameObject gameObject :SessionManager.getCurrentLevel().gameObjectsToDestroy) {
            gameObject.onDestroy();
            SessionManager.getCurrentLevel().activeGameObjects.remove(gameObject);
        }
        SessionManager.getCurrentLevel().gameObjectsToDestroy.clear();
    }

    public static void setGamePaused(boolean val) {
        gamePaused = val;
    }
    public static double getDeltaTime() {
        return deltaTime;
    }
}
