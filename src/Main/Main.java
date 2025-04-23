package Main;

import ObjectSystem.Camera;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {

    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width/100*90;
    public static int height = Toolkit.getDefaultToolkit().getScreenSize().height/100*90;
    static JFrame frame = new JFrame(); // Main window
    public static GamePanel gamePanel = new GamePanel();
    public static Camera camera = new Camera(gamePanel); // Camera system

    public static void main(String[] args) throws InterruptedException, IOException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        width = screenSize.width/100*50;
        height = screenSize.height/100*70;

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