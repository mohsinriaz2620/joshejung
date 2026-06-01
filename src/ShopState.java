import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ShopState extends GameState {

    private String[] items = {
        "HEALING POTION (+50 HP) - 30 Gold",
        "SPIRIT ELIXIR (+50 SP) - 30 Gold",
        "SWORD SHARPENER (+5 ATK) - 100 Gold",
        "ARMOR PLATING (+5 DEF) - 100 Gold",
        "CONTINUE TO NEXT BATTLE"
    };
    
    private int[] costs = {30, 30, 100, 100, 0};
    private int selectedIndex = 0;

    public ShopState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        AudioManager.playBGM("menu_bgm.wav");
        engine.getParticles().clear();
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void render(GraphicsContext gc) {
        // Background
        gc.setFill(Color.web("#1a0f00"));
        gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Header
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 56));
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText("THE BAZAAR", GameConfig.CANVAS_WIDTH / 2, 80);

        // Current Gold
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        gc.setFill(Color.YELLOW);
        gc.fillText("YOUR GOLD: " + engine.getGold() + " Asharfi", GameConfig.CANVAS_WIDTH / 2, 130);

        // Current Stats
        Fighter p1 = engine.getPlayer1();
        gc.setFont(Font.font("Verdana", 18));
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("HP: %d/%d | SP: %d/%d | ATK BONUS: +%d | DEF BONUS: +%d", 
            p1.getHp(), p1.getMaxHp(), p1.getSpirit(), p1.getMaxSpirit(), p1.getAttackBonus(), p1.getDefenseBonus()), 
            GameConfig.CANVAS_WIDTH / 2, 170);

        // Items list
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        for (int i = 0; i < items.length; i++) {
            double y = 280 + i * 60;
            if (i == selectedIndex) {
                gc.setFill(Color.CYAN);
                gc.fillRect(GameConfig.CANVAS_WIDTH / 2 - 300, y - 30, 600, 40);
                gc.setFill(Color.BLACK);
                gc.fillText("> " + items[i] + " <", GameConfig.CANVAS_WIDTH / 2, y);
            } else {
                gc.setFill(costs[i] <= engine.getGold() ? Color.WHITE : Color.GRAY);
                gc.fillText(items[i], GameConfig.CANVAS_WIDTH / 2, y);
            }
        }

        // Instructions
        gc.setFont(Font.font("Verdana", 16));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("UP/DOWN to select. ENTER to purchase.", GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 30);
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.UP) {
            selectedIndex--;
            if (selectedIndex < 0) selectedIndex = items.length - 1;
            AudioManager.playSFX("hit_light.wav");
        } else if (code == KeyCode.DOWN) {
            selectedIndex++;
            if (selectedIndex >= items.length) selectedIndex = 0;
            AudioManager.playSFX("hit_light.wav");
        } else if (code == KeyCode.ENTER) {
            if (selectedIndex == items.length - 1) {
                // Continue
                AudioManager.playSFX("special.wav");
                engine.switchState("VSScreen");
            } else {
                // Try purchase
                if (engine.getGold() >= costs[selectedIndex]) {
                    engine.addGold(-costs[selectedIndex]);
                    AudioManager.playSFX("heal.wav");
                    Fighter p1 = engine.getPlayer1();
                    
                    switch (selectedIndex) {
                        case 0: p1.heal(50, engine.getParticles()); break;
                        case 1: p1.setSpirit(p1.getSpirit() + 50); break;
                        case 2: p1.setAttackBonus(p1.getAttackBonus() + 5); break;
                        case 3: p1.setDefenseBonus(p1.getDefenseBonus() + 5); break;
                    }
                } else {
                    AudioManager.playSFX("block.wav"); // error sound
                }
            }
        }
    }
}
