package core.ui;

import main.Main;
import core.asset.Assets;
import main.GamePanel;
import core.utils.Vector2;
import core.asset.AssetLoader;
import core.input.KeyHandler;
import core.scene.SessionManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameUI {
    public enum Screen {
        GAME,
        DIALOGUE,
        LEVEL_COMPLETE,
        GAME_COMPLETE
    }
    private static GameUI instance;
    private Vector2 textSize;
    private Screen currentScreen = Screen.GAME;
    private final KeyHandler keyHandler = new KeyHandler();

    //LEVEL OVER SCREEN
    private int resultsCrates, resultsCombo;

    //DIALOGUE SCREEN
    String dialogueName = "Name";
    String dialogueContent = "Dialogue";

    //GAME SCREEN
    private final BufferedImage crateIcon = AssetLoader.getInstance().getImage(Assets.Images.CRATE_BASIC);


    private GameUI() {
        setUpControls(keyHandler);
    }
    public static GameUI getInstance() {
        if (instance == null) {
            instance = new GameUI();
        }
        return instance;
    }
    public void drawUI(Graphics2D g2d) {
        switch (currentScreen){
            case GAME -> drawGameScreen(g2d);
            case DIALOGUE -> drawDialogueScreen(g2d);
            case LEVEL_COMPLETE -> drawLevelCompleteScreen(g2d);
            case GAME_COMPLETE -> drawGameCompleteScreen(g2d);
        }
    }

    public void updateScreen(Screen screen){
        currentScreen = screen;
    }

    private void setUpControls(KeyHandler keyHandler) {
        keyHandler.addKey(KeyEvent.VK_ENTER,
                this::enterKeyPressed,
                ()->{});
    }
    private void enterKeyPressed(){
        if(currentScreen==Screen.LEVEL_COMPLETE) {
            updateScreen(Screen.GAME);
            if(SessionManager.loadNextLevel()){
                GamePanel.setGamePaused(false);
            } else {
                updateScreen(Screen.GAME_COMPLETE);
            }
        }
        else if(currentScreen==Screen.GAME_COMPLETE){
            updateScreen(Screen.GAME);
            SessionManager.LoadLevelByPath(Assets.Tilemaps.LEVEL_1);
            GamePanel.setGamePaused(false);
        }
    }
    private void drawGameScreen(Graphics2D g2d) {
        if(SessionManager.getCurrentLevel()==null){return;}
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.PLAIN, GamePanel.WORLD_SCALE));
        textSize = getTextSize(SessionManager.getCurrentLevel().getCratesDestroyed() + "/" + SessionManager.getCurrentLevel().getCratesToDestroy(), g2d);
        g2d.drawString( SessionManager.getCurrentLevel().getCratesDestroyed() + "/" +SessionManager.getCurrentLevel().getCratesToDestroy(), (int) (Main.gamePanel.getWidth()-textSize.getX()), (int) (GamePanel.WORLD_SCALE/2+textSize.getY()/2));
        g2d.drawImage(crateIcon, (int) (Main.gamePanel.getWidth()-textSize.getX()-GamePanel.WORLD_SCALE),GamePanel.WORLD_SCALE/4, GamePanel.WORLD_SCALE, GamePanel.WORLD_SCALE,null);
    }
    private void drawDialogueScreen(Graphics2D g2d){
        int w = GamePanel.WORLD_SCALE*10;
        int h = (int) (GamePanel.WORLD_SCALE*2.75f);
        int x = (int) (Main.width-w-GamePanel.WORLD_SCALE*0.15f);
        int y = (int) (GamePanel.WORLD_SCALE*0.15f);
        drawBoxWithOutline(x,y,w,h, g2d);

        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));
        textSize = getTextSize(dialogueName,g2d);
        g2d.drawString(dialogueName, x+GamePanel.WORLD_SCALE/10, (int) (y+textSize.getY()/1.5f));

        String[] lines = dialogueContent.split("\n");
        int yIncrement = 0;
        for (String line : lines) {
            g2d.setFont(new Font("Arial", Font.PLAIN, GamePanel.WORLD_SCALE/3));
            textSize = getTextSize(line,g2d);
            g2d.drawString(line, x+GamePanel.WORLD_SCALE/10, (int) (y+textSize.getY()*2+yIncrement));
            yIncrement+=textSize.getY();
        }
    }
    private void drawLevelCompleteScreen(Graphics2D g2d) {
        g2d.setColor(Color.white);
        int x = Main.width/10;
        int y = Main.height/10;
        int w = Main.width/10*8;
        int h = Main.height/10*8;
        drawBoxWithOutline(x,y,w,h,g2d);
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE));
        textSize = getTextSize("RESULTS",g2d);
        g2d.drawString("RESULTS",
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h/5)-(textSize.getY()/2)));
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));

        textSize = getTextSize("Crates Destroyed: " + resultsCrates,g2d);
        g2d.drawString("Crates Destroyed: " + resultsCrates,
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h/2)-(textSize.getY()/2)));

        textSize = getTextSize("Highest Combo: " + resultsCombo, g2d);
        g2d.drawString("Highest Combo: " + resultsCombo,
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h/2)+(textSize.getY()/2)));

        textSize = getTextSize("Press Enter to Continue",g2d);
        g2d.drawString("Press Enter to Continue",
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h)-(textSize.getY()/2)));
    }
    private void drawGameCompleteScreen(Graphics2D g2d) {
        g2d.setColor(Color.white);
        int x = Main.width/10;
        int y = Main.height/10;
        int w = Main.width/10*8;
        int h = Main.height/10*8;
        drawBoxWithOutline(x,y,w,h,g2d);
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE));
        textSize = getTextSize("THANK YOU FOR PLAYING!",g2d);
        g2d.drawString("THANK YOU FOR PLAYING!",
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h/2)-(textSize.getY()/2)));
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));
        textSize = getTextSize("Press Enter to Restart",g2d);
        g2d.drawString("Press Enter to Restart",
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h)-(textSize.getY()/2)));
    }

    private void drawBoxWithOutline(int x, int y, int width, int height, Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.fillRoundRect(x,y, width, height, GamePanel.WORLD_SCALE/10, GamePanel.WORLD_SCALE/10);
        g2d.setStroke(new BasicStroke(3)); // Set the stroke size to 3
        g2d.setColor(Color.black);
        g2d.drawRoundRect(x, y, width, height, GamePanel.WORLD_SCALE/10, GamePanel.WORLD_SCALE/10);
    }
    private Vector2 getTextSize(String text, Graphics2D g2d) {
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.getStringBounds(text, g2d ).getBounds().width;
        int textHeight = metrics.getStringBounds(text, g2d ).getBounds().height;
        return new Vector2(textWidth,textHeight);
    }
    public void setDialogue(String dialogueName, String dialogueContent){
        this.dialogueName = dialogueName;
        this.dialogueContent = dialogueContent;
    }
    public void setResults(){
        this.resultsCombo = SessionManager.getCurrentLevel().getHighestCombo();
        this.resultsCrates = SessionManager.getCurrentLevel().getCratesDestroyed();
    }
}
