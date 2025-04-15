package Main;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import java.io.InputStream;

public class SFXPlayer {

    public static void playSound(String sound, float pitch) {
        try {
            InputStream audioSrc = SFXPlayer.class.getResourceAsStream("/Resources/sound.wav");
            assert audioSrc != null : "Audio file not found: " + sound;
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioSrc);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            // Check if SAMPLE_RATE control is available
            if (clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
                FloatControl pitchControl = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
                pitchControl.setValue(pitchControl.getValue() * pitch);
            } else {
                System.err.println("Warning: SAMPLE_RATE control not supported on this audio device.");
            }

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
