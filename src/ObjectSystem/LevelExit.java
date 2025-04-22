package ObjectSystem;

import Main.GameUI;
import Main.GamePanel;

public class LevelExit extends Component{
    // Component references
    private Collider collider;

    // variables
    private boolean playerExited = false;

    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpColliderListener();
    }
    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
    }
    private void setUpColliderListener(){
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
                    public void onCollisionExit(Collider other) {}
                    @Override
                    public void onCollisionStay(Collider other) {}
                }
        );
    }
}
