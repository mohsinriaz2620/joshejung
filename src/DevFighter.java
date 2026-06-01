import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * DevFighter.java - DEV (Stone Giant)
 * Massive slow tank villain. Very high HP, devastating damage.
 * Pure brute force - no magic, just raw power.
 */
public class DevFighter extends Fighter {

    public DevFighter() {
        super("DEV", "dev", "The Stone Giant", 5, 150, 60);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Stone Fist", MoveType.LIGHT_PUNCH, 14, 0, Color.SLATEGRAY));
        moves.add(new Move("Boulder Smash", MoveType.HEAVY_PUNCH, 28, 0, Color.DARKGRAY));
        moves.add(new Move("Ground Stomp", MoveType.LIGHT_KICK, 15, 0, Color.GRAY));
        moves.add(new Move("Earthquake", MoveType.HEAVY_KICK, 30, 0, Color.DIMGRAY));
        moves.add(new Move("Avalanche", MoveType.SPECIAL, 50, 35, Color.SADDLEBROWN));
        moves.add(new Move("Mountain Fury", MoveType.SUPER, 70, 60, Color.DARKRED));
        moves.add(new Move("Stone Skin", MoveType.HEAL, 0, 20, Color.GRAY));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 35) return;
        int dmg = 50 + getLevel() * 4 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -60 : 60);
        setSpirit(getSpirit() - 35);
        particles.spawnBigText("AVALANCHE!", getX(), getY() - 240, Color.SADDLEBROWN);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        // Ground slam particles
        for (int i = 0; i < 20; i++) {
            particles.spawnHitParticles(target.getX() + (i - 10) * 15,
                GameConfig.GROUND_Y, Color.SADDLEBROWN, 2);
        }
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_KO);
        particles.triggerHitFlash(Color.SADDLEBROWN);
        AudioManager.playSFX("special.wav");
    }

    @Override
    public void drawSelf(GraphicsContext gc) {
        // Dev is bigger than normal
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 2);
        if (facingLeft) gc.scale(-1, 1);
        // Shadow
        gc.setFill(new Color(0, 0, 0, 0.4));
        gc.fillOval(-55, -5, 110, 20);
        // Large body
        gc.setFill(Color.SLATEGRAY);
        gc.fillRoundRect(-45, -160, 90, 140, 15, 15);
        // Stone texture
        gc.setFill(Color.DIMGRAY);
        gc.fillRoundRect(-40, -150, 35, 30, 5, 5);
        gc.fillRoundRect(5, -130, 30, 25, 5, 5);
        // Head
        gc.setFill(Color.GRAY);
        gc.fillOval(-30, -210, 60, 55);
        // Eyes
        gc.setFill(Color.RED);
        gc.fillOval(-15, -192, 10, 8);
        gc.fillOval(5, -192, 10, 8);
        // Jaw/fangs
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(-15, -170, 30, 12);
        gc.setFill(Color.IVORY);
        gc.fillRect(-10, -170, 5, 8);
        gc.fillRect(5, -170, 5, 8);
        // Legs
        gc.setFill(Color.SLATEGRAY.darker());
        gc.fillRect(-30, -20, 22, 25);
        gc.fillRect(8, -20, 22, 25);
        // Portrait overlay
        if (getPortrait() != null) {
            gc.drawImage(getPortrait(), -50, -240, 100, 100);
        }
        // Hit flash
        if (hitFlashTimer > 0) {
            gc.setFill(new Color(1, 1, 1, hitFlashTimer * 4));
            gc.fillRoundRect(-45, -160, 90, 140, 15, 15);
        }
        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.SLATEGRAY; }
    @Override public Color getAuraColor() { return Color.SADDLEBROWN; }
}
