import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class CharSelectState extends GameState {

    private int p1Cursor = 0;
    private int p2Cursor = 7;
    private boolean p1Ready = false;
    private boolean p2Ready = false;
    private double flashTimer = 0;

    public CharSelectState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        p1Ready = false;
        p2Ready = false;
        
        // If arcade or story mode, P2 is AI, so mark it ready immediately
        if (engine.getGameMode() == GameConfig.MODE_ARCADE || 
            engine.getGameMode() == GameConfig.MODE_STORY) {
            p2Cursor = -1; // Hidden cursor
            p2Ready = true;
        }
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
        if (flashTimer > 0) {
            flashTimer -= dt;
        }

        if (p1Ready && p2Ready && flashTimer <= 0) {
            engine.setP1CharIndex(p1Cursor);
            if (engine.isPvP()) {
                engine.setP2CharIndex(p2Cursor);
            }
            engine.switchState("VSScreen");
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Background
        gc.setFill(GameConfig.COLOR_DARK_BG);
        gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Title
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 48));
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText("SELECT YOUR FIGHTER", GameConfig.CANVAS_WIDTH / 2, 60);

        // Draw Character Grid (2 rows of 4)
        double startX = GameConfig.CANVAS_WIDTH / 2 - 250;
        double startY = 150;
        double portraitSize = 100;
        double padding = 20;

        for (int i = 0; i < GameConfig.CHARACTER_IDS.length; i++) {
            int row = i / 4;
            int col = i % 4;
            double px = startX + col * (portraitSize + padding);
            double py = startY + row * (portraitSize + padding);

            // Portrait
            SpriteRenderer.drawPortrait(gc, 
                SpriteRenderer.loadCharacterPortrait(GameConfig.CHARACTER_IDS[i]), 
                px, py, portraitSize, portraitSize, false);

            // Cursors
            if (i == p1Cursor) {
                gc.setStroke(Color.CYAN);
                gc.setLineWidth(4);
                gc.strokeRect(px - 4, py - 4, portraitSize + 8, portraitSize + 8);
                gc.setFill(Color.CYAN);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
                gc.fillText("1P", px + portraitSize / 2, py - 10);
            }

            if (engine.isPvP() && p2Cursor >= 0 && i == p2Cursor) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(4);
                gc.strokeRect(px - 2, py - 2, portraitSize + 4, portraitSize + 4);
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
                gc.fillText("2P", px + portraitSize / 2, py + portraitSize + 20);
            }
        }

        // Selected Character Details
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        
        // P1 Details
        gc.setFill(Color.CYAN);
        gc.fillText(GameConfig.CHARACTER_NAMES[p1Cursor], 50, 450);
        gc.setFont(Font.font("Verdana", 16));
        gc.setFill(Color.WHITE);
        gc.fillText(GameConfig.CHARACTER_TITLES[p1Cursor], 50, 480);
        if (p1Ready) {
            gc.setFill(Color.CYAN);
            gc.fillText("READY!", 50, 510);
        }

        // P2 Details (only if PvP)
        if (engine.isPvP() && p2Cursor >= 0 && p2Cursor < GameConfig.CHARACTER_IDS.length) {
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            gc.setFill(Color.RED);
            gc.fillText(GameConfig.CHARACTER_NAMES[p2Cursor], GameConfig.CANVAS_WIDTH - 50, 450);
            gc.setFont(Font.font("Verdana", 16));
            gc.setFill(Color.WHITE);
            gc.fillText(GameConfig.CHARACTER_TITLES[p2Cursor], GameConfig.CANVAS_WIDTH - 50, 480);
            if (p2Ready) {
                gc.setFill(Color.RED);
                gc.fillText("READY!", GameConfig.CANVAS_WIDTH - 50, 510);
            }
        }

        // Flash overlay when both ready
        if (p1Ready && p2Ready && flashTimer > 0) {
            gc.setFill(new Color(1, 1, 1, flashTimer));
            gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        }
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (p1Ready && p2Ready) return;

        // P1 Controls (Arrow keys)
        if (!p1Ready) {
            if (code == KeyCode.UP) moveCursor(true, -4);
            if (code == KeyCode.DOWN) moveCursor(true, 4);
            if (code == KeyCode.LEFT) moveCursor(true, -1);
            if (code == KeyCode.RIGHT) moveCursor(true, 1);
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                p1Ready = true;
                AudioManager.playSFX("hit_heavy.wav");
                checkBothReady();
            }
        }

        // P2 Controls (WASD, only if PvP)
        if (engine.isPvP() && !p2Ready) {
            if (code == KeyCode.W) moveCursor(false, -4);
            if (code == KeyCode.S) moveCursor(false, 4);
            if (code == KeyCode.A) moveCursor(false, -1);
            if (code == KeyCode.D) moveCursor(false, 1);
            if (code == KeyCode.Q) {
                p2Ready = true;
                AudioManager.playSFX("hit_heavy.wav");
                checkBothReady();
            }
        }
    }

    private void moveCursor(boolean isP1, int delta) {
        int cursor = isP1 ? p1Cursor : p2Cursor;
        cursor += delta;
        if (cursor < 0) cursor += 8;
        if (cursor > 7) cursor -= 8;
        
        if (isP1) p1Cursor = cursor;
        else p2Cursor = cursor;
        
        AudioManager.playSFX("hit_light.wav");
    }

    private void checkBothReady() {
        if (p1Ready && p2Ready) {
            flashTimer = 1.0; // 1 second flash before transition
            AudioManager.playSFX("special.wav");
        }
    }
}
