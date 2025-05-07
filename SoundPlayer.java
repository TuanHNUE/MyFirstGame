import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class SoundPlayer {
    private Clip clip;

    public SoundPlayer(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); 
            clip.start();
        }
       
    }

    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
        }
        else {
            System.out.println("Clip is null!");
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Volume range is usually from -80.0f (mute) to 6.0f (max)
            float min = gainControl.getMinimum(); // usually -80.0f
            float max = gainControl.getMaximum(); // usually 6.0f
            float gain = min + (max - min) * (volume / 100f);
            gainControl.setValue(gain);
        }
    }
}
