package Entity;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import Main.Animation;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SpriteAnimator extends Component{
    SpriteRenderer spriteRenderer;
    int frameTimer = 0;
    int currentStep = 0;
    Animation currentAnim;
    public void loadAnimation(String dir, String filename) throws IOException {
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
        catch (DatabindException e) {
            System.out.println("Animation not loaded");
            System.out.println(e);
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
            currentStep++;
            if(currentStep>=currentAnim.animationSteps.size()){
                if(currentAnim.isLooping){
                    currentStep = 0;
                } else {
                    currentStep = currentAnim.animationSteps.size()-1;
                }
            }
            spriteRenderer.spriteImage = currentAnim.animationSteps.get(currentStep).bufferedImage;
            frameTimer = currentAnim.animationSteps.get(currentStep).delay;
        }
    }
}
