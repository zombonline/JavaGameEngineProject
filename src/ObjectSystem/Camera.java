package ObjectSystem;

import Main.GamePanel;
import Utility.Vector2;

public class Camera{
    private Vector2 position;
    private GamePanel gamePanel;

    public Camera(GamePanel gamePanel) {
        this.position = new Vector2(0, 0); // Start at origin
        this.gamePanel = gamePanel;
    }
    public Vector2 getPosition() {
        return position;
    }
    public Vector2 getCameraCentrePosition(){
        return new Vector2(position.getX()+((float) gamePanel.getWidth() /2),position.getY()+((float) gamePanel.getHeight() /2));
    }
    public void setPosition(Vector2 position){
        this.position = new Vector2(position.getX()-((float) gamePanel.getWidth() /2),position.getY()-((float) gamePanel.getHeight() /2));
    }
}
