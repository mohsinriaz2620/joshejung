/**
 * Item.java
 * Abstract base class for all items in the game.
 * Demonstrates abstract class with abstract method.
 * Concrete subclasses: Weapon, Potion, Taweez
 */
public abstract class Item {
    protected String name;
    protected int cost;
    protected String description;

    public Item(String name, int cost, String description) {
        this.name = name;
        this.cost = cost;
        this.description = description;
    }

    public abstract void use(Fighter target);

    public String getName() { return name; }
    public int getCost() { return cost; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name + " (" + cost + "g) - " + description;
    }
}

class Weapon extends Item {
    private int damageBoost;

    public Weapon(String name, int cost, int damageBoost) {
        super(name, cost, "+" + damageBoost + " Attack Power");
        this.damageBoost = damageBoost;
    }

    @Override
    public void use(Fighter target) {
        target.setAttackBonus(target.getAttackBonus() + damageBoost);
    }

    public int getDamageBoost() { return damageBoost; }
}

class Potion extends Item {
    private int healAmount;

    public Potion(String name, int cost, int healAmount) {
        super(name, cost, "Restores " + healAmount + " HP");
        this.healAmount = healAmount;
    }

    @Override
    public void use(Fighter target) {
        target.setHp(target.getHp() + healAmount);
    }

    public int getHealAmount() { return healAmount; }
}

class Taweez extends Item {
    private int defenseBoost;

    public Taweez(String name, int cost, int defenseBoost) {
        super(name, cost, "+" + defenseBoost + " Defense");
        this.defenseBoost = defenseBoost;
    }

    @Override
    public void use(Fighter target) {
        target.setDefenseBonus(target.getDefenseBonus() + defenseBoost);
    }

    public int getDefenseBoost() { return defenseBoost; }
}
