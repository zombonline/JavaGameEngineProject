package game.components;

import core.ui.GameUI;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.player.Player;

import javax.naming.Name;

public class NPCDialogueHandler extends Component {
    Collider collider;
    private  String characterName;
    String dialogue;
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
                    System.out.println("Talking to Player");
                    GameUI.getInstance().setDialogue(characterName,dialogue);
                    GameUI.getInstance().updateScreen(GameUI.Screen.DIALOGUE);
                }
            }
            @Override
            public void onCollisionExit(Collider other, Vector2 contactNormal) {
                if(other.hasComponent(Player.class)){
                    System.out.println("Bye bye Player");
                    GameUI.getInstance().updateScreen(GameUI.Screen.GAME);
                }
            }
            @Override
            public void onCollisionStay(Collider other, Vector2 contactNormal) {
            }
        });
    }
    public void updateDialogue(String val){
        dialogue = val;
    }
}
