import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * IblisFighter.java - IBLIS (The Dark Lord)
 * FINAL BOSS. The ultimate evil from Islamic mythology.
 * Highest stats, devastating attacks, unique boss mechanics.
 * Implements Spellcaster and Curseable.
 */
public class IblisFighter extends Fighter implements Spellcaster, Curseable {

    private boolean cursed = false;

    public IblisFighter() {
        super("IBLIS", "iblis", "The Dark Lord", 7, 180, 120);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Shadow Strike", MoveType.LIGHT_PUNCH, 14, 0, Color.DARKRED));
        moves.add(new Move("Doom Fist", MoveType.HEAVY_PUNCH, 26, 0, Color.CRIMSON));
        moves.add(new Move("Hell Kick", MoveType.LIGHT_KICK, 15, 0, Color.ORANGERED));
        moves.add(new Move("Infernal Kick", MoveType.HEAVY_KICK, 28, 0, Color.MAROON));
        moves.add(new Move("Hellfire Rain", MoveType.SPECIAL, 55, 30, Color.DARKRED));
        moves.add(new Move("Apocalypse", MoveType.SUPER, 80, 70, Color.BLACK));
        moves.add(new Move("Dark Regen", MoveType.HEAL, 0, 25, Color.DARKRED));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 30) return;
        int dmg = 55 + getLevel() * 5 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -55 : 55);
        setSpirit(getSpirit() - 30);
        particles.spawnBigText("HELLFIRE RAIN!", getX(), getY() - 240, Color.DARKRED);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        // Massive fire burst
        particles.spawnFireBurst(target.getX(), target.getY() - 60, 40);
        particles.spawnSparks(target.getX(), target.getY() - 100, Color.RED, 20);
        // Fire from above
        for (int i = 0; i < 5; i++) {
            particles.spawnFireBurst(target.getX() + (i - 2) * 40,
                target.getY() - 200, 8);
        }
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_KO);
        particles.triggerHitFlash(Color.DARKRED);
        particles.triggerSlowMo(0.4);
        AudioManager.playSFX("special.wav");
    }

    @Override public void castSpell(Fighter t, ParticleSystem p, HUD h) { performSpecial(t, p, h); }
    @Override public int getSpellDamage() { return 55; }
    @Override public String getSpellName() { return "Hellfire Rain"; }

    @Override
    public void applyCurse(Fighter target, ParticleSystem particles) {
        cursed = true;
        particles.spawnFloatingText("DOOMED!", target.getX(), target.getY() - 200, Color.DARKRED);
    }
    @Override public void removeCurse() { cursed = false; }
    @Override public boolean isCursed() { return cursed; }
    @Override public int getCurseDamagePerSecond() { return 8; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 2);
        if (facingLeft) gc.scale(-1, 1);

        // Dark aura behind
        gc.setFill(new Color(0.3, 0, 0, 0.08 + Math.sin(bobTimer) * 0.04));
        gc.fillOval(-80, -270, 160, 290);

        // Shadow
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillOval(-55, -5, 110, 20);

        // Dark wings - spread wider during attack
        double wingSpread = isAttacking ? 1.3 : 1.0;
        gc.setFill(new Color(0.1, 0, 0, 0.5));
        gc.fillPolygon(
            new double[]{-40, -90 * wingSpread, -65 * wingSpread, -55},
            new double[]{-140, -210, -110, -80}, 4);
        gc.fillPolygon(
            new double[]{40, 90 * wingSpread, 65 * wingSpread, 55},
            new double[]{-140, -210, -110, -80}, 4);
        // Wing membrane veins
        gc.setStroke(new Color(0.5, 0, 0, 0.3));
        gc.setLineWidth(1);
        gc.strokeLine(-42, -140, -75 * wingSpread, -180);
        gc.strokeLine(-45, -120, -70 * wingSpread, -150);
        gc.strokeLine(42, -140, 75 * wingSpread, -180);
        gc.strokeLine(45, -120, 70 * wingSpread, -150);

        // Large dark body
        gc.setFill(Color.web("#1a0000"));
        gc.fillRoundRect(-45, -170, 90, 155, 12, 12);

        // Armor plates
        gc.setFill(Color.web("#2a0000"));
        gc.fillRoundRect(-38, -160, 76, 30, 6, 6);
        gc.fillRoundRect(-35, -90, 70, 25, 6, 6);

        // Lava cracks on body - pulsing
        double lavaGlow = 0.6 + Math.sin(bobTimer * 2) * 0.3;
        gc.setStroke(new Color(1, 0.3, 0, lavaGlow));
        gc.setLineWidth(2);
        gc.strokeLine(-20, -140, -5, -100);
        gc.strokeLine(5, -150, 20, -110);
        gc.strokeLine(-10, -80, 10, -50);
        gc.strokeLine(-25, -110, -15, -70);
        gc.strokeLine(15, -130, 25, -90);

        // Head
        gc.setFill(Color.BLACK);
        gc.fillOval(-30, -220, 60, 55);

        // Horns with gradient
        gc.setFill(Color.web("#3a0000"));
        gc.fillPolygon(new double[]{-25, -35, -15}, new double[]{-215, -265, -215}, 3);
        gc.fillPolygon(new double[]{25, 35, 15}, new double[]{-215, -265, -215}, 3);
        // Horn tips glow
        gc.setFill(new Color(1, 0.2, 0, 0.5));
        gc.fillOval(-38, -268, 8, 8);
        gc.fillOval(30, -268, 8, 8);

        // Burning eyes
        double eyeIntensity = isAttacking ? 1.0 : 0.9 + Math.sin(bobTimer * 5) * 0.1;
        gc.setFill(new Color(1, 0.4, 0, eyeIntensity));
        gc.fillOval(-15, -205, 12, 10);
        gc.fillOval(5, -205, 12, 10);
        // Eye glow radius
        gc.setFill(new Color(1, 0.3, 0, 0.2));
        gc.fillOval(-18, -208, 18, 16);
        gc.fillOval(2, -208, 18, 16);

        // Fire crown
        for (int i = 0; i < 7; i++) {
            double fx = -24 + i * 8 + Math.sin(bobTimer * 3 + i) * 3;
            double flameH = 12 + Math.sin(bobTimer * 4 + i * 0.7) * 5;
            gc.setFill(new Color(0.9, 0.3, 0, 0.5));
            gc.fillOval(fx, -245 - flameH, 6, flameH);
            gc.setFill(new Color(1, 0.6, 0, 0.3));
            gc.fillOval(fx + 1, -243 - flameH * 0.7, 4, flameH * 0.7);
        }

        // Attack: fire fists
        if (isAttacking) {
            gc.setFill(new Color(1, 0.4, 0, 0.6));
            gc.fillOval(40, -140, 25, 25);
            gc.setFill(new Color(1, 0.7, 0, 0.4));
            gc.fillOval(45, -135, 15, 15);
            // Fire trail
            for (int i = 0; i < 4; i++) {
                gc.setFill(new Color(1, 0.3, 0, 0.3 - i * 0.07));
                gc.fillOval(35 - i * 8, -138 + i * 3, 12, 12);
            }
        }

        // Legs
        gc.setFill(Color.web("#0a0000"));
        gc.fillRect(-28, -15, 20, 20);
        gc.fillRect(8, -15, 20, 20);

        // Portrait overlay
        if (getPortrait() != null) {
            gc.drawImage(getPortrait(), -50, -260, 100, 100);
        }

        // Hit flash
        if (hitFlashTimer > 0) {
            gc.setFill(new Color(1, 1, 1, hitFlashTimer * 4));
            gc.fillRoundRect(-45, -170, 90, 155, 12, 12);
        }

        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.DARKRED; }
    @Override public Color getAuraColor() { return Color.RED; }
}
