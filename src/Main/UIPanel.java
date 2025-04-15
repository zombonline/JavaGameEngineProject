package Main;

import javax.swing.*;

public class UIPanel extends JPanel {
    JPanel pause = new JPanel();
    JLabel pauseLabel = new JLabel("Paused");
    JPanel start = new JPanel();
    private JButton startButton = new JButton("Start Game");
    private JButton continueButton = new JButton("Continue");
    public UIPanel() {
        pause.add(pauseLabel);
        startButton.setActionCommand("Start");

        startButton.addActionListener(e -> {
            if ("Start".equals(e.getActionCommand())) {
                Main.hideUIPanel(); // Switch to the game panel
                Main.gamePanel.resumeGameThread();
            }
            if("Continue".equals(e.getActionCommand())){
                Main.hideUIPanel();
                Main.gamePanel.resumeGameThread();
            }
        });

        start.add(startButton);
    }
    public void LoadPauseMenu(){
        Main.showUIPanel();
        this.removeAll();
        this.add(pause);
    }
    public void UnloadPauseMenu(){
        this.remove(pause);
    }
    public void LoadStartMenu(){
        Main.showUIPanel();
        this.removeAll();
        this.add(start);
    }
    public void UnloadStartMenu(){
        this.remove(start);
    }
}