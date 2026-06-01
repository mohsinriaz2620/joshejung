import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main.java
 * The core entry point for Josh-e-Jung.
 * Re-written from the monolithic script to cleanly initialize the GameEngine
 * and start the game loop.
 */
public class Main extends Application {

    private GameEngine engine;
    private long lastTime;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(GameConfig.GAME_TITLE);

        // Setup Canvas and Scene
        Canvas canvas = new Canvas(GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        // Generate procedural sound effects if missing
        SoundGenerator.generateAll();

        // Initialize Audio and Engine
        AudioManager.preloadAll();
        engine = new GameEngine(canvas);

        // Register States
        engine.addState("Menu", new MenuState(engine));
        engine.addState("CharSelect", new CharSelectState(engine));
        engine.addState("VSScreen", new VSScreenState(engine));
        engine.addState("Fight", new FightState(engine));
        engine.addState("Victory", new VictoryState(engine));
        engine.addState("Shop", new ShopState(engine));
        engine.addState("Story", new StoryState(engine));

        // Input Handling
        scene.setOnKeyPressed(e -> engine.handleKeyPressed(e.getCode()));
        scene.setOnKeyReleased(e -> engine.handleKeyReleased(e.getCode()));
        scene.setOnMousePressed(e -> engine.handleMousePressed(e.getButton()));

        // Start Initial State
        engine.switchState("Menu");

        // Game Loop
        lastTime = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    double dt = (now - lastTime) / 1_000_000_000.0;
                    lastTime = now;
                    
                    // Cap delta time to prevent physics issues on lag spikes
                    if (dt > 0.1) dt = 0.1;

                    engine.update(dt);
                    
                    // Clear canvas before render
                    engine.getGc().clearRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
                    engine.render();
                } catch (Throwable t) {
                    System.err.println("CRASH IN GAME LOOP:");
                    t.printStackTrace();
                    try {
                        java.io.PrintWriter pw = new java.io.PrintWriter("crash_log.txt");
                        t.printStackTrace(pw);
                        pw.close();
                    } catch (Exception e) {}
                    stop(); // Stop the timer on crash
                }
            }
        };

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}