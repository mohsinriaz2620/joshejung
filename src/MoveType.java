/**
 * MoveType.java
 * Enum representing the different types of combat moves.
 * Each type has base properties that modify damage calculation.
 * Demonstrates proper enum usage with fields and constructors.
 */
public enum MoveType {
    LIGHT_PUNCH("Light Punch", 8, 0.3, 5.0, false),
    HEAVY_PUNCH("Heavy Punch", 15, 0.6, 15.0, false),
    LIGHT_KICK("Light Kick", 10, 0.35, 8.0, false),
    HEAVY_KICK("Heavy Kick", 18, 0.7, 20.0, false),
    SPECIAL("Special", 30, 1.0, 30.0, true),
    SUPER("Super", 50, 1.5, 45.0, true),
    GRAB("Grab", 20, 0.8, 10.0, false),
    BLOCK("Block", 0, 0.0, 0.0, false),
    HEAL("Heal", 0, 0.5, 0.0, true);

    private final String displayName;
    private final int baseDamage;
    private final double cooldown;     // seconds
    private final double knockback;
    private final boolean usesSpirit;

    MoveType(String displayName, int baseDamage, double cooldown,
             double knockback, boolean usesSpirit) {
        this.displayName = displayName;
        this.baseDamage = baseDamage;
        this.cooldown = cooldown;
        this.knockback = knockback;
        this.usesSpirit = usesSpirit;
    }

    public String getDisplayName() { return displayName; }
    public int getBaseDamage() { return baseDamage; }
    public double getCooldown() { return cooldown; }
    public double getKnockback() { return knockback; }
    public boolean usesSpirit() { return usesSpirit; }
}
