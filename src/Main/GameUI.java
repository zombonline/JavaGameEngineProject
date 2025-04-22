package Main;

import Utility.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import Main.Assets;

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
    String dialogueName;
    String dialogueContent;

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
            SessionManager.LoadLevelByPath(Assets.Tilemaps.LEVEL_TEST);
            GamePanel.setGamePaused(false);
        }
    }
    private void drawGameScreen(Graphics2D g2d) {
        if(SessionManager.getCurrentLevel()==null){return;}
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.PLAIN, 40));
        textSize = getTextSize(SessionManager.getCurrentLevel().getCratesDestroyed() + "/" + SessionManager.getCurrentLevel().getCratesToDestroy(), g2d);
        g2d.drawString( SessionManager.getCurrentLevel().getCratesDestroyed() + "/" +SessionManager.getCurrentLevel().getCratesToDestroy(), Main.width-textSize.getX()*1.5f, Main.height/100+textSize.getY()*1.25f);
        g2d.drawImage(crateIcon, (int) (Main.width/100*90),Main.height/100, Main.width/20, Main.width/20,null);
    }
    private void drawDialogueScreen(Graphics2D g2d){
        int w = Main.width/10*8;
        int h = Main.height/10*2;
        int x = Main.width/10;
        int y = (int) (Main.height-(h*1.25f));
        drawBoxWithOutline(x,y,w,h, g2d);

        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        textSize = getTextSize(dialogueName,g2d);
        g2d.drawString(dialogueName, x+w/50, y+h/4);

        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        textSize = getTextSize(dialogueContent,g2d);
        g2d.drawString(dialogueContent, x+w/50, y+h/2);
    }
    private void drawLevelCompleteScreen(Graphics2D g2d) {
        g2d.setColor(Color.white);
        int x = Main.width/10;
        int y = Main.height/10;
        int w = Main.width/10*8;
        int h = Main.height/10*8;
        drawBoxWithOutline(x,y,w,h,g2d);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        textSize = getTextSize("RESULTS",g2d);
        g2d.drawString("RESULTS",
                x+(w/2)-(textSize.getX()/2),
                y+(h/5)-(textSize.getY()/2));
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        textSize = getTextSize("Crates Destroyed: " + resultsCrates,g2d);
        g2d.drawString("Crates Destroyed: " + resultsCrates,
                x+(w/2)-(textSize.getX()/2),
                y+(h/2)-(textSize.getY()/2));

        textSize = getTextSize("Highest Combo: " + resultsCombo, g2d);
        g2d.drawString("Highest Combo: " + resultsCombo,
                x+(w/2)-(textSize.getX()/2),
                y+(h/2)+(textSize.getY()/2));

        textSize = getTextSize("Press Enter to Continue",g2d);
        g2d.drawString("Press Enter to Continue",
                x+(w/2)-(textSize.getX()/2),
                y+(h)-(textSize.getY()/2));
    }
    private void drawGameCompleteScreen(Graphics2D g2d) {
        g2d.setColor(Color.white);
        int x = Main.width/10;
        int y = Main.height/10;
        int w = Main.width/10*8;
        int h = Main.height/10*8;
        drawBoxWithOutline(x,y,w,h,g2d);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        textSize = getTextSize("THANK YOU FOR PLAYING!",g2d);
        g2d.drawString("THANK YOU FOR PLAYING!",
                x+(w/2)-(textSize.getX()/2),
                y+(h/2)-(textSize.getY()/2));
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        textSize = getTextSize("Press Enter to Restart",g2d);
        g2d.drawString("Press Enter to Restart",
                x+(w/2)-(textSize.getX()/2),
                y+(h)-(textSize.getY()/2));
    }

    private void drawBoxWithOutline(int x, int y, int width, int height, Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.fillRoundRect(x,y, width, height, 10, 10);
        g2d.setStroke(new BasicStroke(3)); // Set the stroke size to 3
        g2d.setColor(Color.black);
        g2d.drawRoundRect(x, y, width, height, 10, 10);
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
