import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class MenuState extends GameState {

    private String[] options = {"ARCADE MODE", "STORY MODE", "VS MODE"};
    private int selectedIndex = 0;
    private Image background;
    private double titleScale = 1.0;
    private boolean titleScalingUp = true;

    public MenuState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        background = SpriteRenderer.loadImage(GameConfig.UI_DIR + "title_background.png");
        engine.getParticles().clear();
        // Spawn some embers
        for (int i = 0; i < 50; i++) {
            engine.getParticles().spawnSparks(
                Math.random() * GameConfig.CANVAS_WIDTH,
                GameConfig.CANVAS_HEIGHT,
                Color.ORANGE, 1
            );
        }
        AudioManager.playBGM("menu_bgm.wav");
    }

    @Override
    public void exit() {
        engine.getParticles().clear();
    }

    @Override
    public void update(double dt) {

        // Animate title
        if (titleScalingUp) {
            titleScale += dt * 0.1;
            if (titleScale > 1.05) titleScalingUp = false;
        } else {
            titleScale -= dt * 0.1;
            if (titleScale < 0.95) titleScalingUp = true;
        }

        // Randomly spawn more embers
        if (Math.random() < 0.1) {
            engine.getParticles().spawnSparks(
                Math.random() * GameConfig.CANVAS_WIDTH,
                GameConfig.CANVAS_HEIGHT,
                Color.GOLD, 2
            );
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw Background
        SpriteRenderer.drawBackground(gc, background);

        // Draw Title
        gc.save();
        gc.translate(GameConfig.CANVAS_WIDTH / 2, 150);
        gc.scale(titleScale, titleScale);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 72));
        
        // Shadow
        gc.setFill(new Color(0, 0, 0, 0.8));
        gc.fillText(GameConfig.GAME_TITLE, 5, 5);
        
        // Main Text
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText(GameConfig.GAME_TITLE, 0, 0);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeText(GameConfig.GAME_TITLE, 0, 0);
        gc.restore();

        // Draw Options
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        for (int i = 0; i < options.length; i++) {
            double y = 350 + i * 70;
            if (i == selectedIndex) {
                gc.setFill(Color.WHITE);
                gc.fillText("> " + options[i] + " <", GameConfig.CANVAS_WIDTH / 2, y);
            } else {
                gc.setFill(Color.GRAY);
                gc.fillText(options[i], GameConfig.CANVAS_WIDTH / 2, y);
            }
        }

        // Instructions
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        gc.setFill(Color.WHITE);
        gc.fillText("Use UP/DOWN arrows to navigate. Press ENTER to select.", 
            GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 30);
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.UP) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = options.length - 1;
            AudioManager.playSFX("hit_light.wav");
        } else if (code == KeyCode.DOWN) {
            selectedIndex++;
            if (selectedIndex >= options.length) selectedIndex = 0;
            AudioManager.playSFX("hit_light.wav");
        } else if (code == KeyCode.ENTER) {
            AudioManager.playSFX("special.wav");
            engine.setGameMode(selectedIndex);
            engine.switchState("CharSelect");
        }
    }
}
