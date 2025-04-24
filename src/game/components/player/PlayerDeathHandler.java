package game.components.player;


import core.scene.SessionManager;
import core.utils.DebugText;
import core.utils.Vector2;
import game.components.Explosion;
import game.components.core.Component;
import game.components.core.Transform;

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

        if(position.getX() < 0 || position.getX() > SessionManager.getCurrentLevel().getWidth() || position.getY() < 0 || position.getY() > SessionManager.getCurrentLevel().getHeight()) {
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
           SessionManager.reloadCurrentLevel();
        }).start();
    }

    @Override
    public void onTriggered() {
        die();
    }
}
