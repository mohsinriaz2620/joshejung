import java.util.Random;

/**
 * AIController.java
 * Enemy AI system with multiple difficulty tiers.
 * AI reads player position and adapts behavior accordingly.
 * Features aggressive, defensive, and reactive patterns.
 */
public class AIController {
    public enum Difficulty { EASY, MEDIUM, HARD, BOSS }

    private Difficulty difficulty;
    private Random random = new Random();
    private double actionTimer;
    private double reactionDelay;
    private double aggressiveness;
    private boolean wantsToBlock;

    public AIController(Difficulty difficulty) {
        this.difficulty = difficulty;
        switch (difficulty) {
            case EASY:   reactionDelay = 1.2; aggressiveness = 0.3; break;
            case MEDIUM: reactionDelay = 0.7; aggressiveness = 0.5; break;
            case HARD:   reactionDelay = 0.35; aggressiveness = 0.7; break;
            case BOSS:   reactionDelay = 0.2; aggressiveness = 0.85; break;
        }
        actionTimer = reactionDelay;
    }

    /**
     * Updates AI and returns an action to perform.
     * Returns: 0=idle, 1=moveForward, 2=moveBack, 3=lightAttack,
     *          4=heavyAttack, 5=special, 6=block, 7=jump, 8=heal
     */
    public int update(double dt, Fighter self, Fighter opponent) {
        actionTimer -= dt;
        if (actionTimer > 0) return 0;

        actionTimer = reactionDelay + random.nextDouble() * 0.3;
        double dist = Math.abs(self.getX() - opponent.getX());
        double hpRatio = (double) self.getHp() / self.getMaxHp();
        double oppHpRatio = (double) opponent.getHp() / opponent.getMaxHp();
        double roll = random.nextDouble();

        // Low HP - defensive
        if (hpRatio < 0.25 && self.getSpirit() >= 30) {
            if (roll < 0.3) return 8; // heal
            if (roll < 0.5) return 6; // block
        }

        // In attack range
        if (dist < GameConfig.ATTACK_RANGE) {
            if (roll < aggressiveness * 0.4) return 3; // light
            if (roll < aggressiveness * 0.6) return 4; // heavy
            if (roll < aggressiveness * 0.75 && self.getSpirit() >= 30) return 5; // special
            if (roll < aggressiveness * 0.85) return 6; // block
            return 2; // back off
        }

        // Mid range - close the gap
        if (dist < GameConfig.ATTACK_RANGE * 2.5) {
            if (roll < aggressiveness) return 1; // advance
            if (roll < 0.9) return 1;
            return 7; // jump
        }

        // Far away - always advance
        return 1;
    }

    /**
     * Execute the AI action on the fighter.
     */
    public void executeAction(int action, Fighter self, Fighter target,
                               ParticleSystem particles, HUD hud) {
        boolean faceLeft = target.getX() < self.getX();
        self.setFacingLeft(faceLeft);

        switch (action) {
            case 1: // Move forward
                double dir = faceLeft ? -1 : 1;
                self.setTargetX(self.getX() + dir * 40);
                break;
            case 2: // Move back
                dir = faceLeft ? 1 : -1;
                self.setTargetX(self.getX() + dir * 40);
                break;
            case 3: // Light attack
                if (Math.abs(self.getX()-target.getX()) < GameConfig.ATTACK_RANGE) {
                    self.performAttack(target, particles, hud, MoveType.LIGHT_PUNCH);
                }
                break;
            case 4: // Heavy attack
                if (Math.abs(self.getX()-target.getX()) < GameConfig.ATTACK_RANGE) {
                    self.performAttack(target, particles, hud, MoveType.HEAVY_PUNCH);
                }
                break;
            case 5: // Special
                if (Math.abs(self.getX()-target.getX()) < GameConfig.ATTACK_RANGE * 1.5) {
                    self.performSpecial(target, particles, hud);
                }
                break;
            case 6: // Block
                self.setBlocking(true);
                break;
            case 7: // Jump
                self.jump();
                break;
            case 8: // Heal
                self.heal(20, particles);
                self.setSpirit(self.getSpirit() - 25);
                break;
            default:
                self.setBlocking(false);
                break;
        }
    }

    public void setDifficulty(Difficulty d) {
        this.difficulty = d;
        switch (d) {
            case EASY:   reactionDelay = 1.2; aggressiveness = 0.3; break;
            case MEDIUM: reactionDelay = 0.7; aggressiveness = 0.5; break;
            case HARD:   reactionDelay = 0.35; aggressiveness = 0.7; break;
            case BOSS:   reactionDelay = 0.2; aggressiveness = 0.85; break;
        }
    }

    public Difficulty getDifficulty() { return difficulty; }
}
