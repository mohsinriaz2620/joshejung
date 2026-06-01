import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * HUD.java - Tekken-style Heads-Up Display rendered on canvas.
 * Slanted health bars, spirit meters, round indicators, timer, combo counter.
 */
public class HUD {
    private double p1HpD = 1.0, p2HpD = 1.0, p1SpD = 1.0, p2SpD = 1.0;
    private double comboScale = 1.0;

    public void update(double dt, Fighter p1, Fighter p2) {
        p1HpD += ((double)p1.getHp()/p1.getMaxHp() - p1HpD) * 0.08;
        p2HpD += ((double)p2.getHp()/p2.getMaxHp() - p2HpD) * 0.08;
        p1SpD += ((double)p1.getSpirit()/p1.getMaxSpirit() - p1SpD) * 0.1;
        p2SpD += ((double)p2.getSpirit()/p2.getMaxSpirit() - p2SpD) * 0.1;
        if (comboScale > 1.0) comboScale -= dt * 3;
        if (comboScale < 1.0) comboScale = 1.0;
    }

    public void render(GraphicsContext gc, Fighter p1, Fighter p2, int timer, int p1R, int p2R) {
        double W = GameConfig.CANVAS_WIDTH;
        double barW = GameConfig.HP_BAR_WIDTH, barH = GameConfig.HP_BAR_HEIGHT;

        // Top bar bg
        gc.setFill(new Color(0,0,0,0.7));
        gc.fillRect(0,0,W,90);

        // HP bars
        drawHpBar(gc, 30, 18, barW, barH, p1HpD, false);
        drawHpBar(gc, W-30-barW, 18, barW, barH, p2HpD, true);

        // Spirit bars
        drawSpiritBar(gc, 30, 55, 200, 12, p1SpD, p1.getSpirit());
        drawSpiritBar(gc, W-230, 55, 200, 12, p2SpD, p2.getSpirit());

        // Timer
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 56));
        gc.setFill(new Color(0,0,0,0.8));
        gc.fillOval(W/2-38, 5, 76, 70);
        gc.setStroke(GameConfig.COLOR_GOLD); gc.setLineWidth(2);
        gc.strokeOval(W/2-38, 5, 76, 70);
        gc.setFill(timer <= 10 ? Color.RED : GameConfig.COLOR_GOLD);
        gc.fillText(String.valueOf(timer), W/2, 58);

        // Round dots
        drawRounds(gc, W/2-90, 80, p1R);
        drawRounds(gc, W/2+50, 80, p2R);

        // Names
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Color.WHITE);
        gc.fillText(p1.getName(), 32, 13);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(p2.getName(), W-32, 13);

        // Combos
        if(p1.getComboCount()>1) drawCombo(gc, 50, 150, p1.getComboCount());
        if(p2.getComboCount()>1) drawCombo(gc, W-170, 150, p2.getComboCount());
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawHpBar(GraphicsContext gc, double x, double y, double w, double h, double fill, boolean mirror) {
        gc.setFill(new Color(0.2,0.15,0.05,0.8));
        gc.fillRect(x-2,y-2,w+4,h+4);
        gc.setStroke(GameConfig.COLOR_GOLD); gc.setLineWidth(2);
        gc.strokeRect(x-2,y-2,w+4,h+4);
        gc.setFill(Color.web("#2c0000"));
        gc.fillRect(x,y,w,h);
        Color hc = fill>0.5?GameConfig.COLOR_HP_GREEN:fill>0.25?GameConfig.COLOR_HP_YELLOW:GameConfig.COLOR_HP_RED;
        gc.setFill(new LinearGradient(0,0,0,1,true,CycleMethod.NO_CYCLE,
            new Stop(0,hc.brighter()),new Stop(0.5,hc),new Stop(1,hc.darker())));
        double fw = Math.max(0, w*fill);
        if(mirror) gc.fillRect(x+w-fw,y,fw,h); else gc.fillRect(x,y,fw,h);
        gc.setFill(new Color(1,1,1,0.15));
        if(mirror) gc.fillRect(x+w-fw,y,fw,h/3); else gc.fillRect(x,y,fw,h/3);
    }

    private void drawSpiritBar(GraphicsContext gc, double x, double y, double w, double h, double fill, int val) {
        gc.setFill(new Color(0,0,0.15,0.7));
        gc.fillRect(x,y,w,h);
        gc.setFill(new LinearGradient(0,0,1,0,true,CycleMethod.NO_CYCLE,
            new Stop(0,GameConfig.COLOR_SPIRIT_BLUE),new Stop(1,Color.web("#1abc9c"))));
        gc.fillRect(x,y,w*fill,h);
        gc.setStroke(new Color(0.2,0.6,0.9,0.6)); gc.setLineWidth(1);
        gc.strokeRect(x,y,w,h);
        gc.setFont(Font.font("Verdana",FontWeight.BOLD,9));
        gc.setFill(Color.WHITE); gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("SP:"+val, x+4, y+h-2);
    }

    private void drawRounds(GraphicsContext gc, double x, double y, int won) {
        for(int i=0;i<GameConfig.ROUNDS_TO_WIN;i++){
            gc.setFill(i<won?GameConfig.COLOR_GOLD:new Color(0.3,0.3,0.3,0.6));
            gc.fillOval(x+i*22,y,14,14);
            gc.setStroke(GameConfig.COLOR_GOLD.darker()); gc.setLineWidth(1);
            gc.strokeOval(x+i*22,y,14,14);
        }
    }

    private void drawCombo(GraphicsContext gc, double x, double y, int count) {
        gc.save(); gc.translate(x+60,y); gc.scale(comboScale,comboScale);
        gc.setFont(Font.font("Impact",FontWeight.BOLD,36));
        gc.setFill(GameConfig.COLOR_GOLD); gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(count+" HIT",0,0);
        gc.setFont(Font.font("Impact",FontWeight.BOLD,20));
        gc.fillText("COMBO!",0,28);
        gc.restore();
    }

    public void triggerComboScale() { comboScale = 1.5; }
}
