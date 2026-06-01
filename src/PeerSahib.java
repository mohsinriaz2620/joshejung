import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * PeerSahib.java - PEER SAHIB
 * The Mystic Dervish - healer/support with strong defense.
 * Implements Spellcaster and ShieldBearer interfaces.
 * High HP, strong healing, whirling dervish attacks.
 */
public class PeerSahib extends Fighter implements Spellcaster, ShieldBearer {

    private boolean shielded = false;

    public PeerSahib() {
        super("PEER SAHIB", "peer_sahib", "The Mystic Dervish", 2, 110, 90);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Staff Tap", MoveType.LIGHT_PUNCH, 8, 0, Color.WHITE));
        moves.add(new Move("Staff Slam", MoveType.HEAVY_PUNCH, 18, 0, Color.WHEAT));
        moves.add(new Move("Sufi Kick", MoveType.LIGHT_KICK, 9, 0, Color.ANTIQUEWHITE));
        moves.add(new Move("Whirl Kick", MoveType.HEAVY_KICK, 19, 0, Color.BISQUE));
        moves.add(new Move("Dervish Whirl", MoveType.SPECIAL, 35, 25, Color.WHITE));
        moves.add(new Move("Divine Light", MoveType.SUPER, 55, 55, Color.LIGHTYELLOW));
        moves.add(new Move("Greater Shifa", MoveType.HEAL, 0, 15, Color.LIME));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 25) {
            particles.spawnFloatingText("LOW SPIRIT!", getX(), getY() - 200, Color.CYAN);
            return;
        }
        int dmg = 35 + getLevel() * 3 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -30 : 30);
        setSpirit(getSpirit() - 25);
        // Also heals self
        heal(15, particles);
        particles.spawnBigText("DERVISH WHIRL!", getX(), getY() - 220, Color.WHITE);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        particles.spawnAura(getX(), getY() - 80, Color.WHITE, 25);
        particles.spawnHitParticles(target.getX(), target.getY() - 100, Color.WHEAT, 15);
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_LIGHT);
        AudioManager.playSFX("special.wav");
    }

    @Override public void castSpell(Fighter t, ParticleSystem p, HUD h) { performSpecial(t, p, h); }
    @Override public int getSpellDamage() { return 35; }
    @Override public String getSpellName() { return "Dervish Whirl"; }
    @Override public void raiseShield() { shielded = true; setBlocking(true); }
    @Override public void lowerShield() { shielded = false; setBlocking(false); }
    @Override public boolean isShielded() { return shielded; }
    @Override public double getShieldDamageReduction() { return 0.8; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        drawBase(gc, Color.web("#8B7355"), Color.web("#6b5335"));
        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);
        // Staff
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(35, -150, 6, 130);
        // Staff orb
        gc.setFill(new Color(1, 1, 0.8, 0.7 + Math.sin(bobTimer * 3) * 0.2));
        gc.fillOval(32, -160, 14, 14);
        // Tall hat
        gc.setFill(Color.web("#5a4a3a"));
        gc.fillRect(-12, -200, 24, 30);
        // Prayer beads
        gc.setFill(Color.GOLDENROD);
        for (int i = 0; i < 6; i++) {
            gc.fillOval(28 + i * 2, -60 - i * 3, 4, 4);
        }
        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.web("#8B7355"); }
    @Override public Color getAuraColor() { return Color.WHITE; }
}
