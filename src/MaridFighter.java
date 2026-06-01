import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * MaridFighter.java - MARID (Water Djinn)
 * Powerful water-based villain with strong knockback.
 * High HP, moderate spirit, devastating water attacks.
 */
public class MaridFighter extends Fighter {

    public MaridFighter() {
        super("MARID", "marid", "The Water Djinn", 3, 120, 80);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Water Fist", MoveType.LIGHT_PUNCH, 12, 0, Color.AQUA));
        moves.add(new Move("Tidal Slam", MoveType.HEAVY_PUNCH, 22, 0, Color.DARKCYAN));
        moves.add(new Move("Wave Kick", MoveType.LIGHT_KICK, 13, 0, Color.AQUAMARINE));
        moves.add(new Move("Tsunami Kick", MoveType.HEAVY_KICK, 24, 0, Color.TEAL));
        moves.add(new Move("Tidal Crush", MoveType.SPECIAL, 42, 30, Color.AQUA));
        moves.add(new Move("Abyssal Flood", MoveType.SUPER, 58, 60, Color.DARKBLUE));
        moves.add(new Move("Water Heal", MoveType.HEAL, 0, 20, Color.AQUAMARINE));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 30) return;
        int dmg = 42 + getLevel() * 3 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -50 : 50);
        setSpirit(getSpirit() - 30);
        particles.spawnBigText("TIDAL CRUSH!", getX(), getY() - 220, Color.AQUA);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        for (int i = 0; i < 30; i++) {
            particles.spawnHitParticles(target.getX(), target.getY() - 60, Color.AQUA, 3);
        }
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_HEAVY);
        particles.triggerHitFlash(Color.AQUA);
        AudioManager.playSFX("special.wav");
    }

    @Override
    public void drawSelf(GraphicsContext gc) {
        drawBase(gc, Color.DARKCYAN, Color.web("#1a5a5a"));
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);
        // Tusks
        gc.setFill(Color.IVORY);
        gc.fillRect(-18, -155, 5, 15);
        gc.fillRect(13, -155, 5, 15);
        // Water swirls
        gc.setFill(new Color(0, 1, 1, 0.3 + Math.sin(bobTimer * 2) * 0.2));
        gc.fillOval(-50, -100, 30, 30);
        gc.fillOval(25, -120, 25, 25);
        // Chains
        gc.setStroke(Color.DARKGOLDENROD); gc.setLineWidth(3);
        gc.strokeLine(-35, -80, -45, -50);
        gc.strokeLine(35, -80, 45, -50);
        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.DARKCYAN; }
    @Override public Color getAuraColor() { return Color.AQUA; }
}
