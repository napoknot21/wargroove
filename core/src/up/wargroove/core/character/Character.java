package up.wargroove.core.character;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import up.wargroove.core.character.entities.*;
import up.wargroove.utils.Pair;

public abstract class Character extends Entity {

    protected String name;
    private final static String pathDamageMatrix = "./core/src/up/wargroove/core/character/damageMatrix/";
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

    public void attack (Character ch) {
        if (ch == null) return;
        Pair<Integer,Integer> attacks = this.getAttacksValues(ch);
        Pair<Integer,Integer> defends = ch.getDefendsValues(this);
        ch.setHealth(ch.getHealth() - attacks.first + defends.first);
    }

    protected Map<String, Map<String, List<Integer>>> readDamageMatrixValues () {
        try {
            String name = this.getType().toString().toLowerCase();
            File f = new File(pathDamageMatrix + name + ".yml");
            return new Yaml().load(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, List <Integer> > getAttacksAndDefendsValues (Character ch) {
        var data = this.readDamageMatrixValues();
        if (data == null || ch == null) return null;
        return data.get(ch.getType().name().toUpperCase());
    }

    public Pair<Integer, Integer> getAttacksValues (Character ch) {
        var data = this.getAttacksAndDefendsValues(ch);
        if (data == null || ch == null) return new Pair<>(100,20);
        List <Integer> attacks = data.get("attacks");
        if (attacks.size() != 2) return new Pair<>(10,10);;
        return new Pair<Integer,Integer>(attacks.get(0),attacks.get(1));
    }

    public Pair<Integer,Integer> getDefendsValues (Character ch) {
        var data = this.getAttacksAndDefendsValues(ch);
        if (data == null || ch == null) return new Pair<>(0,10);;
        List <Integer> defends = data.get("defends");
        if (defends.size() != 2) return new Pair<>(10,10);;
        return new Pair<Integer,Integer>(defends.get(0),defends.get(1));
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
