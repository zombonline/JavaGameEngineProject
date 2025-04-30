package game.components.rendering;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.animation.Animation;
import core.asset.AssetLoader;
import game.components.core.Component;


public class SpriteAnimator extends Component {
    SpriteRenderer spriteRenderer;
    int frameTimer = 0;
    int currentStepIndex = 0;
    private Animation currentAnim;
    boolean paused = false;


    public SpriteAnimator(SpriteRenderer spriteRenderer, Animation anim){
        this.spriteRenderer = spriteRenderer;
        if(anim==null){return;}
        currentStepIndex = 0;
        currentAnim = anim;
        frameTimer = currentAnim.animationSteps.getFirst().delay;
    }

    public SpriteAnimator(Animation anim){
        if(anim==null){return;}
        currentStepIndex = 0;
        currentAnim = anim;
        frameTimer = currentAnim.animationSteps.getFirst().delay;
    }

    public void loadAnimation(String animPath){
        currentStepIndex = 0;
        currentAnim = AssetLoader.getInstance().getAnimation(animPath);
        frameTimer = currentAnim.animationSteps.getFirst().delay;
        spriteRenderer.setSpriteImage(currentAnim.animationSteps.get(currentStepIndex).getImage()); // Ensure image is set immediately

    }

    public interface AnimatorListener {
        void onAnimationEvent(String eventKey);
        void onAnimationComplete();
    }
    private final List<SpriteAnimator.AnimatorListener> listeners = new ArrayList<>();
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
            listener.onAnimationComplete();
        }
    }
    @Override
    public void awake() {
        super.awake();
        if(spriteRenderer == null){
            spriteRenderer = getComponent(SpriteRenderer.class);
        }
    }
    @Override
    public void update() {
        if(currentAnim == null || paused){return;}
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
            spriteRenderer.setSpriteImage(currentAnim.animationSteps.get(currentStepIndex).getImage());
            frameTimer = currentAnim.animationSteps.get(currentStepIndex).delay;
        }
    }
    public static Map<String, Object> getDefaultValues(){
        Map<String,Object> defaultValues = new HashMap<>();
        defaultValues.put("startingAnim","");
        return defaultValues;
    }
    public void pause(){
        paused = true;
    }
}
