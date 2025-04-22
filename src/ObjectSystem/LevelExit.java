package ObjectSystem;

import Main.GameUI;
import Main.GamePanel;

public class LevelExit extends Component{
    Collider collider;
    boolean playerExited = false;
    @Override
    public void awake() {
        collider = getComponent(Collider.class);
        collider.addListener(
                new Collider.CollisionListener() {
                    @Override
                    public void onCollisionEnter(Collider other) {
                        if(other.getComponent(Player.class) != null) {
                            if (playerExited) {
                                return;
                            }
                            playerExited = true;
                            GameUI.getInstance().setResults();
                            GameUI.getInstance().updateScreen(GameUI.Screen.LEVEL_COMPLETE);
                            GameObject.destroy(other.gameObject);
                            GamePanel.setGamePaused(true);
                        }

                    }

                    @Override
                    public void onCollisionExit(Collider other) {
                        if(other.getComponent(Player.class) != null) {
                        }
                    }

                    @Override
                    public void onCollisionStay(Collider other) {

                    }
                }
        );
    }
}
