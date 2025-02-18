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
        @JsonIgnore
        public BufferedImage bufferedImage;

        public AnimationStep() {
        }

        public AnimationStep(String imageAddress, int delay) {
            this.imageAddress = imageAddress;
            this.delay = delay;
            loadImage();
        }

        public void loadImage() {
            try {
                this.bufferedImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Resources/" + imageAddress));
            } catch (IOException e) {
                System.err.println("Failed to load image: " + imageAddress);
                e.printStackTrace();
            }
        }
    }
}
