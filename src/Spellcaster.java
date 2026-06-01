/**
 * Spellcaster.java
 * Interface for characters that can cast magical spells.
 * Demonstrates interface usage in OOP - behavioral contract.
 */
public interface Spellcaster {
    void castSpell(Fighter target, ParticleSystem particles, HUD hud);
    int getSpellDamage();
    String getSpellName();
}
