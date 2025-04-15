package Main;

import ObjectSystem.Camera;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static final int width = 1864;
    public static final int height = 1024;
    static JFrame frame = new JFrame(); // Main window
    public static GamePanel gamePanel = new GamePanel(); // Background game panel
    public static UIPanel uiPanel = new UIPanel(); // UI overlay panel
    public static Camera camera = new Camera(gamePanel); // Camera system

    // JLayeredPane handles overlapping of the gamePanel and uiPanel
    static JLayeredPane layeredPane = new JLayeredPane();

    public static void main(String[] args) throws InterruptedException, IOException {
        // Initialize JFrame properties
        frame.setSize(width, height);
        frame.setTitle("Test Game");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Set up the layeredPane
        layeredPane.setBounds(0, 0, width, height); // Explicit bounds for the layered pane
        gamePanel.setBackground(Color.DARK_GRAY);
        gamePanel.setBounds(0, 0, width, height);  // Explicit bounds for the game panel
        uiPanel.setOpaque(false);
        uiPanel.setBounds(0, 0, width, height);    // Explicit bounds for the UI panel
        uiPanel.LoadStartMenu();
        layeredPane.add(gamePanel, Integer.valueOf(0)); // Add gamePanel to default layer
        layeredPane.add(uiPanel, Integer.valueOf(1)); // Add uiPanel to higher layer

        // Add the layeredPane to the frame
        frame.add(layeredPane);
        frame.setVisible(true);

        // Start the game loop
        gamePanel.startGameThread(Assets.Levels.LEVEL_TEST_1);
    }

    public static void hideUIPanel() {
        gamePanel.requestFocus();
        uiPanel.setVisible(false);
    }

    public static void showUIPanel() {
        uiPanel.setVisible(true);
    }
}