import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.List;

/**
 * ComboSystem.java
 * Tracks player input sequences for combo detection.
 * Supports chain combos with timing windows.
 * Displays combo count and manages damage multipliers.
 */
public class ComboSystem {
    private List<KeyCode> inputBuffer;
    private long lastInputTime;
    private int hitCount;
    private double damageMultiplier;
    private double comboTimer;

    public ComboSystem() {
        inputBuffer = new ArrayList<>();
        hitCount = 0;
        damageMultiplier = 1.0;
        comboTimer = 0;
    }

    public void registerInput(KeyCode key) {
        long now = System.currentTimeMillis();
        if (now - lastInputTime > GameConfig.MAX_COMBO_WINDOW_MS) {
            inputBuffer.clear();
        }
        inputBuffer.add(key);
        lastInputTime = now;
        if (inputBuffer.size() > 10) inputBuffer.remove(0);
    }

    public void registerHit() {
        hitCount++;
        comboTimer = 0.8;
        damageMultiplier = 1.0 + (hitCount - 1) * 0.15;
        damageMultiplier = Math.min(damageMultiplier, 3.0);
    }

    public void update(double dt) {
        if (comboTimer > 0) {
            comboTimer -= dt;
            if (comboTimer <= 0) {
                hitCount = 0;
                damageMultiplier = 1.0;
                inputBuffer.clear();
            }
        }
    }

    public void reset() {
        inputBuffer.clear();
        hitCount = 0;
        damageMultiplier = 1.0;
        comboTimer = 0;
    }

    public int getHitCount() { return hitCount; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public boolean isInCombo() { return hitCount > 1; }
    public List<KeyCode> getInputBuffer() { return inputBuffer; }
}
