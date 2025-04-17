package ObjectSystem;

import Main.DebugText;
import Main.Main;
import Utility.Vector2;
import Main.Assets;

public class LevelExit extends Component{
    Collider collider;
    boolean playerInRange = false;
    boolean playerExited = false;
    @Override
    public void awake() {
        collider = getComponent(Collider.class);
        collider.addListener(
                new Collider.CollisionListener() {
                    @Override
                    public void onCollisionEnter(Collider other) {
                        if(other.getComponent(Player.class) != null) {
                            if(playerExited){return;}
                            playerExited = true;
                            playerInRange = true;
                            DebugText.logTemporarily("Player in range of level exit");
                            GameObject.destroy(other.gameObject);
                            new Thread(() -> {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Main.gamePanel.startGameThread(Assets.Levels.LEVEL_TEST);
                            }).start();                        }
                    }

                    @Override
                    public void onCollisionExit(Collider other) {
                        if(other.getComponent(Player.class) != null) {
                            playerInRange = false;
                        }
                    }

                    @Override
                    public void onCollisionStay(Collider other) {

                    }
                }
        );
    }
}
