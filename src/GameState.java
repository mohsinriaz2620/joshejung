import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * GameState.java
 * Abstract base class for all game screens/states.
 * Implements the State design pattern for clean screen transitions.
 * Each state manages its own update, render, and input logic.
 */
public abstract class GameState {

    protected GameEngine engine;

    public GameState(GameEngine engine) {
        this.engine = engine;
    }

    /** Called when this state becomes active. */
    public abstract void enter();

    /** Called when leaving this state. */
    public abstract void exit();

    /** Update game logic. */
    public abstract void update(double dt);

    /** Render to canvas. */
    public abstract void render(GraphicsContext gc);

    /** Handle key pressed. */
    public abstract void handleKeyPressed(KeyCode code);

    /** Handle key released. */
    public void handleKeyReleased(KeyCode code) {
        // Default: do nothing. Subclasses override if needed.
    }

    /** Handle mouse pressed. */
    public void handleMousePressed(MouseButton button) {
        // Default: do nothing. Subclasses override if needed.
    }
}
