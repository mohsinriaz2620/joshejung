import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * SpriteRenderer.java
 * Handles loading, caching, and rendering of character sprites and backgrounds.
 * Supports flipping, tinting (damage flash), scaling, and opacity.
 * Uses a HashMap cache to avoid reloading images.
 */
public class SpriteRenderer {

    private static Map<String, Image> imageCache = new HashMap<>();

    /**
     * Loads an image from the assets directory, with caching.
     */
    public static Image loadImage(String relativePath) {
        if (imageCache.containsKey(relativePath)) {
            return imageCache.get(relativePath);
        }

        try {
            File file = new File(relativePath);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                imageCache.put(relativePath, img);
                return img;
            }
        } catch (Exception e) {
            System.err.println("[SpriteRenderer] Failed to load: " + relativePath);
        }

        return null;
    }

    /**
     * Loads a character portrait image.
     */
    public static Image loadCharacterPortrait(String characterId) {
        return loadImage(GameConfig.CHARACTERS_DIR + characterId + ".png");
    }

    /**
     * Loads a stage background image.
     */
    public static Image loadStageBackground(String stageId) {
        return loadImage(GameConfig.STAGES_DIR + stageId + ".png");
    }

    /**
     * Draws a sprite at the given position with optional flipping.
     * @param gc Graphics context
     * @param img Image to draw
     * @param x Center X position
     * @param y Bottom Y position
     * @param width Draw width
     * @param height Draw height
     * @param facingLeft Whether to flip horizontally
     * @param tintColor Optional tint color (null for no tint)
     * @param opacity Opacity (0.0 to 1.0)
     */
    public static void drawSprite(GraphicsContext gc, Image img, double x, double y,
                                   double width, double height, boolean facingLeft,
                                   Color tintColor, double opacity) {
        if (img == null) return;

        gc.save();
        gc.setGlobalAlpha(opacity);

        if (facingLeft) {
            gc.translate(x + width / 2, y - height);
            gc.scale(-1, 1);
            gc.drawImage(img, -width / 2, 0, width, height);
        } else {
            gc.drawImage(img, x - width / 2, y - height, width, height);
        }

        // Apply tint overlay if specified (damage flash)
        if (tintColor != null) {
            gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_ATOP);
            gc.setFill(tintColor);
            if (facingLeft) {
                gc.fillRect(-width / 2, 0, width, height);
            } else {
                gc.fillRect(x - width / 2, y - height, width, height);
            }
        }

        gc.restore();
    }

    /**
     * Draws a sprite as a character in the fight scene with bob animation.
     */
    public static void drawFighter(GraphicsContext gc, Image img, double x, double y,
                                    double width, double height, boolean facingLeft,
                                    double hitFlashTimer, double bobOffset) {
        Color tint = null;
        double opacity = 1.0;

        if (hitFlashTimer > 0) {
            tint = new Color(1, 1, 1, 0.6);
        }

        drawSprite(gc, img, x, y + bobOffset, width, height, facingLeft, tint, opacity);
    }

    /**
     * Draws a background image stretched to fill the canvas.
     */
    public static void drawBackground(GraphicsContext gc, Image img) {
        if (img != null) {
            gc.drawImage(img, 0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        } else {
            // Fallback gradient
            gc.setFill(new javafx.scene.paint.LinearGradient(
                0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.web("#1a1a2e")),
                new javafx.scene.paint.Stop(1, Color.web("#0f0f23"))
            ));
            gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        }
    }

    /**
     * Draws a character portrait for the HUD/select screen.
     */
    public static void drawPortrait(GraphicsContext gc, Image img, double x, double y,
                                     double width, double height, boolean selected) {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        } else {
            gc.setFill(Color.GRAY);
            gc.fillRect(x, y, width, height);
        }

        if (selected) {
            gc.setStroke(GameConfig.COLOR_GOLD);
            gc.setLineWidth(3);
            gc.strokeRect(x - 2, y - 2, width + 4, height + 4);
        }
    }

    /**
     * Pre-loads all game images into cache.
     */
    public static void preloadAll() {
        System.out.println("[SpriteRenderer] Pre-loading assets...");

        for (String id : GameConfig.CHARACTER_IDS) {
            loadCharacterPortrait(id);
        }
        for (String id : GameConfig.STAGE_IDS) {
            loadStageBackground(id);
        }
        loadImage(GameConfig.UI_DIR + "title_background.png");

        System.out.println("[SpriteRenderer] Assets loaded: " + imageCache.size() + " images.");
    }

    /**
     * Clears the image cache to free memory.
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
