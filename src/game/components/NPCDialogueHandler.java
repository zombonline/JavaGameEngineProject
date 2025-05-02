package game.components;

import core.asset.Assets;
import core.audio.SFXPlayer;
import core.ui.GameUI;
import core.utils.Vector2;
import game.components.core.Component;
import game.components.player.Player;
import game.components.rendering.SpriteAnimator;

import java.util.ArrayList;
import java.util.Random;

public class NPCDialogueHandler extends Component {
    private Collider collider;
    SpriteAnimator spriteAnimator;
    NPCDialogueHandler me = this;

    private String characterName;
    private String dialogue;


    boolean talking = false;

    Thread talkingThread = new Thread(() -> {
        while(talking){
            SFXPlayer.playSound(Assets.SFXClips.VOICES.get(new Random().nextInt(Assets.SFXClips.VOICES.size())));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });

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
                    talking = true;

                    talkingThread.start();
                    GameUI.getInstance().setDialogue(me);
                    GameUI.getInstance().updateScreen(GameUI.Screen.DIALOGUE);
                    seenDialogue.add(characterName+dialogue);
                    spriteAnimator.loadAnimation(Assets.Animations.NPC_TALK);
                    other.getComponent(Player.class).setCanMove(false);
                }
            }
            @Override
            public void onCollisionExit(Collider other, Vector2 contactNormal) {
                talking = false;
            }
            @Override
            public void onCollisionStay(Collider other, Vector2 contactNormal) {
            }
        });
    }

    @Override
    public void update() {

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
    public void setTalking(boolean talking) {
        this.talking = talking;
    }

}
