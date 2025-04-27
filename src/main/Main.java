package main;

import core.camera.Camera;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static final int width = (1920/10)*7;
    public static final int height = (1080/10)*7;
    static JFrame frame = new JFrame(); // Main.Main window
    public static GamePanel gamePanel = new GamePanel();
    public static Camera camera = new Camera(gamePanel); // Camera system

    public static void main(String[] args) throws InterruptedException, IOException {

        // Initialize JFrame properties
        frame.setSize(width, height);
        frame.setTitle("Test Game");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setDoubleBuffered(true);
        gamePanel.setFocusable(true);
        gamePanel.setBackground(Color.BLACK);
        gamePanel.setBounds(0, 0, width, height);
        frame.add(gamePanel);
        frame.setVisible(true);
        // Start the game loop
        gamePanel.startGameThread();
    }
}