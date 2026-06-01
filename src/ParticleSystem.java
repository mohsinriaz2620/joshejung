import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * ParticleSystem.java
 * Enhanced particle engine with multiple emitter types.
 * Handles floating text, particle effects (fire, lightning, smoke, healing),
 * screen shake, and cinematic effects.
 */
public class ParticleSystem {

    // --- Inner Classes ---
    public static class FloatingText {
        String text;
        double x, y;
        Color color;
        double opacity;
        double scale;
        double vx, vy;
        boolean isBig;

        public FloatingText(String text, double x, double y, Color color, boolean isBig) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.opacity = 1.0;
            this.scale = isBig ? 2.0 : 1.0;
            this.vx = 0;
            this.vy = -60;
            this.isBig = isBig;
        }

        public void update(double dt) {
            x += vx * dt;
            y += vy * dt;
            opacity -= dt * 0.8;
            if (isBig && scale > 1.0) scale -= dt * 1.5;
        }

        public boolean isDead() {
            return opacity <= 0;
        }
    }

    public static class Particle {
        double x, y;
        double vx, vy;
        Color color;
        double life;
        double maxLife;
        double size;
        boolean hasGravity;
        boolean isSpark;

        public Particle(double x, double y, Color color, double size,
                        double vx, double vy, double life, boolean hasGravity) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.hasGravity = hasGravity;
            this.isSpark = false;
        }

        public void update(double dt) {
            x += vx * dt;
            y += vy * dt;
            if (hasGravity) vy += GameConfig.PARTICLE_GRAVITY * dt;
            life -= dt;
            if (isSpark) size *= 0.97;
        }

        public boolean isDead() {
            return life <= 0 || size < 0.5;
        }

        public double getOpacity() {
            return Math.max(0, Math.min(1, life / maxLife));
        }
    }

    // --- Fields ---
    private List<FloatingText> floatingTexts = new ArrayList<>();
    private List<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    // Screen shake
    private double shakeX = 0, shakeY = 0;
    private double shakeIntensity = 0;

    // Hit flash
    private double hitFlashOpacity = 0;
    private Color hitFlashColor = Color.WHITE;

    // Slow motion
    private double slowMoTimer = 0;
    private boolean isSlowMo = false;

    // --- Methods ---

    public void update(double dt) {
        // Update floating texts
        Iterator<FloatingText> ftIt = floatingTexts.iterator();
        while (ftIt.hasNext()) {
            FloatingText ft = ftIt.next();
            ft.update(dt);
            if (ft.isDead()) ftIt.remove();
        }

        // Update particles (cap at max)
        Iterator<Particle> pIt = particles.iterator();
        while (pIt.hasNext()) {
            Particle p = pIt.next();
            p.update(dt);
            if (p.isDead()) pIt.remove();
        }

        // Decay screen shake
        if (shakeIntensity > 0.1) {
            shakeX = (random.nextDouble() - 0.5) * shakeIntensity * 2;
            shakeY = (random.nextDouble() - 0.5) * shakeIntensity * 2;
            shakeIntensity *= GameConfig.SHAKE_DECAY;
        } else {
            shakeX = 0;
            shakeY = 0;
            shakeIntensity = 0;
        }

        // Decay hit flash
        if (hitFlashOpacity > 0) {
            hitFlashOpacity -= dt * 8;
        }

        // Slow motion timer
        if (isSlowMo) {
            slowMoTimer -= dt;
            if (slowMoTimer <= 0) isSlowMo = false;
        }
    }

    public void render(GraphicsContext gc) {
        // Render particles
        for (Particle p : particles) {
            double alpha = p.getOpacity();
            gc.setFill(new Color(
                p.color.getRed(), p.color.getGreen(), p.color.getBlue(),
                Math.max(0, Math.min(1, alpha))
            ));
            if (p.isSpark) {
                gc.save();
                gc.translate(p.x, p.y);
                gc.rotate(Math.atan2(p.vy, p.vx) * 180 / Math.PI);
                gc.fillRect(-p.size, -1, p.size * 2, 2);
                gc.restore();
            } else {
                gc.fillOval(p.x - p.size / 2, p.y - p.size / 2, p.size, p.size);
            }
        }

        // Render floating texts
        for (FloatingText ft : floatingTexts) {
            double alpha = Math.max(0, Math.min(1, ft.opacity));
            gc.setFont(javafx.scene.text.Font.font("Impact", javafx.scene.text.FontWeight.BOLD,
                ft.isBig ? (int)(36 * ft.scale) : 22));
            // Shadow
            gc.setFill(new Color(0, 0, 0, alpha * 0.7));
            gc.fillText(ft.text, ft.x - 28, ft.y + 2);
            // Main text
            gc.setFill(new Color(
                ft.color.getRed(), ft.color.getGreen(), ft.color.getBlue(), alpha
            ));
            gc.fillText(ft.text, ft.x - 30, ft.y);
        }

        // Render hit flash overlay
        if (hitFlashOpacity > 0) {
            gc.setFill(new Color(
                hitFlashColor.getRed(), hitFlashColor.getGreen(),
                hitFlashColor.getBlue(), Math.min(0.4, hitFlashOpacity)
            ));
            gc.fillRect(0, 0, GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        }
    }

    // --- Spawn Methods ---

    public void spawnFloatingText(String text, double x, double y, Color color) {
        floatingTexts.add(new FloatingText(text, x, y, color, false));
    }

    public void spawnBigText(String text, double x, double y, Color color) {
        floatingTexts.add(new FloatingText(text, x, y, color, true));
    }

    public void spawnHitParticles(double x, double y, Color color, int count) {
        for (int i = 0; i < count && particles.size() < GameConfig.MAX_PARTICLES; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 100 + random.nextDouble() * 300;
            Particle p = new Particle(
                x + (random.nextDouble() - 0.5) * 20,
                y + (random.nextDouble() - 0.5) * 20,
                color, 4 + random.nextDouble() * 6,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                0.3 + random.nextDouble() * 0.5,
                true
            );
            particles.add(p);
        }
    }

    public void spawnSparks(double x, double y, Color color, int count) {
        for (int i = 0; i < count && particles.size() < GameConfig.MAX_PARTICLES; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 200 + random.nextDouble() * 500;
            Particle p = new Particle(
                x, y, color, 8 + random.nextDouble() * 12,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed,
                0.2 + random.nextDouble() * 0.3,
                false
            );
            p.isSpark = true;
            particles.add(p);
        }
    }

    public void spawnFireBurst(double x, double y, int count) {
        Color[] fireColors = {Color.ORANGERED, Color.ORANGE, Color.YELLOW, Color.DARKRED};
        for (int i = 0; i < count && particles.size() < GameConfig.MAX_PARTICLES; i++) {
            Color c = fireColors[random.nextInt(fireColors.length)];
            double angle = -Math.PI / 2 + (random.nextDouble() - 0.5) * Math.PI;
            double speed = 80 + random.nextDouble() * 200;
            Particle p = new Particle(
                x + (random.nextDouble() - 0.5) * 30,
                y,
                c, 5 + random.nextDouble() * 10,
                Math.cos(angle) * speed,
                Math.sin(angle) * speed - 100,
                0.5 + random.nextDouble() * 0.8,
                false
            );
            particles.add(p);
        }
    }

    public void spawnHealEffect(double x, double y, int count) {
        for (int i = 0; i < count && particles.size() < GameConfig.MAX_PARTICLES; i++) {
            Particle p = new Particle(
                x + (random.nextDouble() - 0.5) * 60,
                y + random.nextDouble() * 40,
                Color.LIME, 4 + random.nextDouble() * 6,
                (random.nextDouble() - 0.5) * 30,
                -80 - random.nextDouble() * 120,
                0.8 + random.nextDouble() * 0.7,
                false
            );
            particles.add(p);
        }
    }

    public void spawnLightningBolt(double x1, double y1, double x2, double y2) {
        int segments = 8;
        double dx = (x2 - x1) / segments;
        double dy = (y2 - y1) / segments;
        for (int i = 0; i < segments; i++) {
            double px = x1 + dx * i + (random.nextDouble() - 0.5) * 20;
            double py = y1 + dy * i + (random.nextDouble() - 0.5) * 20;
            Particle p = new Particle(px, py, Color.CYAN, 3, 0, 0, 0.3, false);
            particles.add(p);
            // Branch sparks
            if (random.nextDouble() > 0.5) {
                Particle s = new Particle(px, py, Color.WHITE, 6,
                    (random.nextDouble() - 0.5) * 200, (random.nextDouble() - 0.5) * 200,
                    0.15, false);
                s.isSpark = true;
                particles.add(s);
            }
        }
    }

    public void spawnAura(double x, double y, Color color, int count) {
        for (int i = 0; i < count && particles.size() < GameConfig.MAX_PARTICLES; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 30 + random.nextDouble() * 40;
            Particle p = new Particle(
                x + Math.cos(angle) * radius,
                y + Math.sin(angle) * radius,
                color, 3 + random.nextDouble() * 4,
                Math.cos(angle) * 20,
                -50 - random.nextDouble() * 80,
                0.5 + random.nextDouble() * 0.5,
                false
            );
            particles.add(p);
        }
    }

    // --- Screen Effects ---

    public void triggerShake(double intensity) {
        this.shakeIntensity = Math.max(this.shakeIntensity, intensity);
    }

    public void triggerHitFlash(Color color) {
        this.hitFlashOpacity = 1.0;
        this.hitFlashColor = color;
    }

    public void triggerSlowMo(double duration) {
        this.isSlowMo = true;
        this.slowMoTimer = duration;
    }

    // --- Getters ---

    public double getShakeX() { return shakeX; }
    public double getShakeY() { return shakeY; }
    public boolean isSlowMo() { return isSlowMo; }
    public double getSlowMoFactor() { return isSlowMo ? GameConfig.SLOW_MO_FACTOR : 1.0; }

    public void clear() {
        floatingTexts.clear();
        particles.clear();
        shakeIntensity = 0;
        shakeX = 0;
        shakeY = 0;
        hitFlashOpacity = 0;
        isSlowMo = false;
    }
}
