import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Fighter.java
 * Abstract base class for all playable and enemy fighters.
 * Encapsulates position, health, spirit, physics, combat state,
 * sprite rendering, and move lists. All 8 characters extend this class.
 *
 * Demonstrates:
 * - Abstraction (abstract methods for unique behavior)
 * - Encapsulation (private fields, public getters/setters)
 * - Polymorphism (overridden performAttack, performSpecial, drawSelf)
 */
public abstract class Fighter {

    // --- Identity ---
    private String name;
    private String characterId;
    private String title;
    private int index; // Index in CHARACTER_IDS array

    // --- Stats ---
    private int hp;
    private int maxHp;
    private int spirit;
    private int maxSpirit;
    private int level;
    private int attackBonus;
    private int defenseBonus;

    // --- Position & Physics ---
    protected double x, y;
    protected double targetX;
    protected double velocityX, velocityY;
    protected boolean onGround;
    protected boolean facingLeft;

    // --- Combat State ---
    protected boolean isBlocking;
    protected boolean isAttacking;
    protected boolean isHit;
    protected double hitStunTimer;
    protected double attackTimer;
    protected double hitFlashTimer;
    protected int comboCount;
    protected double comboTimer;
    protected boolean isDashing;
    protected double dashTimer;

    // --- Animation ---
    protected double bobTimer;
    protected double breatheScale;
    protected Image portrait;

    // --- Moves ---
    protected List<Move> moves;

    // --- Round tracking ---
    private int roundsWon;

    // Constructor
    public Fighter(String name, String characterId, String title, int index,
                   int maxHp, int maxSpirit) {
        this.name = name;
        this.characterId = characterId;
        this.title = title;
        this.index = index;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.maxSpirit = maxSpirit;
        this.spirit = maxSpirit;
        this.level = 1;
        this.attackBonus = 0;
        this.defenseBonus = 0;
        this.onGround = true;
        this.facingLeft = false;
        this.isBlocking = false;
        this.isAttacking = false;
        this.isHit = false;
        this.hitStunTimer = 0;
        this.attackTimer = 0;
        this.hitFlashTimer = 0;
        this.comboCount = 0;
        this.comboTimer = 0;
        this.isDashing = false;
        this.dashTimer = 0;
        this.bobTimer = 0;
        this.breatheScale = 1.0;
        this.roundsWon = 0;
        this.moves = new ArrayList<>();

        // Load portrait
        this.portrait = SpriteRenderer.loadCharacterPortrait(characterId);

        // Initialize moves (subclass defines these)
        initMoves();
    }

    /**
     * Abstract: Each fighter defines its unique moveset.
     */
    protected abstract void initMoves();

    /**
     * Abstract: Each fighter has a unique special attack.
     */
    public abstract void performSpecial(Fighter target, ParticleSystem particles, HUD hud);

    /**
     * Abstract: Each fighter draws itself uniquely on canvas
     * (procedural sprite + portrait overlay).
     */
    public abstract void drawSelf(GraphicsContext gc);

    /**
     * Abstract: Each fighter has unique primary attack visual.
     */
    public abstract Color getPrimaryColor();

    /**
     * Abstract: Each fighter has a unique aura color.
     */
    public abstract Color getAuraColor();

    // --- Common Combat Methods ---

    /**
     * Performs a basic attack on the target.
     */
    public void performAttack(Fighter target, ParticleSystem particles, HUD hud, MoveType type) {
        Move move = findMove(type);
        if (move == null || !move.isReady()) return;
        if (type.usesSpirit() && spirit < move.getSpiritCost()) return;

        int totalDamage = move.getDamage() + attackBonus + (level * 2);

        if (target.isBlocking) {
            totalDamage = (int)(totalDamage * (1.0 - GameConfig.BLOCK_DAMAGE_REDUCTION));
            particles.spawnFloatingText("BLOCKED!", target.x, target.y - 200, Color.LIGHTBLUE);
            particles.spawnSparks(target.x, target.y - 100, Color.LIGHTBLUE, 8);
        } else {
            // Apply defense reduction
            totalDamage = Math.max(1, totalDamage - target.defenseBonus);
        }

        target.takeDamage(totalDamage, particles);
        target.applyKnockback(facingLeft ? -move.getKnockbackForce() : move.getKnockbackForce());

        // Spirit cost
        if (type.usesSpirit()) {
            spirit -= move.getSpiritCost();
        }

        // Trigger cooldown
        move.trigger();

        // Screen effects
        particles.triggerShake(type == MoveType.HEAVY_PUNCH || type == MoveType.HEAVY_KICK
            ? GameConfig.SHAKE_INTENSITY_HEAVY : GameConfig.SHAKE_INTENSITY_LIGHT);

        // Spawn effect particles
        particles.spawnHitParticles(target.x, target.y - 100, move.getEffectColor(),
            move.getParticleCount());

        // Floating damage text
        particles.spawnFloatingText("-" + totalDamage, target.x, target.y - 180,
            totalDamage >= 25 ? Color.RED : Color.YELLOW);

        // Combo tracking
        comboCount++;
        comboTimer = 0.6;
        if (comboCount > 1) {
            particles.spawnBigText(comboCount + " HIT COMBO!", x, y - 250, GameConfig.COLOR_GOLD);
        }

        // Attack animation
        isAttacking = true;
        attackTimer = 0.15;
    }

    /**
     * Take damage, applying hit stun and flash.
     */
    public void takeDamage(int damage, ParticleSystem particles) {
        hp = Math.max(0, hp - damage);
        isHit = true;
        hitStunTimer = 0.2;
        hitFlashTimer = 0.15;
    }

    /**
     * Apply knockback force.
     */
    public void applyKnockback(double force) {
        velocityX += force;
    }

    /**
     * Heal the fighter.
     */
    public void heal(int amount, ParticleSystem particles) {
        int healAmount = Math.min(amount, maxHp - hp);
        hp += healAmount;
        if (particles != null) {
            particles.spawnFloatingText("+" + healAmount + " SHIFA", x, y - 180, Color.LIME);
            particles.spawnHealEffect(x, y - 80, 20);
        }
    }

    // --- Physics & Update ---

    public void update(double dt) {
        // Update move cooldowns
        for (Move m : moves) {
            m.updateCooldown(dt);
        }

        // Gravity
        if (!onGround) {
            velocityY += GameConfig.GRAVITY * dt;
            y += velocityY * dt;
            if (y >= GameConfig.GROUND_Y) {
                y = GameConfig.GROUND_Y;
                velocityY = 0;
                onGround = true;
            }
        }

        // Horizontal movement (lerp + knockback)
        if (isDashing) {
            dashTimer -= dt;
            if (dashTimer <= 0) isDashing = false;
        }

        x += velocityX * dt;
        velocityX *= GameConfig.KNOCKBACK_DECAY;

        // Lerp toward target
        if (!isDashing && Math.abs(velocityX) < 50) {
            x += (targetX - x) * GameConfig.LERP_SPEED;
        }

        // Keep in bounds
        x = Math.max(60, Math.min(GameConfig.CANVAS_WIDTH - 60, x));

        // Timers
        if (hitStunTimer > 0) {
            hitStunTimer -= dt;
            if (hitStunTimer <= 0) isHit = false;
        }
        if (attackTimer > 0) {
            attackTimer -= dt;
            if (attackTimer <= 0) isAttacking = false;
        }
        if (hitFlashTimer > 0) {
            hitFlashTimer -= dt;
        }
        if (comboTimer > 0) {
            comboTimer -= dt;
            if (comboTimer <= 0) comboCount = 0;
        }

        // Spirit regeneration
        if (spirit < maxSpirit) {
            spirit = Math.min(maxSpirit, spirit + 1);
        }

        // Bob animation
        bobTimer += dt * 3;
        breatheScale = 1.0 + Math.sin(bobTimer) * 0.02;
    }

    /**
     * Draws the base fighter sprite using portrait image + procedural body.
     * Subclasses can override drawSelf() for unique visuals.
     */
    protected void drawBase(GraphicsContext gc, Color bodyColor, Color armorColor) {
        double drawX = x;
        double drawY = y;
        double bobY = Math.sin(bobTimer) * 3;

        gc.save();
        gc.translate(drawX, drawY + bobY);
        if (facingLeft) gc.scale(-1, 1);

        // Shadow on ground
        gc.setFill(new Color(0, 0, 0, 0.3));
        gc.fillOval(-40, -5, 80, 15);

        // Legs with boots
        gc.setFill(bodyColor.darker());
        gc.fillRect(-20, -30, 15, 30);
        gc.fillRect(5, -30, 15, 30);
        // Boots
        gc.setFill(bodyColor.darker().darker());
        gc.fillRoundRect(-22, -5, 19, 10, 4, 4);
        gc.fillRoundRect(3, -5, 19, 10, 4, 4);

        // Body
        gc.setFill(bodyColor);
        gc.fillRoundRect(-30, -130, 60, 100, 10, 10);

        // Armor/clothing overlay
        gc.setFill(armorColor);
        gc.fillRoundRect(-28, -125, 56, 50, 8, 8);

        // Belt
        gc.setFill(bodyColor.brighter());
        gc.fillRect(-30, -35, 60, 5);
        gc.setFill(new Color(1, 0.85, 0.2, 0.8));
        gc.fillOval(-4, -36, 8, 7); // Belt buckle

        // Arms
        gc.setFill(bodyColor);
        // Left arm
        gc.fillRoundRect(-42, -120, 14, 50, 6, 6);
        // Right arm - extend when attacking
        if (isAttacking) {
            gc.fillRoundRect(28, -130, 14, 55, 6, 6);
        } else {
            gc.fillRoundRect(28, -120, 14, 50, 6, 6);
        }
        // Hands (skin)
        gc.setFill(Color.BISQUE);
        gc.fillOval(-42, -72, 12, 12);
        if (isAttacking) {
            gc.fillOval(30, -78, 12, 12);
        } else {
            gc.fillOval(30, -72, 12, 12);
        }

        // Head
        gc.setFill(Color.BISQUE);
        gc.fillOval(-22, -170, 44, 44);

        // Eyes
        gc.setFill(Color.web("#2c1810"));
        gc.fillOval(-10, -155, 7, 7);
        gc.fillOval(5, -155, 7, 7);
        // Eye whites
        gc.setFill(Color.WHITE);
        gc.fillOval(-9, -154, 5, 5);
        gc.fillOval(6, -154, 5, 5);
        // Pupils
        gc.setFill(Color.BLACK);
        gc.fillOval(-8, -153, 3, 3);
        gc.fillOval(7, -153, 3, 3);

        // Portrait on body (if available)
        if (portrait != null) {
            gc.drawImage(portrait, -45, -200, 90, 90);
        }

        // Hit flash overlay
        if (hitFlashTimer > 0) {
            gc.setFill(new Color(1, 1, 1, hitFlashTimer * 4));
            gc.fillRoundRect(-30, -130, 60, 100, 10, 10);
            gc.fillOval(-22, -170, 44, 44);
        }

        // Blocking shield effect
        if (isBlocking) {
            gc.setStroke(new Color(0.5, 0.8, 1.0, 0.6));
            gc.setLineWidth(3);
            gc.strokeOval(-50, -190, 100, 200);
            // Shield icon
            gc.setFill(new Color(0.5, 0.8, 1.0, 0.2));
            gc.fillOval(-50, -190, 100, 200);
        }

        gc.restore();
    }

    // --- Utility Methods ---

    public Move findMove(MoveType type) {
        for (Move m : moves) {
            if (m.getType() == type) return m;
        }
        return null;
    }

    public void jump() {
        if (onGround) {
            velocityY = GameConfig.JUMP_VELOCITY;
            onGround = false;
        }
    }

    public void dash(boolean left) {
        isDashing = true;
        dashTimer = GameConfig.DASH_DURATION;
        velocityX = left ? -GameConfig.DASH_SPEED : GameConfig.DASH_SPEED;
    }

    public void resetForRound() {
        hp = maxHp;
        spirit = maxSpirit;
        hitStunTimer = 0;
        attackTimer = 0;
        hitFlashTimer = 0;
        comboCount = 0;
        comboTimer = 0;
        isBlocking = false;
        isAttacking = false;
        isHit = false;
        isDashing = false;
        velocityX = 0;
        velocityY = 0;
        onGround = true;
        for (Move m : moves) {
            // Reset cooldowns - using reflection-free approach
        }
    }

    public void resetFull() {
        resetForRound();
        roundsWon = 0;
        level = 1;
        attackBonus = 0;
        defenseBonus = 0;
    }

    public boolean isAlive() { return hp > 0; }

    // --- Getters and Setters (Encapsulation) ---

    public String getName() { return name; }
    public String getCharacterId() { return characterId; }
    public String getTitle() { return title; }
    public int getIndex() { return index; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; this.hp = Math.min(hp, maxHp); }

    public int getSpirit() { return spirit; }
    public void setSpirit(int spirit) { this.spirit = Math.max(0, Math.min(spirit, maxSpirit)); }
    public int getMaxSpirit() { return maxSpirit; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getAttackBonus() { return attackBonus; }
    public void setAttackBonus(int attackBonus) { this.attackBonus = attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public void setDefenseBonus(int defenseBonus) { this.defenseBonus = defenseBonus; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; this.targetX = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public void setTargetX(double tx) { this.targetX = tx; }
    public double getTargetX() { return targetX; }

    public boolean isFacingLeft() { return facingLeft; }
    public void setFacingLeft(boolean facingLeft) { this.facingLeft = facingLeft; }

    public boolean isBlocking() { return isBlocking; }
    public void setBlocking(boolean blocking) { this.isBlocking = blocking; }

    public boolean isOnGround() { return onGround; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isHit() { return isHit; }
    public double getHitFlashTimer() { return hitFlashTimer; }

    public int getRoundsWon() { return roundsWon; }
    public void winRound() { roundsWon++; }

    public int getComboCount() { return comboCount; }

    public Image getPortrait() { return portrait; }
    public List<Move> getMoves() { return moves; }
}
