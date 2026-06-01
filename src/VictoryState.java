import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class VictoryState extends GameState {

    private Fighter winner;
    private Fighter loser;
    private double timer = 0;
    private Image winnerPortrait;
    private int rewardGold = 0;

    public VictoryState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        if (engine.getPlayer1().getRoundsWon() > engine.getPlayer2().getRoundsWon()) {
            winner = engine.getPlayer1();
            loser = engine.getPlayer2();
        } else {
            winner = engine.getPlayer2();
            loser = engine.getPlayer1();
        }

        winnerPortrait = SpriteRenderer.loadCharacterPortrait(winner.getCharacterId());
        timer = 0;
        
        // Calculate reward based on game mode
        if (!engine.isPvP() && winner == engine.getPlayer1()) {
            rewardGold = 50 + (engine.getPlayer2().getLevel() * 10);
            engine.addGold(rewardGold);
        } else {
            rewardGold = 0;
        }

        AudioManager.playBGM("victory.wav");
        
        // Fireworks!
        for (int i = 0; i < 20; i++) {
            engine.getParticles().spawnSparks(
                Math.random() * GameConfig.CANVAS_WIDTH, 
                Math.random() * GameConfig.CANVAS_HEIGHT, 
                winner.getAuraColor(), 10
            );
        }
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
        timer += dt;
        
        if (Math.random() < 0.05) {
            engine.getParticles().spawnSparks(
                Math.random() * GameConfig.CANVAS_WIDTH, 
                Math.random() * GameConfig.CANVAS_HEIGHT, 
                GameConfig.COLOR_GOLD, 5
            );
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Background
        gc.setFill(GameConfig.COLOR_DARK_BG);
        gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Portrait
        if (winnerPortrait != null) {
            gc.save();
            gc.setGlobalAlpha(Math.min(1.0, timer));
            gc.drawImage(winnerPortrait, GameConfig.CANVAS_WIDTH / 2 - 200, 150, 400, 400);
            gc.restore();
        }

        // Title text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 72));
        
        // Slide in from top
        double titleY = Math.min(100, timer * 200 - 100);
        
        gc.setFill(Color.BLACK);
        gc.fillText(winner.getName() + " WINS!", GameConfig.CANVAS_WIDTH / 2 + 5, titleY + 5);
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText(winner.getName() + " WINS!", GameConfig.CANVAS_WIDTH / 2, titleY);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeText(winner.getName() + " WINS!", GameConfig.CANVAS_WIDTH / 2, titleY);

        if (timer > 2.0) {
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
            gc.setFill(Color.WHITE);
            gc.fillText("FLAWLESS VICTORY", GameConfig.CANVAS_WIDTH / 2, 580);
            
            if (rewardGold > 0) {
                gc.setFill(Color.YELLOW);
                gc.fillText("REWARD: " + rewardGold + " ASHARFI (GOLD)", GameConfig.CANVAS_WIDTH / 2, 620);
            }
            
            gc.setFont(Font.font("Verdana", 18));
            gc.setFill(Color.LIGHTGRAY);
            gc.fillText("Press SPACE to continue", GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 40);
        }
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (timer > 2.0 && code == KeyCode.SPACE) {
            AudioManager.playSFX("hit_light.wav");
            
            if (engine.getGameMode() == GameConfig.MODE_ARCADE && winner == engine.getPlayer1()) {
                engine.setArcadeLevel(engine.getArcadeLevel() + 1);
                engine.switchState("Shop"); // Go to shop between arcade matches
            } else if (engine.getGameMode() == GameConfig.MODE_STORY && winner == engine.getPlayer1()) {
                engine.setCurrentStoryChapter(engine.getCurrentStoryChapter() + 1);
                engine.switchState("Story");
            } else {
                // PvP or player lost -> go back to menu
                engine.switchState("Menu");
            }
        }
    }
}
