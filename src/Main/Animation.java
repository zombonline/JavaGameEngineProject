package Main;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Animation {
    public boolean isLooping;
    public List<AnimationStep> animationSteps;
    public static class AnimationStep {
        public String imageAddress;
        public int delay;
        public String eventKey=null;
        @JsonIgnore
        public BufferedImage bufferedImage;

        public AnimationStep() {
        }

        public AnimationStep(String imageAddress, int delay, String eventKey) {
            this.imageAddress = imageAddress;
            this.delay = delay;
            this.eventKey = eventKey;
            loadImage();
        }

        public void loadImage() {
            System.out.println("Loading image: " + imageAddress);
            try {
                this.bufferedImage = ImageIO.read(getClass().getResourceAsStream(Assets.getAssetPath(imageAddress)));
            } catch (IOException e) {
                System.out.println("Failed to load image: " + imageAddress);
                e.printStackTrace();
            }
        }
    }
}
