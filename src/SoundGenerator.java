import javax.sound.sampled.*;
import java.io.*;
import java.util.Random;

/**
 * SoundGenerator.java
 * Generates procedural WAV sound effects on first run.
 * Creates battle drums, hit sounds, and atmospheric audio
 * so the game has audio even without manually added files.
 */
public class SoundGenerator {

    private static final int SAMPLE_RATE = 22050;
    private static final Random random = new Random();

    /**
     * Generate all missing sound effects into assets/audio/.
     * Only creates files that don't already exist.
     */
    public static void generateAll() {
        File audioDir = new File(GameConfig.AUDIO_DIR);
        if (!audioDir.exists()) audioDir.mkdirs();

        generateIfMissing("hit_light.wav", () -> generateHitSound(0.15, 800, 0.6));
        generateIfMissing("hit_heavy.wav", () -> generateHitSound(0.25, 400, 0.9));
        generateIfMissing("block.wav", () -> generateBlockSound());
        generateIfMissing("special.wav", () -> generateSpecialSound());
        generateIfMissing("heal.wav", () -> generateHealSound());
        generateIfMissing("ko.wav", () -> generateKOSound());
        generateIfMissing("round_start.wav", () -> generateRoundStartSound());
        generateIfMissing("victory.wav", () -> generateVictorySound());
        generateIfMissing("defeat.wav", () -> generateDefeatSound());
        generateIfMissing("menu_bgm.wav", () -> generateMenuBGM());
        generateIfMissing("fight_bgm.wav", () -> generateFightBGM());

        System.out.println("[SoundGenerator] Audio generation complete.");
    }

    private static void generateIfMissing(String filename, SoundProducer producer) {
        File file = new File(GameConfig.AUDIO_DIR + filename);
        if (!file.exists()) {
            try {
                byte[] data = producer.produce();
                writeWav(file, data);
                System.out.println("[SoundGenerator] Created: " + filename);
            } catch (Exception e) {
                System.err.println("[SoundGenerator] Failed to create: " + filename + " - " + e.getMessage());
            }
        }
    }

    @FunctionalInterface
    interface SoundProducer {
        byte[] produce();
    }

    // --- Sound Generators ---

    private static byte[] generateHitSound(double duration, double freq, double volume) {
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = Math.max(0, 1.0 - t / duration);
            double noise = (random.nextDouble() - 0.5) * 0.5;
            double tone = Math.sin(2 * Math.PI * freq * t * (1 - t * 2));
            data[i] = (byte)(127 * volume * envelope * (tone * 0.6 + noise * 0.4));
        }
        return data;
    }

    private static byte[] generateBlockSound() {
        int samples = (int)(SAMPLE_RATE * 0.15);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = Math.max(0, 1.0 - t / 0.15);
            double tone = Math.sin(2 * Math.PI * 1200 * t);
            data[i] = (byte)(80 * envelope * tone);
        }
        return data;
    }

    private static byte[] generateSpecialSound() {
        int samples = (int)(SAMPLE_RATE * 0.5);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = t < 0.05 ? t / 0.05 : Math.max(0, 1.0 - (t - 0.05) / 0.45);
            double sweep = 200 + 600 * t;
            double tone = Math.sin(2 * Math.PI * sweep * t);
            double harmonic = Math.sin(2 * Math.PI * sweep * 1.5 * t) * 0.3;
            data[i] = (byte)(100 * envelope * (tone + harmonic));
        }
        return data;
    }

    private static byte[] generateHealSound() {
        int samples = (int)(SAMPLE_RATE * 0.6);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = Math.sin(Math.PI * t / 0.6);
            double tone1 = Math.sin(2 * Math.PI * 523 * t); // C5
            double tone2 = Math.sin(2 * Math.PI * 659 * t); // E5
            double tone3 = Math.sin(2 * Math.PI * 784 * t); // G5
            double mix = (tone1 + tone2 + tone3) / 3.0;
            data[i] = (byte)(80 * envelope * mix);
        }
        return data;
    }

    private static byte[] generateKOSound() {
        int samples = (int)(SAMPLE_RATE * 0.8);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = Math.max(0, 1.0 - t / 0.8);
            double bass = Math.sin(2 * Math.PI * 80 * t) * 0.7;
            double impact = Math.sin(2 * Math.PI * 200 * t * (1 - t)) * 0.5;
            double noise = (random.nextDouble() - 0.5) * 0.3 * Math.max(0, 1.0 - t / 0.2);
            data[i] = (byte)(120 * envelope * (bass + impact + noise));
        }
        return data;
    }

    private static byte[] generateRoundStartSound() {
        int samples = (int)(SAMPLE_RATE * 0.4);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double envelope = t < 0.02 ? t / 0.02 : Math.max(0, 1.0 - (t - 0.02) / 0.38);
            double tone = Math.sin(2 * Math.PI * 440 * t) + Math.sin(2 * Math.PI * 880 * t) * 0.5;
            data[i] = (byte)(80 * envelope * tone / 1.5);
        }
        return data;
    }

    private static byte[] generateVictorySound() {
        int samples = (int)(SAMPLE_RATE * 1.0);
        byte[] data = new byte[samples];
        double[] notes = {523, 659, 784, 1047}; // C E G C (major chord arpeggio)
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            int noteIdx = Math.min((int)(t * 4), 3);
            double noteT = t - noteIdx * 0.25;
            double envelope = Math.max(0, 1.0 - noteT / 0.3);
            double tone = Math.sin(2 * Math.PI * notes[noteIdx] * t);
            data[i] = (byte)(80 * envelope * tone);
        }
        return data;
    }

    private static byte[] generateDefeatSound() {
        int samples = (int)(SAMPLE_RATE * 1.0);
        byte[] data = new byte[samples];
        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double freq = 400 - 300 * t; // Descending pitch
            double envelope = Math.max(0, 1.0 - t);
            double tone = Math.sin(2 * Math.PI * freq * t);
            data[i] = (byte)(80 * envelope * tone);
        }
        return data;
    }

    // --- BGM Generators (looping drum patterns) ---

    private static byte[] generateMenuBGM() {
        // 8-second ambient drum loop
        double duration = 8.0;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] data = new byte[samples];
        double bpm = 70;
        double beatLen = 60.0 / bpm;

        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double beatPos = (t % beatLen) / beatLen;
            double val = 0;

            // Deep tabla-like bass on beat
            if (beatPos < 0.1) {
                double env = 1.0 - beatPos / 0.1;
                val += Math.sin(2 * Math.PI * 60 * t) * env * 0.6;
            }
            // Lighter tap on off-beat
            double halfBeat = ((t + beatLen/2) % beatLen) / beatLen;
            if (halfBeat < 0.05) {
                double env = 1.0 - halfBeat / 0.05;
                val += Math.sin(2 * Math.PI * 200 * t) * env * 0.3;
            }
            // Ambient drone
            val += Math.sin(2 * Math.PI * 110 * t) * 0.08;

            data[i] = (byte)(Math.max(-127, Math.min(127, val * 100)));
        }
        return data;
    }

    private static byte[] generateFightBGM() {
        // 6-second intense battle drum loop
        double duration = 6.0;
        int samples = (int)(SAMPLE_RATE * duration);
        byte[] data = new byte[samples];
        double bpm = 140;
        double beatLen = 60.0 / bpm;

        for (int i = 0; i < samples; i++) {
            double t = (double)i / SAMPLE_RATE;
            double beatPos = (t % beatLen) / beatLen;
            double val = 0;

            // Heavy kick drum on beat
            if (beatPos < 0.08) {
                double env = 1.0 - beatPos / 0.08;
                val += Math.sin(2 * Math.PI * 55 * t * (1 + env * 2)) * env * 0.8;
            }
            // Snare-like hit on off-beat
            double halfBeat = ((t + beatLen/2) % beatLen) / beatLen;
            if (halfBeat < 0.05) {
                double env = 1.0 - halfBeat / 0.05;
                val += (random.nextDouble() - 0.5) * env * 0.5;
                val += Math.sin(2 * Math.PI * 300 * t) * env * 0.3;
            }
            // Hi-hat pattern (every 1/4 beat)
            double quarterBeat = ((t) % (beatLen / 4)) / (beatLen / 4);
            if (quarterBeat < 0.02) {
                double env = 1.0 - quarterBeat / 0.02;
                val += (random.nextDouble() - 0.5) * env * 0.15;
            }

            data[i] = (byte)(Math.max(-127, Math.min(127, val * 100)));
        }
        return data;
    }

    // --- WAV Writer ---

    private static void writeWav(File file, byte[] data) throws IOException {
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, data.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
    }
}
