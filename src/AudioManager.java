import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * AudioManager.java
 * Manages background music and sound effects.
 * Structured for easy drop-in of WAV/MP3 audio files.
 * Audio files go in assets/audio/ directory.
 *
 * Expected audio files (add your own):
 *   hit_light.wav, hit_heavy.wav, block.wav,
 *   special.wav, heal.wav, ko.wav, round_start.wav,
 *   victory.wav, defeat.wav, menu_bgm.wav, fight_bgm.wav
 */
public class AudioManager {
    private static Map<String, javafx.scene.media.AudioClip> sfxCache = new HashMap<>();
    private static javafx.scene.media.MediaPlayer bgmPlayer;
    private static double sfxVolume = 0.7;
    private static double bgmVolume = 0.4;

    public static void loadSFX(String name) {
        String path = GameConfig.AUDIO_DIR + name;
        File file = new File(path);
        if (file.exists()) {
            try {
                javafx.scene.media.AudioClip clip =
                    new javafx.scene.media.AudioClip(file.toURI().toString());
                sfxCache.put(name, clip);
            } catch (Exception e) {
                System.out.println("[Audio] Could not load SFX: " + name);
            }
        }
    }

    public static void playSFX(String name) {
        javafx.scene.media.AudioClip clip = sfxCache.get(name);
        if (clip != null) {
            clip.play(sfxVolume);
        }
    }

    public static void playBGM(String filename) {
        stopBGM();
        String path = GameConfig.AUDIO_DIR + filename;
        File file = new File(path);
        if (file.exists()) {
            try {
                javafx.scene.media.Media media =
                    new javafx.scene.media.Media(file.toURI().toString());
                bgmPlayer = new javafx.scene.media.MediaPlayer(media);
                bgmPlayer.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
                bgmPlayer.setVolume(bgmVolume);
                bgmPlayer.play();
            } catch (Exception e) {
                System.out.println("[Audio] Could not play BGM: " + filename);
            }
        }
    }

    public static void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
        }
    }

    public static void preloadAll() {
        String[] sfxFiles = {
            "hit_light.wav", "hit_heavy.wav", "block.wav",
            "special.wav", "heal.wav", "ko.wav",
            "round_start.wav", "victory.wav", "defeat.wav"
        };
        for (String f : sfxFiles) loadSFX(f);
        System.out.println("[Audio] Loaded " + sfxCache.size() + " sound effects.");
    }

    public static void setSfxVolume(double v) { sfxVolume = v; }
    public static void setBgmVolume(double v) { bgmVolume = v; }
}
