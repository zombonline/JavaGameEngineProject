package Main;

import javax.swing.*;
import java.io.IOException;

public class Main {
    static JFrame frame = new JFrame();
    static GamePanel gamePanel = new GamePanel();
    public static void main(String[] args) throws InterruptedException, IOException {
        frame.setSize(400,300);
        frame.setVisible(true);
        frame.setTitle("Test Game");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);

        gamePanel.startGameThread();
        gamePanel.requestFocusInWindow();

    }
}