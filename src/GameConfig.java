import javafx.scene.paint.Color;

/**
 * GameConfig.java
 * Central configuration class holding all game constants.
 * Uses static final fields for easy access across the entire codebase.
 * Demonstrates proper use of encapsulation and centralized configuration.
 */
public final class GameConfig {

    private GameConfig() {} // Prevent instantiation

    // --- WINDOW ---
    public static final int WINDOW_WIDTH = 1100;
    public static final int WINDOW_HEIGHT = 750;
    public static final int CANVAS_WIDTH = 1100;
    public static final int CANVAS_HEIGHT = 550;
    public static final String GAME_TITLE = "JOSH-E-JUNG: TEKKEN EDITION";

    // --- PHYSICS ---
    public static final double GRAVITY = 1800.0;
    public static final double GROUND_Y = 460.0;
    public static final double LERP_SPEED = 0.12;
    public static final double KNOCKBACK_DECAY = 0.92;
    public static final double JUMP_VELOCITY = -700.0;
    public static final double MOVE_SPEED = 350.0;
    public static final double DASH_SPEED = 600.0;
    public static final double DASH_DURATION = 0.2;

    // --- COMBAT ---
    public static final int ROUND_TIME = 99;
    public static final int ROUNDS_TO_WIN = 2;
    public static final int MAX_COMBO_WINDOW_MS = 500;
    public static final double ATTACK_RANGE = 160.0;
    public static final double BLOCK_DAMAGE_REDUCTION = 0.7;
    public static final int HIT_STOP_FRAMES = 6;
    public static final double SLOW_MO_DURATION = 0.6;
    public static final double SLOW_MO_FACTOR = 0.25;

    // --- SCREEN SHAKE ---
    public static final double SHAKE_INTENSITY_LIGHT = 4.0;
    public static final double SHAKE_INTENSITY_HEAVY = 12.0;
    public static final double SHAKE_INTENSITY_KO = 25.0;
    public static final double SHAKE_DECAY = 0.88;

    // --- PARTICLES ---
    public static final int MAX_PARTICLES = 500;
    public static final double PARTICLE_GRAVITY = 400.0;

    // --- CHARACTER DRAWING DIMENSIONS ---
    public static final double CHAR_DRAW_WIDTH = 180.0;
    public static final double CHAR_DRAW_HEIGHT = 280.0;
    public static final double PORTRAIT_WIDTH = 120.0;
    public static final double PORTRAIT_HEIGHT = 120.0;

    // --- HP BAR ---
    public static final double HP_BAR_WIDTH = 380.0;
    public static final double HP_BAR_HEIGHT = 28.0;
    public static final double SPIRIT_BAR_WIDTH = 200.0;
    public static final double SPIRIT_BAR_HEIGHT = 12.0;

    // --- COLORS ---
    public static final Color COLOR_GOLD = Color.web("#f1c40f");
    public static final Color COLOR_CRIMSON = Color.web("#e74c3c");
    public static final Color COLOR_EMERALD = Color.web("#2ecc71");
    public static final Color COLOR_AZURE = Color.web("#3498db");
    public static final Color COLOR_AMETHYST = Color.web("#9b59b6");
    public static final Color COLOR_DARK_BG = Color.web("#0a0a12");
    public static final Color COLOR_PANEL_BG = Color.web("#1a1a2e");
    public static final Color COLOR_PANEL_BORDER = Color.web("#16213e");
    public static final Color COLOR_HP_GREEN = Color.web("#27ae60");
    public static final Color COLOR_HP_YELLOW = Color.web("#f39c12");
    public static final Color COLOR_HP_RED = Color.web("#c0392b");
    public static final Color COLOR_SPIRIT_BLUE = Color.web("#2980b9");
    public static final Color COLOR_HIT_FLASH = Color.web("#ffffffcc");

    // --- ASSET PATHS ---
    public static final String ASSETS_DIR = "assets/";
    public static final String CHARACTERS_DIR = ASSETS_DIR + "characters/";
    public static final String STAGES_DIR = ASSETS_DIR + "stages/";
    public static final String UI_DIR = ASSETS_DIR + "ui/";
    public static final String AUDIO_DIR = ASSETS_DIR + "audio/";

    // --- CHARACTER FILE NAMES ---
    public static final String[] CHARACTER_IDS = {
        "al_murtaza", "noor_jahan", "peer_sahib", "marid",
        "churail", "dev", "jinn", "iblis"
    };

    public static final String[] CHARACTER_NAMES = {
        "AL-MURTAZA", "NOOR JAHAN", "PEER SAHIB", "MARID",
        "CHURAIL", "DEV", "JINN", "IBLIS"
    };

    public static final String[] CHARACTER_TITLES = {
        "The Sufi Warrior", "The Mughal Sorceress", "The Mystic Dervish",
        "The Water Djinn", "The Ghost Witch", "The Stone Giant",
        "The Fire Spirit", "The Dark Lord"
    };

    // --- STAGE NAMES ---
    public static final String[] STAGE_IDS = {
        "thar_desert", "mughal_garden", "haunted_haveli",
        "jinn_lair", "iblis_throne"
    };
    public static final String[] STAGE_NAMES = {
        "Thar Desert", "Mughal Garden", "Haunted Haveli",
        "Jinn's Lair", "Iblis Throne Room"
    };

    // --- STORY MODE ---
    public static final String[][] STORY_CHAPTERS = {
        // Chapter 1: The Awakening
        {
            "Chapter I: The Awakening",
            "In the ancient lands of Hindustan, darkness stirs...",
            "The Sufi warrior Al-Murtaza receives a vision—\nevil forces gather under the command of Iblis.",
            "He must journey through cursed lands,\ndefeat the forces of darkness,\nand restore balance to the realm.",
            "His path begins in the scorching Thar Desert..."
        },
        // Chapter 2: The Gathering Storm
        {
            "Chapter II: The Gathering Storm",
            "Having proven his strength in the desert,\nAl-Murtaza seeks allies for the battle ahead.",
            "Noor Jahan, the Mughal Sorceress, joins his cause.\nPeer Sahib, the ancient mystic, offers wisdom.",
            "But the forces of darkness grow stronger.\nThe Churail haunts the abandoned havelis,\nand the Dev guards the mountain passes.",
            "The warriors must face them all..."
        },
        // Chapter 3: The Final Battle
        {
            "Chapter III: The Final Battle",
            "The path to Iblis's throne room is revealed.\nThrough the underground lairs of the Jinn,\nthe warriors descend into the abyss.",
            "Iblis awaits on his throne of black stone,\nsurrounded by rivers of fire and shadow.",
            "This is the final battle.\nThe fate of all creation hangs in the balance.",
            "PREPARE FOR THE ULTIMATE FIGHT!"
        }
    };

    // --- GAME MODES ---
    public static final int MODE_ARCADE = 0;
    public static final int MODE_STORY = 1;
    public static final int MODE_PVP = 2;
}
