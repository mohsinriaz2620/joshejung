import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;

public class FightState extends GameState {

    private Fighter p1, p2;
    private Image background;
    private double roundTimer = GameConfig.ROUND_TIME;
    private int statePhase = 0; // 0=Intro, 1=Fight, 2=RoundEnd
    private double phaseTimer = 0;
    
    // Systems
    private ComboSystem p1Combo = new ComboSystem();
    private ComboSystem p2Combo = new ComboSystem();
    private AIController aiController;
    
    // P1 Inputs (Arrow keys)
    private boolean p1Up, p1Down, p1Left, p1Right, p1Block;
    // P2 Inputs (WASD for PvP)
    private boolean p2Up, p2Down, p2Left, p2Right, p2Block;
    
    // Chat bubble system
    private List<ChatBubble> activeBubbles = new ArrayList<>();
    private double p1TauntCooldown = 0;
    private double p2TauntCooldown = 0;
    private static final double TAUNT_COOLDOWN = 6.0; // Seconds between random taunts
    private double introBubbleTimer = 0;
    private boolean introP1Shown = false;
    private boolean introP2Shown = false;

    public FightState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        p1 = engine.getPlayer1();
        p2 = engine.getPlayer2();
        
        // Reset positions
        p1.setX(200); p1.setY(GameConfig.GROUND_Y); p1.setTargetX(200); p1.setFacingLeft(false);
        p2.setX(GameConfig.CANVAS_WIDTH - 200); p2.setY(GameConfig.GROUND_Y); p2.setTargetX(GameConfig.CANVAS_WIDTH - 200); p2.setFacingLeft(true);
        
        p1.resetForRound();
        p2.resetForRound();
        
        background = SpriteRenderer.loadStageBackground(GameConfig.STAGE_IDS[engine.getStageIndex()]);
        
        roundTimer = GameConfig.ROUND_TIME;
        statePhase = 0;
        phaseTimer = 3.0; // 3 seconds intro (more time for chat bubbles)
        
        engine.getParticles().clear();
        engine.getParticles().spawnBigText("ROUND " + (p1.getRoundsWon() + p2.getRoundsWon() + 1), GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, GameConfig.COLOR_GOLD);
        
        if (!engine.isPvP()) {
            AIController.Difficulty diff = AIController.Difficulty.MEDIUM;
            if (p2.getIndex() == 7) diff = AIController.Difficulty.BOSS; // Iblis
            aiController = new AIController(diff);
        }
        
        // Reset chat bubbles
        activeBubbles.clear();
        p1TauntCooldown = TAUNT_COOLDOWN;
        p2TauntCooldown = TAUNT_COOLDOWN;
        introBubbleTimer = 0;
        introP1Shown = false;
        introP2Shown = false;
        
        AudioManager.playSFX("round_start.wav");
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
        // Update chat bubbles (all phases)
        updateChatBubbles(dt);
        
        if (statePhase == 0) {
            // Intro phase - show character dialogue
            phaseTimer -= dt;
            introBubbleTimer += dt;
            
            // P1 intro bubble at 0.5s
            if (!introP1Shown && introBubbleTimer > 0.5) {
                introP1Shown = true;
                spawnBubble(p1, ChatBubble.getIntroLine(p1.getIndex()), false, 2.5);
            }
            // P2 response at 1.5s
            if (!introP2Shown && introBubbleTimer > 1.5) {
                introP2Shown = true;
                spawnBubble(p2, ChatBubble.getIntroResponse(p2.getIndex()), true, 2.0);
            }
            
            if (phaseTimer <= 0) {
                statePhase = 1;
                activeBubbles.clear();
                engine.getParticles().spawnBigText("FIGHT!", GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, Color.RED);
            }
        } else if (statePhase == 1) {
            // Fighting
            roundTimer -= dt;
            if (roundTimer <= 0) {
                roundTimer = 0;
                timeOut();
            }
            
            // Movement
            handleMovement(dt);
            
            // AI
            if (!engine.isPvP() && p2.isAlive() && p1.isAlive()) {
                int action = aiController.update(dt, p2, p1);
                if (action != 0) {
                    aiController.executeAction(action, p2, p1, engine.getParticles(), engine.getHud());
                    if (action >= 3 && action <= 5) p2Combo.registerHit();
                }
            }
            
            p1.update(dt);
            p2.update(dt);
            p1Combo.update(dt);
            p2Combo.update(dt);
            
            // Random taunts during fight
            p1TauntCooldown -= dt;
            p2TauntCooldown -= dt;
            if (p1TauntCooldown <= 0 && Math.random() < 0.3) {
                spawnBubble(p1, ChatBubble.getRandomTaunt(p1.getIndex()), false, 2.5);
                p1TauntCooldown = TAUNT_COOLDOWN + Math.random() * 4;
            }
            if (p2TauntCooldown <= 0 && Math.random() < 0.3) {
                spawnBubble(p2, ChatBubble.getRandomTaunt(p2.getIndex()), true, 2.5);
                p2TauntCooldown = TAUNT_COOLDOWN + Math.random() * 4;
            }
            
            // Check win condition
            if (!p1.isAlive() || !p2.isAlive()) {
                statePhase = 2;
                phaseTimer = 3.0;
                engine.getParticles().triggerSlowMo(GameConfig.SLOW_MO_DURATION);
                AudioManager.playSFX("ko.wav");
                activeBubbles.clear();
                
                if (!p1.isAlive() && !p2.isAlive()) {
                    engine.getParticles().spawnBigText("DOUBLE K.O.", GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, Color.WHITE);
                } else if (!p2.isAlive()) {
                    engine.getParticles().spawnBigText("K.O.", GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, GameConfig.COLOR_GOLD);
                    p1.winRound();
                    // Victory taunt
                    spawnBubble(p1, ChatBubble.getRandomTaunt(p1.getIndex()), false, 3.0);
                } else {
                    engine.getParticles().spawnBigText("K.O.", GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, Color.RED);
                    p2.winRound();
                    // Victory taunt
                    spawnBubble(p2, ChatBubble.getRandomTaunt(p2.getIndex()), true, 3.0);
                }
            }
        } else if (statePhase == 2) {
            // Round over
            phaseTimer -= dt;
            p1.update(dt);
            p2.update(dt);
            
            if (phaseTimer <= 0) {
                if (p1.getRoundsWon() >= GameConfig.ROUNDS_TO_WIN || p2.getRoundsWon() >= GameConfig.ROUNDS_TO_WIN) {
                    // Match over
                    engine.switchState("Victory");
                } else {
                    // Next round
                    enter(); 
                }
            }
        }
        
        // Update HUD
        engine.getHud().update(dt, p1, p2);
    }

    private void handleMovement(double dt) {
        // Face each other
        if (p1.getX() < p2.getX()) {
            p1.setFacingLeft(false);
            p2.setFacingLeft(true);
        } else {
            p1.setFacingLeft(true);
            p2.setFacingLeft(false);
        }
        
        // P1 Movement (Arrow keys)
        if (p1.isAlive() && p1.getHitFlashTimer() <= 0) {
            p1.setBlocking(p1Block);
            if (!p1Block) {
                if (p1Left) p1.setTargetX(p1.getX() - GameConfig.MOVE_SPEED * dt);
                if (p1Right) p1.setTargetX(p1.getX() + GameConfig.MOVE_SPEED * dt);
                if (p1Up) p1.jump();
            }
        }
        
        // P2 Movement (WASD for PvP)
        if (engine.isPvP() && p2.isAlive() && p2.getHitFlashTimer() <= 0) {
            p2.setBlocking(p2Block);
            if (!p2Block) {
                if (p2Left) p2.setTargetX(p2.getX() - GameConfig.MOVE_SPEED * dt);
                if (p2Right) p2.setTargetX(p2.getX() + GameConfig.MOVE_SPEED * dt);
                if (p2Up) p2.jump();
            }
        }
    }

    private void timeOut() {
        statePhase = 2;
        phaseTimer = 3.0;
        engine.getParticles().spawnBigText("TIME UP", GameConfig.CANVAS_WIDTH/2, GameConfig.CANVAS_HEIGHT/2, Color.WHITE);
        if (p1.getHp() > p2.getHp()) {
            p1.winRound();
            p2.setHp(0);
        } else if (p2.getHp() > p1.getHp()) {
            p2.winRound();
            p1.setHp(0);
        } else {
            // Draw
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw background
        SpriteRenderer.drawBackground(gc, background);
        
        // Draw fighters
        if (p1.isAlive() || p1.getHp() == 0) p1.drawSelf(gc); // Always draw P1
        if (p2.isAlive() || p2.getHp() == 0) p2.drawSelf(gc); // Always draw P2
        
        // Draw chat bubbles above fighters
        renderChatBubbles(gc);
        
        // Draw HUD
        engine.getHud().render(gc, p1, p2, (int)Math.ceil(roundTimer), p1.getRoundsWon(), p2.getRoundsWon());
        
        // Draw control hints at bottom
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        gc.setFill(new Color(1, 1, 1, 0.5));
        gc.fillText("ARROWS: Move | LEFT CLICK: Attack | RIGHT CLICK: Special | SHIFT: Block | H: Heal", 
            GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 10);
    }

    // --- Chat Bubble helpers ---
    private void spawnBubble(Fighter fighter, String text, boolean isLeft, double lifetime) {
        double bx = fighter.getX();
        double by = fighter.getY() - 200; // Above the fighter head
        Color border = isLeft ? fighter.getAuraColor() : fighter.getPrimaryColor();
        activeBubbles.add(new ChatBubble(text, bx, by, isLeft, border, lifetime));
    }
    
    private void updateChatBubbles(double dt) {
        activeBubbles.removeIf(b -> !b.update(dt));
        // Update bubble positions to follow fighters
        // (We track by creation order: even index = P1 perspective, but simpler to just
        //  let them stay at spawn position for clean rendering)
    }
    
    private void renderChatBubbles(GraphicsContext gc) {
        for (ChatBubble bubble : activeBubbles) {
            bubble.render(gc);
        }
    }

    // --- P1 attack helper ---
    private void p1Attack(MoveType type) {
        if (statePhase != 1 || !p1.isAlive() || p1Block) return;
        p1.performAttack(p2, engine.getParticles(), engine.getHud(), type);
        p1Combo.registerInput(KeyCode.A); // generic
        if (p1.isAttacking() && p2.isHit()) {
            p1Combo.registerHit();
            if (p1Combo.getHitCount() > 1) engine.getHud().triggerComboScale();
            // Chance for attack taunt
            if (Math.random() < 0.15 && p1TauntCooldown <= 0) {
                spawnBubble(p1, ChatBubble.getRandomTaunt(p1.getIndex()), false, 2.0);
                p1TauntCooldown = TAUNT_COOLDOWN;
            }
        }
    }

    private void p1Special() {
        if (statePhase != 1 || !p1.isAlive() || p1Block) return;
        p1.performSpecial(p2, engine.getParticles(), engine.getHud());
        p1Combo.registerInput(KeyCode.S);
        if (p1.isAttacking() && p2.isHit()) {
            p1Combo.registerHit();
            if (p1Combo.getHitCount() > 1) engine.getHud().triggerComboScale();
        }
        // Always taunt on special
        if (p1TauntCooldown <= 2) {
            spawnBubble(p1, ChatBubble.getRandomTaunt(p1.getIndex()), false, 2.5);
            p1TauntCooldown = TAUNT_COOLDOWN;
        }
    }

    @Override
    public void handleMousePressed(MouseButton button) {
        if (statePhase != 1) return;
        
        if (button == MouseButton.PRIMARY) {
            // Left click = Light Punch attack
            p1Attack(MoveType.LIGHT_PUNCH);
        } else if (button == MouseButton.SECONDARY) {
            // Right click = Special attack
            p1Special();
        }
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (statePhase != 1) return; // Only accept inputs during fight
        
        // P1 Controls (Arrow keys + keyboard attacks)
        if (p1.isAlive()) {
            if (code == KeyCode.LEFT) p1Left = true;
            if (code == KeyCode.RIGHT) p1Right = true;
            if (code == KeyCode.UP) p1Up = true;
            if (code == KeyCode.DOWN) p1Down = true;
            if (code == KeyCode.SHIFT) p1Block = true;
            
            // Keyboard attack alternatives
            if (!p1Block) {
                if (code == KeyCode.Z) p1Attack(MoveType.LIGHT_PUNCH);
                else if (code == KeyCode.X) p1Attack(MoveType.HEAVY_PUNCH);
                else if (code == KeyCode.C) p1Attack(MoveType.LIGHT_KICK);
                else if (code == KeyCode.V) p1Attack(MoveType.HEAVY_KICK);
                else if (code == KeyCode.SPACE) p1Special();
                else if (code == KeyCode.H) { p1.performAttack(p2, engine.getParticles(), engine.getHud(), MoveType.HEAL); p1Combo.registerInput(code); }
            }
        }
        
        // P2 Controls (WASD + QER) for PvP
        if (engine.isPvP() && p2.isAlive()) {
            if (code == KeyCode.A) p2Left = true;
            if (code == KeyCode.D) p2Right = true;
            if (code == KeyCode.W) p2Up = true;
            if (code == KeyCode.S) p2Down = true;
            if (code == KeyCode.F) p2Block = true;
            
            // Attacks
            if (!p2Block) {
                if (code == KeyCode.Q) { p2.performAttack(p1, engine.getParticles(), engine.getHud(), MoveType.LIGHT_PUNCH); p2Combo.registerInput(code); }
                else if (code == KeyCode.E) { p2.performAttack(p1, engine.getParticles(), engine.getHud(), MoveType.HEAVY_PUNCH); p2Combo.registerInput(code); }
                else if (code == KeyCode.R) { p2.performSpecial(p1, engine.getParticles(), engine.getHud()); p2Combo.registerInput(code); }
                
                if (code == KeyCode.Q || code == KeyCode.E || code == KeyCode.R) {
                    if (p2.isAttacking() && p1.isHit()) {
                        p2Combo.registerHit();
                        if (p2Combo.getHitCount() > 1) engine.getHud().triggerComboScale();
                    }
                }
            }
        }
    }

    @Override
    public void handleKeyReleased(KeyCode code) {
        // P1 (Arrow keys)
        if (code == KeyCode.LEFT) p1Left = false;
        if (code == KeyCode.RIGHT) p1Right = false;
        if (code == KeyCode.UP) p1Up = false;
        if (code == KeyCode.DOWN) p1Down = false;
        if (code == KeyCode.SHIFT) p1Block = false;
        
        // P2 (WASD)
        if (code == KeyCode.A) p2Left = false;
        if (code == KeyCode.D) p2Right = false;
        if (code == KeyCode.W) p2Up = false;
        if (code == KeyCode.S) p2Down = false;
        if (code == KeyCode.F) p2Block = false;
    }
}
