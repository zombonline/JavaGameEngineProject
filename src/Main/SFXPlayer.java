package Main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.InputStream;

public class SFXPlayer {

    public static void playSound(String sound) {
        try {
            InputStream audioSrc = SFXPlayer.class.getResourceAsStream("/Resources/Audio/sound.wav");
            assert audioSrc != null : "Audio file not found: " + sound;
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
