import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * JinnFighter.java - JINN (Fire Spirit)
 * Fast agile villain made of smokeless fire.
 * High speed, moderate damage, fire-based attacks.
 */
public class JinnFighter extends Fighter implements Spellcaster {

    public JinnFighter() {
        super("JINN", "jinn", "The Fire Spirit", 6, 90, 95);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Fire Jab", MoveType.LIGHT_PUNCH, 11, 0, Color.ORANGERED));
        moves.add(new Move("Inferno Punch", MoveType.HEAVY_PUNCH, 21, 0, Color.RED));
        moves.add(new Move("Flame Kick", MoveType.LIGHT_KICK, 12, 0, Color.ORANGE));
        moves.add(new Move("Blaze Kick", MoveType.HEAVY_KICK, 23, 0, Color.DARKRED));
        moves.add(new Move("Smokeless Fire", MoveType.SPECIAL, 43, 30, Color.ORANGERED));
        moves.add(new Move("Hellfire Storm", MoveType.SUPER, 62, 60, Color.DARKRED));
        moves.add(new Move("Ember Heal", MoveType.HEAL, 0, 20, Color.ORANGE));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 30) return;
        int dmg = 43 + getLevel() * 3 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -35 : 35);
        setSpirit(getSpirit() - 30);
        particles.spawnBigText("SMOKELESS FIRE!", getX(), getY() - 220, Color.ORANGERED);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        particles.spawnFireBurst(target.getX(), target.getY() - 60, 30);
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_HEAVY);
        particles.triggerHitFlash(Color.ORANGERED);
        AudioManager.playSFX("special.wav");
    }

    @Override public void castSpell(Fighter t, ParticleSystem p, HUD h) { performSpecial(t, p, h); }
    @Override public int getSpellDamage() { return 43; }
    @Override public String getSpellName() { return "Smokeless Fire"; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);
        // Shadow
        gc.setFill(new Color(0, 0, 0, 0.3));
        gc.fillOval(-35, -5, 70, 15);
        // Fiery body (semi-transparent)
        gc.setFill(new Color(0.8, 0.2, 0, 0.85));
        gc.fillOval(-30, -130, 60, 120);
        // Inner fire
        gc.setFill(new Color(1, 0.5, 0, 0.6 + Math.sin(bobTimer * 4) * 0.2));
        gc.fillOval(-20, -120, 40, 90);
        // Head
        gc.setFill(Color.DARKRED);
        gc.fillOval(-22, -170, 44, 44);
        // Flame eyes
        gc.setFill(new Color(1, 0.8, 0, 0.9));
        gc.fillOval(-12, -158, 10, 8);
        gc.fillOval(4, -158, 10, 8);
        // Fire emanating from body
        for (int i = 0; i < 4; i++) {
            double fx = -25 + i * 15 + Math.sin(bobTimer * 3 + i) * 8;
            double fy = -140 - i * 8 + Math.cos(bobTimer * 4 + i) * 5;
            gc.setFill(new Color(1, 0.6, 0, 0.4));
            gc.fillOval(fx, fy, 12, 18);
        }
        // Smoke wisps
        gc.setFill(new Color(0.3, 0.3, 0.3, 0.2));
        gc.fillOval(-40, -160, 25, 35);
        gc.fillOval(20, -150, 20, 30);
        // Portrait overlay
        if (getPortrait() != null) {
            gc.drawImage(getPortrait(), -45, -210, 90, 90);
        }
        // Hit flash
        if (hitFlashTimer > 0) {
            gc.setFill(new Color(1, 1, 1, hitFlashTimer * 4));
            gc.fillOval(-30, -130, 60, 120);
        }
        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.DARKRED; }
    @Override public Color getAuraColor() { return Color.ORANGERED; }
}
