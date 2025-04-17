package ObjectSystem;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Main.Animation;
import Main.AssetLoader;



public class SpriteAnimator extends Component{
    SpriteRenderer spriteRenderer;
    int frameTimer = 0;
    int currentStepIndex = 0;
    Animation currentAnim;

    public SpriteAnimator(){

    }

    public SpriteAnimator(Animation anim){
        currentAnim = anim;
        frameTimer = currentAnim.animationSteps.getFirst().getDelay();
    }

    public void loadAnimation(String animPath){
        currentAnim = AssetLoader.getInstance().getAnimation(animPath);
        frameTimer = currentAnim.animationSteps.getFirst().getDelay();
    }

    public interface AnimatorListener {
        void onAnimationEvent(String eventKey);
        void onAnimationComlete();
    }
    private List<SpriteAnimator.AnimatorListener> listeners = new ArrayList<>();
    public void addListener(SpriteAnimator.AnimatorListener listener) {
        listeners.add(listener);
    }
    public void removeListener(SpriteAnimator.AnimatorListener listener) {
        listeners.remove(listener);
    }
    public void notifyAnimationEvent(String eventKey) {
        for (SpriteAnimator.AnimatorListener listener : listeners) {
            listener.onAnimationEvent(eventKey);
        }
    }
    public void notifyAnimationComplete(){
        for(SpriteAnimator.AnimatorListener listener : listeners) {
            listener.onAnimationComlete();
        }
    }
//    C:\Users\megaz\Documents\JavaGameEngineProject\src\Resources\test.json
    @Override
    public void awake() {
        super.awake();
        spriteRenderer = getComponent(SpriteRenderer.class);
        if(spriteRenderer == null){
            System.out.println("Sprite Animator on " + getGameObject().getName() + " is missing a reference to Sprite Renderer.");
        }
    }
    @Override
    public void update() {
        if(currentAnim == null){return;}
        frameTimer--;
        if(frameTimer == 0){
            currentStepIndex++;
            if(currentStepIndex >=currentAnim.animationSteps.size()){
                if(currentAnim.isLooping){
                    currentStepIndex = 0;
                } else {
                    currentStepIndex = currentAnim.animationSteps.size()-1;
                    notifyAnimationComplete();
                    System.out.println("Notifying animation complete");
                }
            }
            String eventKey = currentAnim.animationSteps.get(currentStepIndex).eventKey;
            if(eventKey!=null){
                notifyAnimationEvent(eventKey);
            }
            spriteRenderer.spriteImage = currentAnim.animationSteps.get(currentStepIndex).getImage();
            frameTimer = currentAnim.animationSteps.get(currentStepIndex).getDelay();
        }
    }
}
