package core.audio;

import core.asset.AssetLoader;

import javax.sound.sampled.Clip;

public class SFXPlayer {

    public static void playSound(String path) {
        Clip clip = AssetLoader.getInstance().getSFXClip(path);
        clip.start();
    }
    public static void playSound(String path, boolean loop){
        Clip clip = AssetLoader.getInstance().getSFXClip(path);
        clip.start();
        clip.loop(Integer.MAX_VALUE);
    }
}
