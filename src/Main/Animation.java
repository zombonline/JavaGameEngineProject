package Main;

import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    public boolean isLooping;
    public List<AnimationStep> animationSteps;
    public static class AnimationStep {
        public String imageAddress;
        public int delay;
        public String eventKey=null;
        //to be used by object mapper
        public AnimationStep() {}

        public AnimationStep(String imageAddress, int delay, String eventKey) {
            this.imageAddress = imageAddress;
            this.delay = delay;
            this.eventKey = eventKey;
        }
        public BufferedImage getImage() {
            return AssetLoader.getInstance().getImage(Assets.getAssetPath(imageAddress));
        }
        public int getDelay() {
            return delay;
        }
    }
}
