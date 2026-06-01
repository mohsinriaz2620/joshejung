import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import java.util.HashMap;
import java.util.Map;

/**
 * GameEngine.java
 * Central game controller managing state machine, game data, and transitions.
 * Holds shared data between states (selected fighters, game mode, gold, etc.)
 */
public class GameEngine {

    // State machine
    private Map<String, GameState> states = new HashMap<>();
    private GameState currentState;
    private String currentStateName = "";

    // Canvas reference
    private Canvas canvas;
    private GraphicsContext gc;

    // Shared game data
    private int gameMode = GameConfig.MODE_ARCADE; // 0=Arcade, 1=Story, 2=PvP
    private int p1CharIndex = 0;
    private int p2CharIndex = 3;
    private Fighter player1;
    private Fighter player2;
    private int gold = 0;
    private int currentStoryChapter = 0;
    private int arcadeLevel = 1;
    private int stageIndex = 0;
    private boolean isPvP = false;

    // Particle system (shared)
    private ParticleSystem particles = new ParticleSystem();
    private HUD hud = new HUD();

    public GameEngine(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    /** Register a state with a name. */
    public void addState(String name, GameState state) {
        states.put(name, state);
    }

    /** Switch to a different state. */
    public void switchState(String name) {
        System.out.println("[GameEngine] Switching state to: " + name);
        if (currentState != null) currentState.exit();
        currentState = states.get(name);
        currentStateName = name;
        if (currentState != null) currentState.enter();
        System.out.println("[GameEngine] Switched state to: " + name);
    }

    /** Update current state. */
    public void update(double dt) {
        if (currentState != null) {
            double effectiveDt = dt * particles.getSlowMoFactor();
            currentState.update(effectiveDt);
            particles.update(effectiveDt);
        }
    }

    /** Render current state. */
    public void render() {
        if (currentState != null) {
            gc.save();
            gc.translate(particles.getShakeX(), particles.getShakeY());
            currentState.render(gc);
            particles.render(gc);
            gc.restore();
        }
    }

    /** Route key events. */
    public void handleKeyPressed(KeyCode code) {
        if (currentState != null) currentState.handleKeyPressed(code);
    }

    public void handleKeyReleased(KeyCode code) {
        if (currentState != null) currentState.handleKeyReleased(code);
    }

    public void handleMousePressed(MouseButton button) {
        if (currentState != null) currentState.handleMousePressed(button);
    }

    /** Create a fighter instance by index. */
    public static Fighter createFighter(int index) {
        switch (index) {
            case 0: return new SufiWarrior();
            case 1: return new NoorJahan();
            case 2: return new PeerSahib();
            case 3: return new MaridFighter();
            case 4: return new ChuralFighter();
            case 5: return new DevFighter();
            case 6: return new JinnFighter();
            case 7: return new IblisFighter();
            default: return new SufiWarrior();
        }
    }

    // --- Getters/Setters for shared state ---
    public Canvas getCanvas() { return canvas; }
    public GraphicsContext getGc() { return gc; }
    public ParticleSystem getParticles() { return particles; }
    public HUD getHud() { return hud; }

    public int getGameMode() { return gameMode; }
    public void setGameMode(int mode) { this.gameMode = mode; this.isPvP = (mode == GameConfig.MODE_PVP); }

    public int getP1CharIndex() { return p1CharIndex; }
    public void setP1CharIndex(int i) { this.p1CharIndex = i; }
    public int getP2CharIndex() { return p2CharIndex; }
    public void setP2CharIndex(int i) { this.p2CharIndex = i; }

    public Fighter getPlayer1() { return player1; }
    public void setPlayer1(Fighter f) { this.player1 = f; }
    public Fighter getPlayer2() { return player2; }
    public void setPlayer2(Fighter f) { this.player2 = f; }

    public int getGold() { return gold; }
    public void setGold(int g) { this.gold = g; }
    public void addGold(int amount) { this.gold += amount; }

    public int getCurrentStoryChapter() { return currentStoryChapter; }
    public void setCurrentStoryChapter(int c) { this.currentStoryChapter = c; }

    public int getArcadeLevel() { return arcadeLevel; }
    public void setArcadeLevel(int l) { this.arcadeLevel = l; }

    public int getStageIndex() { return stageIndex; }
    public void setStageIndex(int s) { this.stageIndex = s; }

    public boolean isPvP() { return isPvP; }
    public String getCurrentStateName() { return currentStateName; }
}
