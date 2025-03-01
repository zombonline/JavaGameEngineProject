package ObjectSystem;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import Main.Animation;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SpriteAnimator extends Component{
    SpriteRenderer spriteRenderer;
    int frameTimer = 0;
    int currentStepIndex = 0;
    Animation currentAnim;


    public void loadAnimation(String dir, String filename)  {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/Resources/test.json");
            if (inputStream == null) {
                System.out.println("DEBUG: Could not find test.json in resources.");
            }
            Animation animation = objectMapper.readValue(inputStream, Animation.class);
            System.out.println("islooping: " + animation.isLooping);
            for (Animation.AnimationStep step : animation.animationSteps) {
                System.out.println("Image: " + step.imageAddress + ", Delay: " + step.delay);
                step.loadImage();
            }
            currentAnim = animation;
            frameTimer = animation.animationSteps.get(0).delay;
        }
        catch (Exception e) {
            System.out.println("Animation not loaded");
            System.out.println(e);
        }
    }

    public interface AnimatorListener {
        void onAnimationEvent(String eventKey);
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
//    C:\Users\megaz\Documents\JavaGameEngineProject\src\Resources\test.json
    @Override
    public void awake() {
        super.awake();
        spriteRenderer = getComponent(SpriteRenderer.class);
        if(spriteRenderer == null){
            System.out.println("Sprite Animator on " + getGameObject().name + " is missing a reference to Sprite Renderer.");
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
                }
            }
            String eventKey = currentAnim.animationSteps.get(currentStepIndex).eventKey;
            if(eventKey!=null){
                notifyAnimationEvent(eventKey);
            }
            spriteRenderer.spriteImage = currentAnim.animationSteps.get(currentStepIndex).bufferedImage;
            frameTimer = currentAnim.animationSteps.get(currentStepIndex).delay;
        }
    }
}
