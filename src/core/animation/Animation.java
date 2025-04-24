package core.animation;

import core.asset.AssetLoader;
import core.asset.Assets;

import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    public boolean isLooping;
    public List<AnimationStep> animationSteps;
    public static class AnimationStep {
        public String imageAddress;
        public int delay;
        public String eventKey=null;

        //Both of these constructors are to be used by object mapper (Do not delete)
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
