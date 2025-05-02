package main;

import core.asset.AssetLoader;
import core.asset.Assets;
import core.camera.Camera;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static final int width = (1920/10)*7;
    public static final int height = (1080/10)*7;
    static final JFrame frame = new JFrame(); // Main.Main window
    public static final GamePanel gamePanel = new GamePanel();
    public static final Camera camera = new Camera(gamePanel); // Camera system

    public static void main(String[] args){

        // Initialize JFrame properties

        gamePanel.setPreferredSize(new Dimension(width, height));
        gamePanel.setIgnoreRepaint(true);
        gamePanel.setFocusable(true);
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setBounds(0, 0, width, height);
        frame.setSize(width, height);
        frame.setTitle("Crate Corruption");
        frame.setIconImage(AssetLoader.getInstance().getImage(Assets.Images.CRATE_BASIC));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setVisible(true);
        // Start the game loop
        gamePanel.startGameThread();
    }
}