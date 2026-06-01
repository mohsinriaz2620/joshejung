import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * StoryState.java
 * Displays narrative cutscenes between story mode battles.
 * Features multi-page dialogue with character portraits and speech bubbles.
 * Press ENTER to advance dialogue, then begins the fight.
 */
public class StoryState extends GameState {

    // Each chapter has: title, stage background ID, and dialogue lines.
    // Dialogue format: "SPEAKER_ID|Text" where SPEAKER_ID maps to a portrait.
    // Use "NARRATOR|" for narration (no portrait), or character IDs for speech bubbles.
    private static final String[][][] STORY_CHAPTERS = {
        // === Chapter 0: Prologue (no fight - auto-advance to chapter 1) ===
        {
            {"Prologue: The Shattered Amulet", "thar_desert"},
            {
                "NARRATOR|In the ancient lands of Hindustan, a fragile peace held for centuries...",
                "NARRATOR|The Amulet of Jamshed, a relic of immense power, kept the veil\nbetween the mortal world and the realm of Djinn sealed tight.",
                "NARRATOR|But on the night of a blood-red moon, the seal shattered.\nDark forces poured through the cracks in reality.",
                "al_murtaza|I felt the tremor in my prayers tonight.\nThe balance has been broken... I must act.",
                "al_murtaza|The Amulet's shards have scattered across the land.\nIf Iblis collects them first, all will be lost.",
                "NARRATOR|And so the Sufi warrior Al-Murtaza began his quest,\nwielding the legendary Zulfiqar, to restore the Amulet\nand banish the darkness once more.",
                "NARRATOR|His first destination: the scorching Thar Desert,\nwhere the water djinn Marid guards a shard..."
            }
        },
        // === Chapter 1: Thar Desert - vs Marid ===
        {
            {"Chapter I: The Desert Guardian", "thar_desert"},
            {
                "NARRATOR|The endless dunes of the Thar stretch before Al-Murtaza.\nHeat shimmers distort the horizon as he approaches an oasis.",
                "al_murtaza|The amulet shard pulses with energy...\nI can feel its power beneath these sands.",
                "marid|HALT, mortal! You dare trespass in my domain?",
                "al_murtaza|I mean no disrespect, O Marid.\nI seek only the Amulet shard to restore the seal.",
                "marid|The shard is MINE now. Its power feeds my waters.\nIf you want it... you must TAKE it from me!",
                "al_murtaza|Then forgive me, spirit.\nThis blade speaks when words fail.",
                "NARRATOR|The desert erupts in a clash of steel and water.\nThe battle for the first shard begins!"
            }
        },
        // === Chapter 2: Haunted Haveli - vs Churail ===
        {
            {"Chapter II: The Haunted Haveli", "haunted_haveli"},
            {
                "NARRATOR|With the first shard recovered, Al-Murtaza travels north\nto the abandoned havelis of Punjab.",
                "NARRATOR|The villages whisper of a ghostly presence\nthat devours the souls of wanderers at night.",
                "al_murtaza|I can feel the darkness here.\nThe walls themselves weep with sorrow.",
                "churail|Hehehehe... Another soul wanders into my web.\nHow... delicious.",
                "al_murtaza|Churail! Release the shard and the souls you hold captive!",
                "churail|Release them? Oh, but they keep me company\nin this lonely haveli... just as YOU will!",
                "al_murtaza|Your reign of terror ends tonight, witch.\nBy the light of the Divine, I will set them free!",
                "churail|Then come, warrior. Let us see if your faith\nis stronger than my hunger!",
                "NARRATOR|The haveli trembles as ancient power collides.\nDarkness fights against light in the cursed halls."
            }
        },
        // === Chapter 3: Iblis Throne - vs Iblis ===
        {
            {"Chapter III: The Throne of Shadows", "iblis_throne"},
            {
                "NARRATOR|Two shards recovered. The Amulet is nearly whole.\nBut the final piece lies in the deepest abyss.",
                "NARRATOR|Through the underground lairs of the Jinn,\npast rivers of fire and shadow, Al-Murtaza descends.",
                "al_murtaza|The air burns with brimstone.\nI can feel his presence... Iblis awaits.",
                "iblis|So... the little Sufi comes at last.\nI have been watching your pathetic journey.",
                "al_murtaza|Iblis! Your corruption ends here.\nReturn the final shard!",
                "iblis|Return it? I AM the darkness, fool.\nThe shard is part of me now. To take it...\nyou must destroy me. And THAT... is impossible.",
                "al_murtaza|Nothing is impossible with faith.\nLa hawla wa la quwwata illa billah!",
                "iblis|Then DIE with your prayers on your lips!\nI will enjoy crushing your hope!",
                "NARRATOR|This is the final battle.\nThe fate of all creation hangs in the balance.\n\nPREPARE FOR THE ULTIMATE FIGHT!"
            }
        },
        // === Chapter 4: Epilogue (no fight) ===
        {
            {"Epilogue: Dawn of a New Age", "mughal_garden"},
            {
                "NARRATOR|With a final strike of the Zulfiqar, Iblis is vanquished.\nHis throne crumbles into the abyss.",
                "NARRATOR|The three shards of the Amulet of Jamshed reunite,\nglowing with ancient light that fills the cavern.",
                "al_murtaza|It is done. The seal is restored.\nThe darkness will not return... not in this age.",
                "NARRATOR|As dawn breaks over the land of Hindustan,\nthe wounds of the world begin to heal.",
                "NARRATOR|The spirits return to their realms.\nThe ghosts find peace. The fire dims.",
                "al_murtaza|My journey has ended, but the path of the righteous\nnever truly ends. There will always be those\nwho walk in the light.",
                "NARRATOR|And so the legend of Al-Murtaza,\nthe Sufi Warrior of Hindustan, was written\ninto the eternal pages of history.",
                "NARRATOR|~~ THE END ~~\n\nThank you for playing JOSH-E-JUNG!\n\nPress ENTER to return to the menu."
            }
        }
    };

    // Maps chapter index to which fight opponent index (-1 = no fight, cutscene only)
    private static final int[] CHAPTER_OPPONENT = { -1, 3, 4, 7, -1 };
    // Maps chapter index to stage index for the fight
    private static final int[] CHAPTER_STAGE = { 0, 0, 2, 4, 1 };

    private Image stageBackground;
    private Image speakerPortrait;
    private String currentSpeakerId = "";
    private int currentChapter = 0;
    private int currentLine = 0;
    private int textCharsToDraw = 0;
    private double timer = 0;
    private double bubbleAnimTimer = 0;

    public StoryState(GameEngine engine) {
        super(engine);
    }

    @Override
    public void enter() {
        currentChapter = engine.getCurrentStoryChapter();
        if (currentChapter >= STORY_CHAPTERS.length) {
            engine.switchState("Menu");
            return;
        }

        currentLine = 0;
        textCharsToDraw = 0;
        timer = 0;
        bubbleAnimTimer = 0;

        // Load stage background
        String stageId = STORY_CHAPTERS[currentChapter][0][1];
        stageBackground = SpriteRenderer.loadStageBackground(stageId);
        speakerPortrait = null;
        currentSpeakerId = "";

        AudioManager.playBGM("menu_bgm.wav");
    }

    @Override
    public void exit() {
    }

    @Override
    public void update(double dt) {
        timer += dt;
        bubbleAnimTimer += dt;

        String[] dialogueLines = STORY_CHAPTERS[currentChapter][1];
        if (currentLine < dialogueLines.length) {
            String line = dialogueLines[currentLine];
            String text = line.substring(line.indexOf('|') + 1);
            int maxChars = text.length();
            textCharsToDraw = Math.min(maxChars, (int)(timer * 40)); // 40 chars/sec
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (currentChapter >= STORY_CHAPTERS.length) return;
        String[] dialogueLines = STORY_CHAPTERS[currentChapter][1];
        if (currentLine >= dialogueLines.length) return;

        // Background
        SpriteRenderer.drawBackground(gc, stageBackground);

        // Dark cinematic overlay
        gc.setFill(new Color(0, 0, 0, 0.65));
        gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);

        // Chapter title at top
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 36));
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText(STORY_CHAPTERS[currentChapter][0][0], GameConfig.CANVAS_WIDTH / 2, 50);

        // Parse current line
        String line = dialogueLines[currentLine];
        String speakerId = line.substring(0, line.indexOf('|'));
        String fullText = line.substring(line.indexOf('|') + 1);
        String visibleText = fullText.substring(0, Math.min(textCharsToDraw, fullText.length()));

        // Load speaker portrait if changed
        if (!speakerId.equals(currentSpeakerId)) {
            currentSpeakerId = speakerId;
            if (!speakerId.equals("NARRATOR")) {
                speakerPortrait = SpriteRenderer.loadCharacterPortrait(speakerId);
            } else {
                speakerPortrait = null;
            }
        }

        if (speakerId.equals("NARRATOR")) {
            // --- Narration: centered text with cinematic bar ---
            drawNarrationBox(gc, visibleText);
        } else {
            // --- Character dialogue: speech bubble with portrait ---
            drawSpeechBubble(gc, speakerId, visibleText);
        }

        // Page indicator
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
        gc.setFill(new Color(1, 1, 1, 0.5));
        gc.fillText((currentLine + 1) + " / " + dialogueLines.length,
            GameConfig.CANVAS_WIDTH - 20, GameConfig.CANVAS_HEIGHT - 15);

        // Continue prompt (blinking)
        if (textCharsToDraw >= fullText.length()) {
            if ((int)(timer * 2) % 2 == 0) {
                gc.setTextAlign(TextAlignment.CENTER);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
                gc.setFill(GameConfig.COLOR_GOLD);
                gc.fillText("▸ Press ENTER to continue ◂",
                    GameConfig.CANVAS_WIDTH / 2, GameConfig.CANVAS_HEIGHT - 15);
            }
        }
    }

    /** Draws a centered narration text box with a semi-transparent background. */
    private void drawNarrationBox(GraphicsContext gc, String text) {
        double boxW = GameConfig.CANVAS_WIDTH - 120;
        double boxH = 160;
        double boxX = 60;
        double boxY = GameConfig.CANVAS_HEIGHT / 2 - boxH / 2 + 30;

        // Box background
        gc.setFill(new Color(0, 0, 0, 0.75));
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 15, 15);
        gc.setStroke(new Color(0.85, 0.65, 0.13, 0.6));
        gc.setLineWidth(2);
        gc.strokeRoundRect(boxX, boxY, boxW, boxH, 15, 15);

        // Narrator label
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        gc.setFill(GameConfig.COLOR_GOLD);
        gc.fillText("NARRATOR", boxX + 20, boxY + 25);

        // Text
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 18));
        gc.setFill(Color.WHITE);
        drawWrappedText(gc, text, boxX + 20, boxY + 50, boxW - 40, 24);
    }

    /** Draws a speech bubble with character portrait. */
    private void drawSpeechBubble(GraphicsContext gc, String speakerId, String text) {
        // Determine if speaker is on left or right
        boolean isHero = speakerId.equals("al_murtaza") || speakerId.equals("noor_jahan") || speakerId.equals("peer_sahib");
        double portraitSize = 140;
        double bubbleW = GameConfig.CANVAS_WIDTH - 240;
        double bubbleH = 150;

        double portraitX, portraitY, bubbleX, bubbleY;

        if (isHero) {
            // Portrait on left
            portraitX = 30;
            portraitY = GameConfig.CANVAS_HEIGHT - portraitSize - 60;
            bubbleX = portraitX + portraitSize + 15;
            bubbleY = portraitY + 5;
        } else {
            // Portrait on right
            portraitX = GameConfig.CANVAS_WIDTH - portraitSize - 30;
            portraitY = GameConfig.CANVAS_HEIGHT - portraitSize - 60;
            bubbleX = 30;
            bubbleY = portraitY + 5;
            bubbleW = portraitX - 45;
        }

        // Portrait frame
        gc.setFill(new Color(0, 0, 0, 0.8));
        gc.fillRoundRect(portraitX - 4, portraitY - 4, portraitSize + 8, portraitSize + 8, 10, 10);
        gc.setStroke(isHero ? Color.CYAN : Color.RED);
        gc.setLineWidth(3);
        gc.strokeRoundRect(portraitX - 4, portraitY - 4, portraitSize + 8, portraitSize + 8, 10, 10);

        // Portrait image
        if (speakerPortrait != null) {
            gc.drawImage(speakerPortrait, portraitX, portraitY, portraitSize, portraitSize);
        }

        // Speech bubble background
        double bubbleAnim = Math.min(1.0, bubbleAnimTimer * 4); // Quick pop-in
        gc.save();
        gc.setGlobalAlpha(bubbleAnim);

        gc.setFill(new Color(1, 1, 1, 0.92));
        gc.fillRoundRect(bubbleX, bubbleY, bubbleW, bubbleH, 18, 18);

        // Bubble tail (triangle pointing to portrait)
        if (isHero) {
            // Tail pointing left
            gc.setFill(new Color(1, 1, 1, 0.92));
            gc.fillPolygon(
                new double[]{bubbleX, bubbleX - 15, bubbleX},
                new double[]{bubbleY + 30, bubbleY + 45, bubbleY + 55}, 3
            );
        } else {
            // Tail pointing right
            gc.setFill(new Color(1, 1, 1, 0.92));
            gc.fillPolygon(
                new double[]{bubbleX + bubbleW, bubbleX + bubbleW + 15, bubbleX + bubbleW},
                new double[]{bubbleY + 30, bubbleY + 45, bubbleY + 55}, 3
            );
        }

        // Speaker name in bubble
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        gc.setFill(isHero ? Color.web("#006699") : Color.web("#990000"));
        String displayName = getDisplayName(speakerId);
        gc.fillText(displayName, bubbleX + 15, bubbleY + 24);

        // Underline
        gc.setStroke(isHero ? Color.web("#006699") : Color.web("#990000"));
        gc.setLineWidth(1.5);
        gc.strokeLine(bubbleX + 15, bubbleY + 28, bubbleX + bubbleW - 15, bubbleY + 28);

        // Dialogue text (dark on white)
        gc.setFont(Font.font("Verdana", FontWeight.NORMAL, 16));
        gc.setFill(Color.web("#1a1a1a"));
        drawWrappedText(gc, text, bubbleX + 15, bubbleY + 48, bubbleW - 30, 22);

        gc.restore();
    }

    /** Helper: Get display name from character ID */
    private String getDisplayName(String id) {
        for (int i = 0; i < GameConfig.CHARACTER_IDS.length; i++) {
            if (GameConfig.CHARACTER_IDS[i].equals(id)) {
                return GameConfig.CHARACTER_NAMES[i];
            }
        }
        return id.toUpperCase().replace('_', ' ');
    }

    /** Helper: Draw text with manual line wrapping on \n */
    private void drawWrappedText(GraphicsContext gc, String text, double x, double y, double maxW, double lineH) {
        String[] lines = text.split("\n");
        double currentY = y;
        for (String line : lines) {
            gc.fillText(line, x, currentY);
            currentY += lineH;
        }
    }

    @Override
    public void handleKeyPressed(KeyCode code) {
        if (currentChapter >= STORY_CHAPTERS.length) return;
        String[] dialogueLines = STORY_CHAPTERS[currentChapter][1];

        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            String line = dialogueLines[currentLine];
            String fullText = line.substring(line.indexOf('|') + 1);

            if (textCharsToDraw < fullText.length()) {
                // Skip text crawl — reveal all text
                textCharsToDraw = fullText.length();
            } else {
                // Advance to next line
                currentLine++;
                timer = 0;
                textCharsToDraw = 0;
                bubbleAnimTimer = 0;
                currentSpeakerId = ""; // Force portrait reload
                AudioManager.playSFX("hit_light.wav");

                if (currentLine >= dialogueLines.length) {
                    // All dialogue done — proceed
                    int opponent = CHAPTER_OPPONENT[currentChapter];
                    if (opponent == -1) {
                        // No fight for this chapter (prologue/epilogue)
                        if (currentChapter >= STORY_CHAPTERS.length - 1) {
                            // Epilogue done — back to menu
                            engine.switchState("Menu");
                        } else {
                            // Move to next chapter
                            engine.setCurrentStoryChapter(currentChapter + 1);
                            engine.switchState("Story");
                        }
                    } else {
                        // Set fight opponent and stage, proceed to VS screen
                        engine.setP2CharIndex(opponent);
                        engine.setStageIndex(CHAPTER_STAGE[currentChapter]);
                        AudioManager.playSFX("special.wav");
                        engine.switchState("VSScreen");
                    }
                }
            }
        }
    }
}
