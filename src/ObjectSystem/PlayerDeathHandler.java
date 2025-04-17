package ObjectSystem;


import Main.Assets;
import Main.GamePanel;
import Main.Main;
import Main.DebugText;
import Utility.Vector2;

public class PlayerDeathHandler extends Component implements Explosion.ExplosionListener {
    Transform transform;
    boolean dead = false;
    @Override
    public void awake() {
        getRequiredComponentReferences();
    }
    @Override
    protected void getRequiredComponentReferences() {
        transform = fetchRequiredComponent(Transform.class);
    }

    @Override
    public void update() {
        Vector2 position = transform.getPosition();

        if(position.getX() < 0 || position.getX() > GamePanel.currentLevel.getWidth() || position.getY() < 0 || position.getY() > GamePanel.currentLevel.getHeight()) {
            die();
        }

    }
    public void die(){
        if(dead){return;}
        dead = true;
        getComponent(Player.class).setCanMove(false);
        DebugText.logTemporarily("Player died");
        //reload level after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Main.gamePanel.startGameThread(GamePanel.currentLevel.levelString);
        }).start();
    }

    @Override
    public void onTriggered() {
        die();
    }
}
