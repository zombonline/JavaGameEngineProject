package game.components;

import core.ui.GameUI;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.player.Player;
import main.GamePanel;

import javax.naming.Name;
import java.util.ArrayList;

public class NPCDialogueHandler extends Component {
    private Collider collider;
    private  String characterName;
    private String dialogue;

    private static final ArrayList<String> seenDialogue = new ArrayList<>();
    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpColliderListener();
        if(gameObject.hasExtraData("dialogue")){
            dialogue = gameObject.getExtraData("dialogue").toString();
            dialogue = dialogue.replace("\\n", "\n");
        }
        if(gameObject.hasExtraData("name")){
            characterName = gameObject.getExtraData("name").toString();
        }
    }
    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
    }
    private void setUpColliderListener(){
        collider.addListener(new Collider.CollisionListener() {
            @Override
            public void onCollisionEnter(Collider other, Vector2 contactNormal) {
                if(other.hasComponent(Player.class)){
                    if(seenDialogue.contains(characterName+dialogue)){
                        return;
                    }
                    System.out.println("Talking to Player");
                    GameUI.getInstance().setDialogue(characterName,dialogue);
                    GameUI.getInstance().updateScreen(GameUI.Screen.DIALOGUE);
                    seenDialogue.add(characterName+dialogue);
                    other.getComponent(Player.class).setCanMove(false);
                }
            }
            @Override
            public void onCollisionExit(Collider other, Vector2 contactNormal) {

            }
            @Override
            public void onCollisionStay(Collider other, Vector2 contactNormal) {
            }
        });
    }
}
