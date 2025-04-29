package core.camera;

import main.GamePanel;
import core.utils.Vector2;

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
    public Vector2 getCameraCentrePosition() {
        float halfWidth = gamePanel.getWidth() / 2f;
        float halfHeight = gamePanel.getHeight() / 2f;
        return new Vector2(position.getX() + halfWidth, position.getY() + halfHeight);
    }
    /**
     * Sets the camera's position so that the given point becomes the center of the viewport.
     */
    public void setPosition(Vector2 centerPosition) {
        float halfWidth = gamePanel.getWidth() / 2f;
        float halfHeight = gamePanel.getHeight() / 2f;
        this.position = new Vector2(centerPosition.getX() - halfWidth, centerPosition.getY() - halfHeight);
    }
}
