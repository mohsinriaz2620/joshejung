import javafx.scene.paint.Color;

/**
 * Move.java
 * Represents a single combat move with all its properties.
 * Used by Fighter subclasses to define their unique movesets.
 * Demonstrates composition (Move HAS-A MoveType).
 */
public class Move {
    private final String name;
    private final MoveType type;
    private final int damage;
    private final int spiritCost;
    private final double range;
    private final double knockbackForce;
    private final double cooldownTime;
    private final Color effectColor;
    private final int particleCount;
    private final String soundFile;

    private double cooldownRemaining = 0;

    public Move(String name, MoveType type, int damage, int spiritCost,
                double range, double knockbackForce, Color effectColor,
                int particleCount, String soundFile) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.spiritCost = spiritCost;
        this.range = range;
        this.knockbackForce = knockbackForce;
        this.cooldownTime = type.getCooldown();
        this.effectColor = effectColor;
        this.particleCount = particleCount;
        this.soundFile = soundFile;
    }

    // Convenience constructor for simple moves
    public Move(String name, MoveType type, int damage, int spiritCost, Color effectColor) {
        this(name, type, damage, spiritCost, GameConfig.ATTACK_RANGE,
             type.getKnockback(), effectColor, 10, null);
    }

    public boolean isReady() {
        return cooldownRemaining <= 0;
    }

    public void trigger() {
        cooldownRemaining = cooldownTime;
    }

    public void updateCooldown(double dt) {
        if (cooldownRemaining > 0) {
            cooldownRemaining -= dt;
        }
    }

    public double getCooldownPercent() {
        if (cooldownTime <= 0) return 0;
        return Math.max(0, cooldownRemaining / cooldownTime);
    }

    // Getters
    public String getName() { return name; }
    public MoveType getType() { return type; }
    public int getDamage() { return damage; }
    public int getSpiritCost() { return spiritCost; }
    public double getRange() { return range; }
    public double getKnockbackForce() { return knockbackForce; }
    public Color getEffectColor() { return effectColor; }
    public int getParticleCount() { return particleCount; }
    public String getSoundFile() { return soundFile; }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ") DMG:" + damage;
    }
}
