package ObjectSystem;

import Main.Main;
import Main.GameUI;
public class NPCDialogueHandler extends Component{
    Collider collider;
    private final String characterName;
    String dialogue;
    public NPCDialogueHandler(){
        characterName = "Worker #" + Math.round(Math.random()*1000f);
    }
    @Override
    public void awake() {
        getRequiredComponentReferences();
        setUpColliderListener();
        dialogue = gameObject.getExtraData("dialogue").toString();
        dialogue = dialogue.replace("\\n", "\n");
    }
    @Override
    protected void getRequiredComponentReferences() {
        collider = fetchRequiredComponent(Collider.class);
    }
    private void setUpColliderListener(){
        collider.addListener(new Collider.CollisionListener() {
            @Override
            public void onCollisionEnter(Collider other) {
                if(other.hasComponent(Player.class)){
                    System.out.println("Talking to Player");
                    GameUI.getInstance().setDialogue(characterName,dialogue);
                    GameUI.getInstance().updateScreen(GameUI.Screen.DIALOGUE);
                }
            }
            @Override
            public void onCollisionExit(Collider other) {
                if(other.hasComponent(Player.class)){
                    System.out.println("Bye bye Player");
                    GameUI.getInstance().updateScreen(GameUI.Screen.GAME);
                }
            }
            @Override
            public void onCollisionStay(Collider other) {
            }
        });
    }
    public void updateDialogue(String val){
        dialogue = val;
    }
}
