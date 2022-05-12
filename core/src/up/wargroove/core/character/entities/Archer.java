package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Archer extends Character {

    /**
     * Constructor for archer
     */
    public Archer () {
        this("", Faction.OUTLAWS);
    }


    /**
     * constructor for Archer
     * @param name character name
     * @param faction character faction
     */
    public Archer (String name, Faction faction) {
        super(name, Type.ARCHER, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 3;
        stats.attack = 20;
        stats.health = 100;
        stats.range = 3;
        stats.cost = 500;
    }

}