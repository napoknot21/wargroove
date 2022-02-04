package up.wargroove.core.character;

public class Stats {


    private double health; //up to 100
    private double attack;
    private double defense; //pourcentage 0 to 100
    private int sight;
    private Movement movement;

    public Stats (double health, double attack, double defense, int sight, Movement movement) {
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.sight = sight;
        this.movement = movement;
    }

    public double getHealth() {
        return health;
    }

    public double getAttack() {
        return attack;
    }

    public double getDefense() {
        return defense;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
