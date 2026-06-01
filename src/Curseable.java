/**
 * Curseable.java
 * Interface for characters that can apply and receive curses.
 * Demonstrates interface with multiple method contracts.
 */
public interface Curseable {
    void applyCurse(Fighter target, ParticleSystem particles);
    void removeCurse();
    boolean isCursed();
    int getCurseDamagePerSecond();
}
