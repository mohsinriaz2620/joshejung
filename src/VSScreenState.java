import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class VSScreenState extends GameState {

    private double timer = 0;
    private Image p1Portrait;
    private Image p2Portrait;
    private double slideIn = 0;

    public VSScreenState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        timer = 3.0; // 3 seconds before fight
        slideIn = 0;
        
        // Instantiate the actual fighters for the match
        Fighter p1 = GameEngine.createFighter(engine.getP1CharIndex());
        p1.setFacingLeft(false);
        engine.setPlayer1(p1);

        Fighter p2;
        if (engine.getGameMode() == GameConfig.MODE_ARCADE) {
            // Arcade mode: opponent is based on level
            int level = engine.getArcadeLevel();
            int p2Index = level % 8; // Cycle through characters
            if (level % 5 == 0) p2Index = 7; // Boss every 5 levels
            engine.setP2CharIndex(p2Index);
            
            p2 = GameEngine.createFighter(p2Index);
            p2.setLevel(level);
            p2.setMaxHp(p2.getMaxHp() + (level - 1) * 15);
            
            // Set Stage
            engine.setStageIndex((level - 1) % GameConfig.STAGE_IDS.length);
        } else if (engine.getGameMode() == GameConfig.MODE_STORY) {
            // Story mode: opponent and stage already set by StoryState
            int p2Index = engine.getP2CharIndex();
            p2 = GameEngine.createFighter(p2Index);
            int chapter = engine.getCurrentStoryChapter();
            p2.setLevel(chapter * 2 + 1);
            // Stage index already set by StoryState
        } else {
            // PvP - P2 already selected
            p2 = GameEngine.createFighter(engine.getP2CharIndex());
            engine.setStageIndex((int)(Math.random() * GameConfig.STAGE_IDS.length));
        }
        
        p2.setFacingLeft(true);
        engine.setPlayer2(p2);

        // Load large portraits for VS screen
        p1Portrait = SpriteRenderer.loadCharacterPortrait(GameConfig.CHARACTER_IDS[engine.getP1CharIndex()]);
        p2Portrait = SpriteRenderer.loadCharacterPortrait(GameConfig.CHARACTER_IDS[engine.getP2CharIndex()]);

        AudioManager.playBGM("fight_bgm.wav");
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
        timer -= dt;
        if (slideIn < 1.0) {
            slideIn += dt * 2.0;
        }

        if (timer <= 0) {
            engine.switchState("Fight");
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Black background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Slide in animation
        double p1X = -400 + (slideIn * 400);
        double p2X = GameConfig.CANVAS_WIDTH - (slideIn * 400);

        // P1 Portrait
        if (p1Portrait != null) {
            gc.drawImage(p1Portrait, p1X, 100, 400, 400);
        }
        // P1 Name
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 48));
        gc.setFill(Color.CYAN);
        gc.fillText(engine.getPlayer1().getName(), p1X + 20, 550);

        // P2 Portrait (Flipped)
        if (p2Portrait != null) {
            gc.save();
            gc.translate(p2X + 400, 100);
            gc.scale(-1, 1);
            gc.drawImage(p2Portrait, 0, 0, 400, 400);
            gc.restore();
        }
        // P2 Name
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFill(Color.RED);
        gc.fillText(engine.getPlayer2().getName(), p2X + 380, 550);

        // VS Text
        if (slideIn >= 1.0) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("Impact", FontWeight.BOLD, 120));
            
            // Pulsing effect
            double scale = 1.0 + Math.sin(timer * 10) * 0.1;
            gc.save();
            gc.translate(GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT / 2);
            gc.scale(scale, scale);
            
            gc.setFill(Color.BLACK);
            gc.fillText("VS", 5, 5); // shadow
            gc.setFill(GameConfig.COLOR_GOLD);
            gc.fillText("VS", 0, 0);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(3);
            gc.strokeText("VS", 0, 0);
            
            gc.restore();
            
            // "GET READY" text
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            gc.setFill(Color.WHITE);
            gc.fillText("GET READY FOR THE NEXT BATTLE", GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 50);
        }
    }

    @Override
    public void handleKeyPressed(javafx.scene.input.KeyCode code) {
        // Skip VS screen if pressed
        if (code == javafx.scene.input.KeyCode.SPACE) {
            timer = 0;
        }
    }
}
