import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * NoorJahan.java - NOOR JAHAN
 * The Mughal Sorceress - powerful ranged spellcaster.
 * Implements Spellcaster and Curseable interfaces.
 * High spirit, lower HP, devastating magical attacks.
 */
public class NoorJahan extends Fighter implements Spellcaster, Curseable {

    private boolean cursed = false;
    private int curseDps = 5;

    public NoorJahan() {
        super("NOOR JAHAN", "noor_jahan", "The Mughal Sorceress", 1, 85, 120);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Arcane Slap", MoveType.LIGHT_PUNCH, 8, 0, Color.PURPLE));
        moves.add(new Move("Mystic Blast", MoveType.HEAVY_PUNCH, 18, 5, Color.MEDIUMPURPLE));
        moves.add(new Move("Energy Kick", MoveType.LIGHT_KICK, 10, 0, Color.ORCHID));
        moves.add(new Move("Void Kick", MoveType.HEAVY_KICK, 20, 5, Color.DARKVIOLET));
        moves.add(new Move("Nazar-e-Bad", MoveType.SPECIAL, 45, 35, Color.PURPLE));
        moves.add(new Move("Mughal Storm", MoveType.SUPER, 65, 70, Color.MAGENTA));
        moves.add(new Move("Restoration", MoveType.HEAL, 0, 15, Color.LIME));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 35) {
            particles.spawnFloatingText("LOW SPIRIT!", getX(), getY() - 200, Color.CYAN);
            return;
        }
        int dmg = 45 + getLevel() * 4 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -35 : 35);
        setSpirit(getSpirit() - 35);
        particles.spawnBigText("NAZAR-E-BAD!", getX(), getY() - 220, Color.PURPLE);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        particles.spawnLightningBolt(getX(), getY() - 150, target.getX(), target.getY() - 100);
        particles.spawnAura(target.getX(), target.getY() - 80, Color.PURPLE, 20);
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_HEAVY);
        particles.triggerHitFlash(Color.PURPLE);
        AudioManager.playSFX("special.wav");
    }

    @Override public void castSpell(Fighter t, ParticleSystem p, HUD h) { performSpecial(t, p, h); }
    @Override public int getSpellDamage() { return 45; }
    @Override public String getSpellName() { return "Nazar-e-Bad"; }

    @Override
    public void applyCurse(Fighter target, ParticleSystem particles) {
        cursed = true;
        particles.spawnFloatingText("CURSED!", target.getX(), target.getY() - 200, Color.DARKVIOLET);
        particles.spawnAura(target.getX(), target.getY() - 80, Color.DARKVIOLET, 15);
    }
    @Override public void removeCurse() { cursed = false; }
    @Override public boolean isCursed() { return cursed; }
    @Override public int getCurseDamagePerSecond() { return curseDps; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        drawBase(gc, Color.INDIGO, Color.web("#4a1a6a"));
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);

        // Flowing dupatta (scarf)
        gc.setFill(new Color(0.6, 0.1, 0.8, 0.3));
        double scarfWave = Math.sin(bobTimer * 2) * 8;
        gc.fillRect(-35, -170, 6, 50 + scarfWave);
        gc.fillRect(-38, -120, 4, 30 + scarfWave);

        // Crown/headpiece with jewels
        gc.setFill(Color.GOLD);
        gc.fillRect(-18, -178, 36, 6);
        gc.setFill(Color.web("#FFD700"));
        gc.fillOval(-8, -188, 16, 14);
        gc.setFill(Color.web("#FF00FF"));
        gc.fillOval(-4, -185, 8, 8); // Central jewel
        // Side jewels
        gc.setFill(Color.web("#9400D3"));
        gc.fillOval(-16, -182, 6, 6);
        gc.fillOval(10, -182, 6, 6);

        // Magical orb in hand - animate forward when attacking
        double orbX = isAttacking ? 55 : 32;
        double orbY = isAttacking ? -110 : -90;
        double orbSize = isAttacking ? 28 : 20;
        double orbPulse = Math.sin(bobTimer * 3) * 0.2;
        gc.setFill(new Color(0.6, 0.2, 0.8, 0.5 + orbPulse));
        gc.fillOval(orbX, orbY, orbSize, orbSize);
        // Inner glow
        gc.setFill(new Color(1, 0.5, 1, 0.4 + orbPulse));
        gc.fillOval(orbX + orbSize * 0.2, orbY + orbSize * 0.2, orbSize * 0.6, orbSize * 0.6);
        // White sparkle
        gc.setFill(new Color(1, 1, 1, 0.6));
        gc.fillOval(orbX + 4, orbY + 3, 5, 5);

        // Attack magic trail
        if (isAttacking) {
            for (int i = 0; i < 3; i++) {
                double trailX = orbX - 10 - i * 12;
                double trailAlpha = 0.3 - i * 0.1;
                gc.setFill(new Color(0.8, 0.2, 1.0, trailAlpha));
                gc.fillOval(trailX, orbY + 5, 10, 10);
            }
        }

        // Mystical aura
        gc.setFill(new Color(0.5, 0.1, 0.7, 0.05 + Math.sin(bobTimer * 2) * 0.03));
        gc.fillOval(-55, -200, 110, 220);

        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.INDIGO; }
    @Override public Color getAuraColor() { return Color.PURPLE; }
}
