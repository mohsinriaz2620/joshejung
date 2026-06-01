import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * ChatBubble.java
 * Renders animated speech bubbles above fighters during combat.
 * Bubbles appear, linger, then fade out with a smooth animation.
 * Used for taunts, battle cries, and character-specific dialogue.
 */
public class ChatBubble {

    private String text;
    private double x, y;
    private double lifetime;     // Total time to display
    private double elapsed;      // Time since spawn
    private boolean isLeft;      // Tail points left (P1) or right (P2)
    private Color borderColor;

    // Animation state
    private double scaleAnim = 0; // Grows from 0 to 1

    // Static taunt databases per character
    private static final String[][] FIGHT_TAUNTS = {
        // [0] Al-Murtaza
        {
            "Bismillah!",
            "Ya Ali!",
            "Feel the Zulfiqar!",
            "Your darkness ends here!",
            "By the will of the Divine!",
            "I fight for the light!",
            "You cannot defeat faith!",
            "This sword never fails!",
            "La hawla wa la quwwata...",
            "Surrender, or face justice!"
        },
        // [1] Noor Jahan
        {
            "Bow before the Empress!",
            "My magic knows no bounds!",
            "Nazar-e-Bad upon you!",
            "The Mughal Empire strikes!",
            "You dare challenge me?",
            "Feel my arcane fury!",
            "My power is absolute!",
            "Such impudence!"
        },
        // [2] Peer Sahib
        {
            "Peace be upon you, child...",
            "Let me teach you humility.",
            "The Sufi way is peace...\nbut also strength.",
            "SubhanAllah!",
            "Forgiveness is power.",
            "Your spirit is restless.",
            "Calm yourself, child.",
            "The dervish whirls!"
        },
        // [3] Marid
        {
            "The ocean obeys ME!",
            "Drown in my waves!",
            "You are but a drop...\nI am the flood!",
            "Water crushes stone!",
            "Feel the tide's wrath!",
            "Foolish mortal!",
            "My waters will consume you!",
            "The depths call for you!"
        },
        // [4] Churail
        {
            "Hehehehe...",
            "Your soul smells... delicious!",
            "Come closer, darling...",
            "No one leaves my haveli!",
            "I'll add you to\nmy collection!",
            "Scream for me!",
            "The night is mine!",
            "You'll never escape!"
        },
        // [5] Dev
        {
            "CRUSH! SMASH! DESTROY!",
            "Puny human!",
            "I am the mountain!",
            "Nothing can break me!",
            "RAAAAGH!",
            "Dev STRONG!",
            "You are an insect!",
            "The earth trembles!"
        },
        // [6] Jinn
        {
            "Burn in my flames!",
            "Fire is my nature!",
            "You cannot extinguish me!",
            "The smokeless fire rises!",
            "Ashes to ashes!",
            "Feel the inferno!",
            "I was born from flame!",
            "Your world will burn!"
        },
        // [7] Iblis
        {
            "KNEEL before darkness!",
            "I refused to bow once...\nI refuse again!",
            "Your faith is WEAK!",
            "I am ETERNAL!",
            "Foolish mortal warrior...",
            "The throne of shadows\nis my birthright!",
            "Despair is my weapon!",
            "You CANNOT win!",
            "I am the whisper\nin every heart!",
            "AHAHAHA!"
        }
    };

    // Pre-fight intro dialogue (P1 says first, P2 responds)
    private static final String[][] INTRO_DIALOGUE_P1 = {
        // P1 intro lines (indexed by P1 character index)
        {"Let us begin!", "Prepare yourself!", "I won't hold back!", "For honor!", "In the name of light!", "Show me your strength!", "Ready? Let's go!", "Time to fight!"}
    };

    // Response from P2 based on their character
    private static final String[] INTRO_RESPONSE = {
        "You'll regret this!",       // Al-Murtaza
        "How amusing...",             // Noor Jahan
        "May the best soul win.",     // Peer Sahib
        "You will drown!",           // Marid
        "Fresh meat! Hehehe...",     // Churail
        "SMASH YOU!",               // Dev
        "You'll be cinders!",        // Jinn
        "This will be your end!"    // Iblis
    };

    public ChatBubble(String text, double x, double y, boolean isLeft, Color borderColor, double lifetime) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.isLeft = isLeft;
        this.borderColor = borderColor;
        this.lifetime = lifetime;
        this.elapsed = 0;
        this.scaleAnim = 0;
    }

    /** Update the bubble animation. Returns true if bubble is still alive. */
    public boolean update(double dt) {
        elapsed += dt;
        // Pop-in animation (first 0.15s)
        if (scaleAnim < 1.0) {
            scaleAnim = Math.min(1.0, scaleAnim + dt * 7);
        }
        return elapsed < lifetime;
    }

    /** Render the speech bubble. */
    public void render(GraphicsContext gc) {
        if (text == null || text.isEmpty()) return;

        // Fade out in last 0.5s
        double alpha = 1.0;
        if (elapsed > lifetime - 0.5) {
            alpha = Math.max(0, (lifetime - elapsed) / 0.5);
        }

        gc.save();
        gc.setGlobalAlpha(alpha);

        // Calculate bubble dimensions based on text
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        int maxLineLen = 0;
        for (String l : lines) {
            if (l.length() > maxLineLen) maxLineLen = l.length();
        }

        double bubbleW = Math.max(100, maxLineLen * 8.5 + 24);
        double bubbleH = 28 + lineCount * 18;
        double bubbleX = isLeft ? x - bubbleW - 10 : x + 10;
        double bubbleY = y - bubbleH - 20;

        // Clamp to screen
        bubbleX = Math.max(5, Math.min(GameConfig.CANVAS_WIDTH - bubbleW - 5, bubbleX));
        bubbleY = Math.max(5, bubbleY);

        // Scale animation
        gc.translate(bubbleX + bubbleW / 2, bubbleY + bubbleH / 2);
        gc.scale(scaleAnim, scaleAnim);
        gc.translate(-(bubbleX + bubbleW / 2), -(bubbleY + bubbleH / 2));

        // Bubble background
        gc.setFill(new Color(1, 1, 1, 0.9));
        gc.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 12, 12);

        // Border
        gc.setStroke(borderColor);
        gc.setLineWidth(2);
        gc.strokeRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 12, 12);

        // Tail triangle
        double tailX = isLeft ? bubbleX + bubbleW - 20 : bubbleX + 20;
        gc.setFill(new Color(1, 1, 1, 0.9));
        gc.fillPolygon(
            new double[]{tailX - 6, tailX + 6, isLeft ? tailX + 12 : tailX - 12},
            new double[]{bubbleY + bubbleH, bubbleY + bubbleH, bubbleY + bubbleH + 10}, 3
        );
        // Tail border
        gc.setStroke(borderColor);
        gc.setLineWidth(1.5);
        gc.strokePolygon(
            new double[]{tailX - 6, tailX + 6, isLeft ? tailX + 12 : tailX - 12},
            new double[]{bubbleY + bubbleH, bubbleY + bubbleH, bubbleY + bubbleH + 10}, 3
        );

        // Text
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        gc.setFill(Color.web("#1a1a1a"));
        double textY = bubbleY + 18;
        for (String l : lines) {
            gc.fillText(l, bubbleX + 12, textY);
            textY += 18;
        }

        gc.restore();
    }

    // --- Static helper methods ---

    /** Get a random taunt for a character by index. */
    public static String getRandomTaunt(int characterIndex) {
        if (characterIndex < 0 || characterIndex >= FIGHT_TAUNTS.length) return "...";
        String[] taunts = FIGHT_TAUNTS[characterIndex];
        return taunts[(int)(Math.random() * taunts.length)];
    }

    /** Get an intro line for a character. */
    public static String getIntroLine(int characterIndex) {
        String[] lines = INTRO_DIALOGUE_P1[0]; // Generic pool
        return lines[(int)(Math.random() * lines.length)];
    }

    /** Get an intro response for a character by index. */
    public static String getIntroResponse(int characterIndex) {
        if (characterIndex < 0 || characterIndex >= INTRO_RESPONSE.length) return "...";
        return INTRO_RESPONSE[characterIndex];
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
}
