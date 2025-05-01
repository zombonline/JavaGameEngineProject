package game.components;

import core.asset.Assets;
import core.ui.GameUI;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.player.Player;
import game.components.rendering.SpriteAnimator;
import main.GamePanel;

import javax.naming.Name;
import java.util.ArrayList;

public class NPCDialogueHandler extends Component {
    private Collider collider;
    SpriteAnimator spriteAnimator;
    NPCDialogueHandler me = this;

    private String characterName;
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
        spriteAnimator = fetchRequiredComponent(SpriteAnimator.class);
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
                    GameUI.getInstance().setDialogue(me);
                    GameUI.getInstance().updateScreen(GameUI.Screen.DIALOGUE);
                    seenDialogue.add(characterName+dialogue);
                    spriteAnimator.loadAnimation(Assets.Animations.NPC_TALK);
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
    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }
    public void finishedDialogue(){
        spriteAnimator.loadAnimation(Assets.Animations.NPC_IDLE);
    }
}
