import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * ChuralFighter.java - CHURAIL (Ghost Witch)
 * Fast, eerie villain from Pakistani folklore.
 * Implements Curseable - can apply damage-over-time curses.
 * Low HP but very fast and disorienting attacks.
 */
public class ChuralFighter extends Fighter implements Curseable {

    private boolean cursed = false;

    public ChuralFighter() {
        super("CHURAIL", "churail", "The Ghost Witch", 4, 80, 100);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Claw Swipe", MoveType.LIGHT_PUNCH, 11, 0, Color.GHOSTWHITE));
        moves.add(new Move("Soul Rend", MoveType.HEAVY_PUNCH, 19, 0, Color.LIMEGREEN));
        moves.add(new Move("Phantom Kick", MoveType.LIGHT_KICK, 12, 0, Color.PALEGREEN));
        moves.add(new Move("Banshee Kick", MoveType.HEAVY_KICK, 21, 0, Color.DARKGREEN));
        moves.add(new Move("Wailing Curse", MoveType.SPECIAL, 38, 28, Color.GREEN));
        moves.add(new Move("Soul Devour", MoveType.SUPER, 55, 55, Color.DARKGREEN));
        moves.add(new Move("Life Drain", MoveType.HEAL, 0, 18, Color.LIME));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 28) return;
        int dmg = 38 + getLevel() * 3 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -25 : 25);
        setSpirit(getSpirit() - 28);
        // Also steal some HP
        heal(10, null);
        particles.spawnBigText("WAILING CURSE!", getX(), getY() - 220, Color.GREEN);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        particles.spawnAura(target.getX(), target.getY() - 80, Color.GREEN, 20);
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_LIGHT);
        particles.triggerHitFlash(Color.GREEN);
        AudioManager.playSFX("special.wav");
    }

    @Override
    public void applyCurse(Fighter target, ParticleSystem particles) {
        cursed = true;
        particles.spawnFloatingText("CURSED!", target.getX(), target.getY() - 200, Color.GREEN);
        particles.spawnAura(target.getX(), target.getY() - 80, Color.DARKGREEN, 15);
    }
    @Override public void removeCurse() { cursed = false; }
    @Override public boolean isCursed() { return cursed; }
    @Override public int getCurseDamagePerSecond() { return 4; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        // Ghostly flickering body - slight transparency variation
        double ghostAlpha = 0.85 + Math.sin(bobTimer * 4) * 0.1;
        gc.save();
        gc.setGlobalAlpha(ghostAlpha);
        drawBase(gc, Color.web("#2a3a2a"), Color.web("#1a2a1a"));
        gc.restore();

        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);

        // Long hair flowing upward (more strands)
        gc.setFill(Color.BLACK);
        for (int i = 0; i < 7; i++) {
            double hx = -25 + i * 8 + Math.sin(bobTimer * 1.5 + i) * 6;
            double hairLen = 35 + i * 4 + Math.sin(bobTimer * 2 + i * 0.5) * 8;
            gc.fillRect(hx, -195, 3, hairLen);
        }

        // Glowing eyes - more intense during attack
        double eyeGlow = isAttacking ? 1.0 : 0.7 + Math.sin(bobTimer * 3) * 0.3;
        gc.setFill(new Color(0, 1, 0.3, eyeGlow));
        gc.fillOval(-14, -162, 8, 6);
        gc.fillOval(6, -162, 8, 6);
        // Eye light trails when attacking
        if (isAttacking) {
            gc.setFill(new Color(0, 1, 0.3, 0.3));
            gc.fillOval(-18, -162, 16, 6);
            gc.fillOval(2, -162, 16, 6);
        }

        // Claw attacks - extend forward
        if (isAttacking) {
            gc.setStroke(Color.web("#00FF66"));
            gc.setLineWidth(2);
            for (int i = 0; i < 3; i++) {
                double clawY = -120 + i * 12;
                gc.strokeLine(35, clawY, 65, clawY - 8);
            }
            // Slash effect
            gc.setFill(new Color(0, 1, 0.4, 0.2));
            gc.fillOval(40, -140, 30, 50);
        }

        // Reversed feet (folklore detail)
        gc.setFill(Color.web("#1a2a1a"));
        gc.fillOval(-22, -8, 14, 10);
        gc.fillOval(8, -8, 14, 10);

        // Ghostly trail
        gc.setFill(new Color(0.5, 1, 0.5, 0.1 + Math.sin(bobTimer * 2) * 0.05));
        gc.fillOval(-55, -150, 110, 170);

        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.DARKGREEN; }
    @Override public Color getAuraColor() { return Color.LIMEGREEN; }
}
