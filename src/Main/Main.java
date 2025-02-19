package Main;

import ObjectSystem.Camera;
import com.fasterxml.jackson.databind.DatabindException;

import javax.swing.*;
import java.io.IOException;

public class Main {
    static JFrame frame = new JFrame();
    public static GamePanel gamePanel = new GamePanel();
    public static Camera camera = new Camera(gamePanel);
    public static void main(String[] args) throws InterruptedException, IOException, DatabindException {
        frame.setSize(400,300);
        frame.setVisible(true);
        frame.setTitle("Test Game");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0); // Position at top-left
        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);
        gamePanel.startGameThread();
        gamePanel.requestFocusInWindow();
    }
}