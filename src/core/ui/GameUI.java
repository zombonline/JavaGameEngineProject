package core.ui;

import game.components.NPCDialogueHandler;
import game.components.player.Player;
import game.entities.GameObject;
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
        GAME_COMPLETE,
        GAME_INTRO,
        LOADING
    }
    private static GameUI instance;
    private Vector2 textSize;
    private Screen currentScreen = Screen.LOADING;

    //LEVEL OVER SCREEN
    private int resultsCrates, resultsCombo;

    //DIALOGUE SCREEN
    String dialogueName = "Name";
    String dialogueContent = "Dialogue";
    StringBuilder displayedDialogue = new StringBuilder();
    NPCDialogueHandler currentNpc;
    Thread dialogueAnimationThread = new Thread(() -> {
        while (true){
            if(displayedDialogue.length()<dialogueContent.length()){
                displayedDialogue.append(dialogueContent.charAt(displayedDialogue.length()));
            } else {
                currentNpc.setTalking(false);
            }
            Main.gamePanel.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e){
                break;
            }
        }
    });

    //GAME SCREEN
    private final BufferedImage crateIcon = AssetLoader.getInstance().getImage(Assets.Images.CRATE_BASIC);
    private final BufferedImage playerIcon = AssetLoader.getInstance().getImage(Assets.Images.PLAYER_RUN_1);

    //LOADING SCREEN
    StringBuilder dots = new StringBuilder();
    Thread dotsAnimationThread = new Thread(() -> {
        while (true){
            dots.append(".");
            if(dots.length()>3){
                dots.setLength(0);
            }
            Main.gamePanel.repaint();
            try {
                Thread.sleep(800);
            } catch (InterruptedException e){
                break;
            }
        }
    });

    private GameUI() {
        setUpControls(new KeyHandler());
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
            case GAME_INTRO -> drawGameIntroScreen(g2d);
            case LOADING -> drawLoadingScreen(g2d);
        }
    }

    private void drawLoadingScreen(Graphics2D g2d) {
        if(dotsAnimationThread.getState() == Thread.State.NEW){
            dotsAnimationThread.start();
        }
        g2d.setColor(Color.black);
        g2d.fillRect(0,0, Main.width,Main.height);
        g2d.setFont(new Font("Arial", Font.PLAIN, GamePanel.WORLD_SCALE));
        g2d.setColor(Color.white);
        textSize = getTextSize("Loading...", g2d);
        g2d.drawString( "Loading"+dots, ((float) Main.width /2)-textSize.getX()/2, ((float) Main.height /2)-textSize.getY()/2);
    }

    public void updateScreen(Screen screen){
        System.out.println("Screen updated to: " + screen.name());
        currentScreen = screen;
        Main.gamePanel.repaint();
    }

    private void setUpControls(KeyHandler keyHandler) {
        keyHandler.addKey(KeyEvent.VK_ENTER,
                this::enterKeyPressed,
                ()->{});
    }
    private void enterKeyPressed(){
        if(currentScreen==Screen.LEVEL_COMPLETE) {
            if(SessionManager.loadNextLevel()){
                GamePanel.setGamePaused(false);
            } else {
                updateScreen(Screen.GAME_COMPLETE);
            }
        }
        else if(currentScreen==Screen.GAME_COMPLETE){
            SessionManager.loadLevelByIndex(0);
            GamePanel.setGamePaused(false);
        } else if(currentScreen==Screen.DIALOGUE){
            GameObject.findFirstObjectByType(Player.class).getComponent(Player.class).setCanMove(true);
            currentNpc.finishedDialogue();
            updateScreen(Screen.GAME);
        } else if(currentScreen==Screen.GAME_INTRO){
            updateScreen(Screen.GAME);
            SessionManager.loadLevelByIndex(0);
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
        if(dialogueAnimationThread.getState() == Thread.State.NEW){
            dialogueAnimationThread.start();
        }
        int w = GamePanel.WORLD_SCALE*10;
        int h = (int) (GamePanel.WORLD_SCALE*2.5f);
        int x = (int) (Main.width/2-w/2-GamePanel.WORLD_SCALE*0.3f);
        int y = (int) (Main.height-h-GamePanel.WORLD_SCALE*0.5f);
        drawBoxWithOutline(x,y,w,h, g2d);

        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));
        textSize = getTextSize(dialogueName,g2d);
        g2d.drawString(dialogueName, x+GamePanel.WORLD_SCALE/10, (int) (y+textSize.getY()/1.3f));

        String[] lines = displayedDialogue.toString().split("\n");
        int yIncrement = 0;
        for (String line : lines) {
            g2d.setFont(new Font("Arial", Font.PLAIN, GamePanel.WORLD_SCALE/3));
            textSize = getTextSize(line,g2d);
            g2d.drawString(line, x+GamePanel.WORLD_SCALE/10, (int) (y+textSize.getY()*2+yIncrement));
            yIncrement+=textSize.getY();
        }
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));
        textSize = getTextSize("Press Enter to Continue",g2d);
        g2d.drawString("Press Enter to Continue", x+w-textSize.getX()*1.1f, (int) (y+h-textSize.getY()/2));
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

    private void drawGameIntroScreen(Graphics2D g2d) {
        g2d.setColor(Color.white);

        int x = Main.width/10;
        int y = Main.height/10;
        int w = Main.width/10*8;
        int h = Main.height/10*8;

        drawBoxWithOutline(x,y,w,h,g2d);
        g2d.drawImage(playerIcon, x,y+h/4, (int)(h/1.5f), (int)(h/1.5f),null);

        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE));
        textSize = getTextSize("CRATE CORRUPTION",g2d);
        g2d.drawString("CRATE CORRUPTION",
                (int) (x+(w/2)-(textSize.getX()/2)),
                (int) (y+(h/2)-(textSize.getY()/2)));
        g2d.setFont(new Font("Arial", Font.BOLD, GamePanel.WORLD_SCALE/2));
        textSize = getTextSize("Press Enter to Start",g2d);
        g2d.drawString("Press Enter to Start",
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
    public void setDialogue(NPCDialogueHandler npcDialogueHandler){
        this.dialogueName = npcDialogueHandler.getCharacterName();
        this.dialogueContent = npcDialogueHandler.getDialogue();
        displayedDialogue.setLength(0);
        currentNpc = npcDialogueHandler;
    }
    public void setResults(){
        this.resultsCombo = SessionManager.getCurrentLevel().getHighestCombo();
        this.resultsCrates = SessionManager.getCurrentLevel().getCratesDestroyed();
    }
}
