import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * SufiWarrior.java - AL-MURTAZA
 * The main hero - a Sufi warrior wielding the legendary Zulfiqar sword.
 * Implements Spellcaster and ShieldBearer interfaces.
 * Balanced fighter with moderate stats across the board.
 */
public class SufiWarrior extends Fighter implements Spellcaster, ShieldBearer {

    private boolean shielded = false;

    public SufiWarrior() {
        super("AL-MURTAZA", "al_murtaza", "The Sufi Warrior", 0, 100, 100);
    }

    @Override
    protected void initMoves() {
        moves.add(new Move("Jab", MoveType.LIGHT_PUNCH, 10, 0, Color.YELLOW));
        moves.add(new Move("Zulfiqar Slash", MoveType.HEAVY_PUNCH, 20, 0, Color.GOLD));
        moves.add(new Move("Swift Kick", MoveType.LIGHT_KICK, 12, 0, Color.ORANGE));
        moves.add(new Move("Crescent Kick", MoveType.HEAVY_KICK, 22, 0, Color.ORANGERED));
        moves.add(new Move("Zulfiqar Fury", MoveType.SPECIAL, 40, 30, Color.GOLD));
        moves.add(new Move("Divine Wrath", MoveType.SUPER, 60, 60, Color.WHITE));
        moves.add(new Move("Shifa", MoveType.HEAL, 0, 20, Color.LIME));
    }

    @Override
    public void performSpecial(Fighter target, ParticleSystem particles, HUD hud) {
        if (getSpirit() < 30) {
            particles.spawnFloatingText("LOW SPIRIT!", getX(), getY() - 200, Color.CYAN);
            return;
        }
        int dmg = 40 + getLevel() * 3 + getAttackBonus();
        if (target.isBlocking()) dmg = (int)(dmg * 0.3);
        target.takeDamage(dmg, particles);
        target.applyKnockback(isFacingLeft() ? -45 : 45);
        setSpirit(getSpirit() - 30);
        particles.spawnBigText("ZULFIQAR!", getX(), getY() - 220, Color.GOLD);
        particles.spawnFloatingText("-" + dmg, target.getX(), target.getY() - 180, Color.RED);
        particles.spawnSparks(target.getX(), target.getY() - 100, Color.GOLD, 25);
        particles.spawnFireBurst(target.getX(), target.getY() - 80, 15);
        particles.triggerShake(GameConfig.SHAKE_INTENSITY_HEAVY);
        particles.triggerHitFlash(Color.GOLD);
        AudioManager.playSFX("special.wav");
    }

    @Override
    public void castSpell(Fighter target, ParticleSystem particles, HUD hud) {
        performSpecial(target, particles, hud);
    }
    @Override public int getSpellDamage() { return 40; }
    @Override public String getSpellName() { return "Zulfiqar Fury"; }

    @Override public void raiseShield() { shielded = true; setBlocking(true); }
    @Override public void lowerShield() { shielded = false; setBlocking(false); }
    @Override public boolean isShielded() { return shielded; }
    @Override public double getShieldDamageReduction() { return 0.75; }

    @Override
    public void drawSelf(GraphicsContext gc) {
        drawBase(gc, Color.DARKBLUE, Color.web("#1a3a6a"));

        gc.save();
        gc.translate(x, y + Math.sin(bobTimer) * 3);
        if (facingLeft) gc.scale(-1, 1);

        // Turban with wrapped layers
        gc.setFill(Color.WHITE);
        gc.fillOval(-18, -178, 36, 18);
        gc.fillRect(-15, -175, 30, 8);
        gc.setFill(Color.web("#e0e0e0"));
        gc.fillOval(-20, -182, 40, 12);
        // Turban tail
        gc.setFill(Color.WHITE);
        double tailWave = Math.sin(bobTimer * 2) * 5;
        gc.fillRect(-25, -170, 8, 25 + tailWave);

        // Flowing garment trim
        gc.setFill(Color.web("#2a5a9a"));
        gc.fillRect(-28, -80, 56, 8);
        gc.setFill(Color.GOLD);
        gc.fillRect(-28, -82, 56, 3);

        // Sword - animate forward when attacking
        double swordAngle = isAttacking ? -30 : 15;
        double swordX = isAttacking ? 45 : 30;
        double swordY = isAttacking ? -130 : -100;
        gc.save();
        gc.translate(swordX, swordY);
        gc.rotate(swordAngle);
        // Blade
        gc.setFill(Color.SILVER);
        gc.fillRect(-4, 0, 8, 75);
        // Blade edge highlight
        gc.setFill(new Color(1, 1, 1, 0.4));
        gc.fillRect(-2, 0, 4, 75);
        // Guard (cross-piece)
        gc.setFill(Color.GOLD);
        gc.fillRect(-12, -5, 24, 8);
        // Handle
        gc.setFill(Color.web("#8B4513"));
        gc.fillRect(-3, 75, 6, 18);
        // Pommel
        gc.setFill(Color.GOLD);
        gc.fillOval(-5, 90, 10, 10);
        gc.restore();

        // Sword glow when attacking
        if (isAttacking) {
            gc.setFill(new Color(1, 0.85, 0.2, 0.25));
            gc.fillOval(swordX - 25, swordY - 10, 50, 90);
        }

        // Aura glow behind character
        gc.setFill(new Color(1, 0.85, 0.2, 0.06 + Math.sin(bobTimer * 2) * 0.03));
        gc.fillOval(-60, -200, 120, 220);

        gc.restore();
    }

    @Override public Color getPrimaryColor() { return Color.DARKBLUE; }
    @Override public Color getAuraColor() { return Color.GOLD; }
}
