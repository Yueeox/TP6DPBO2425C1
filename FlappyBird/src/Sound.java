import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    private Clip bgmClip;
    private Clip jumpClip;
    private Clip poinClip;

    public Sound() {
        try {
            // Load BGM
            URL bgmUrl = getClass().getResource("/sound/bgm.wav");
            if (bgmUrl != null) {
                AudioInputStream bgmStream = AudioSystem.getAudioInputStream(bgmUrl);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(bgmStream);
            }

            // Load SFX Jump
            URL jumpUrl = getClass().getResource("/sound/jump.wav");
            if (jumpUrl != null) {
                AudioInputStream jumpStream = AudioSystem.getAudioInputStream(jumpUrl);
                jumpClip = AudioSystem.getClip();
                jumpClip.open(jumpStream);
            }

            // Load SFX PoinWhenPassed
            URL poinUrl = getClass().getResource("/sound/poin.wav");
            if (poinUrl != null) {
                AudioInputStream jumpStream = AudioSystem.getAudioInputStream(poinUrl);
                poinClip = AudioSystem.getClip();
                poinClip.open(jumpStream);
            }
        } catch (Exception e) {
            System.out.println("Gagal memuat suara: " + e.getMessage());
        }
    }

    public void playBGM() {
        if (bgmClip != null) {
            bgmClip.setFramePosition(0); // Mulai dari awal
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // Putar berulang-ulang
        }
    }

    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    public void playJumpSound() {
        if (jumpClip != null) {
            jumpClip.setFramePosition(0); // Reset ke awal agar bisa diputar cepat berkali-kali
            jumpClip.start();
        }
    }

    public void playPoinSound() {
        if (poinClip != null) {
            poinClip.setFramePosition(0); // Reset ke awal agar bisa diputar cepat berkali-kali
            poinClip.start();
        }
    }

}