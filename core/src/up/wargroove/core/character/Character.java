package up.wargroove.core.character;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;

import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.core.character.entities.*;
import up.wargroove.utils.Pair;


public abstract class Character extends Entity {

    protected String name;
    protected Stats stats;

    /**
     * Constructeur pour Character
     *
     * @param name nom du personnage
     * @param faction Faction du personnage
     * @param type Type d'unité du personnage
     */
    public Character(String name, Entity.Type type, Faction faction) {

        super(type, faction);
        this.name = name;
        stats = new Stats();

    }

    public Faction getFaction() {
        return super.getFaction();
    }

    /**
     * getter et setters pour character
     */
    @Override
    public int getCost() {
        return this.stats.cost;
    }

    public Stats getStats() {
        return stats;
    }

    @Override
    public double getHealth() {
        return this.stats.health;
    }

    public int getRange() {

	    return stats.range;

    }

    public double getAttack() {
        return this.stats.health;
    }

    public double getDefense() {
        return this.stats.defense;
    }

    @Override
    public void setHealth(double health) {
        this.stats.health = health;
        super.setHealth(health);
    }

    public void setAttack(double attack) {
        this.stats.attack = attack;
    }

    public void setDefense(double defense) {
        this.stats.defense = defense;
    }

    public void setCapture(boolean capture) {
        this.stats.capture = capture;
    }

    public void setSight(int sight) {
        this.stats.sight = sight;
    }

    public void setRange(int range) {
        this.stats.range = range;
    }

    public void setCost(int cost) {
        this.stats.cost = cost;
    }

    /**
     * Classe interne contenant les stats du personnage
     */
    protected static class Stats {

        public double health; //Vie du personnage
        public double attack; //Attaque du personnage
        public double defense; //Pourcentage (entre 0 et 100) qui réduit l'attaque du personnage attaquant
        public boolean capture; //Capacité à capture un village (true = oui, false = no)
        public int sight; //Vue du personnage
        public int range; //Rang d'attaque du personnage
        public int cost; //Prix du personnage (monnaie du jeu)

        /**
         * Constructeur pour les stats d'un personnage
         */
        public Stats() {
            this.health = 0;
            this.attack = 0;
            this.defense = 0;
            this.capture = false;
            this.sight = 0;
            this.range = 0;
            this.cost = 0;
        }

    }

}
