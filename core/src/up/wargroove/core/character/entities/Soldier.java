package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Soldier extends Character {

    /**
     * Constructor for Soldier
     */
    public Soldier () {
        this("", Faction.OUTLAWS);
    }


    /**
     * Constructor for Soldier
     * @param name character name
     * @param faction character faction
     */
    public Soldier (String name, Faction faction) {
        super(name, Type.SOLDIER, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 4;
        stats.attack = 20;
        stats.health = 100;
        stats.range = 1;
        stats.cost = 100;
    }

}