package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Mage extends Character {

    /**
     * Constructor for Mage
     */
    public Mage () {
        this("", Faction.OUTLAWS);
    }


    /**
     * Constructor for Mage
     * @param name character name
     * @param faction character faction
     */
    public Mage (String name, Faction faction) {
        super(name, Type.MAGE, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 5;
        stats.attack = 20;
        stats.health = 100;
        stats.range = 1;
        stats.cost = 400;
    }

}