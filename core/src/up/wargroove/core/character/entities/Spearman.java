package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Spearman extends Character {

    /**
     * Constructor for Spearman
     */
    public Spearman () {
        this("", Faction.OUTLAWS);
    }


    /**
     * Constructor for Spearman
     * @param name character name
     * @param faction character faction
     */
    public Spearman (String name, Faction faction) {
        super(name, Type.SPEARMAN, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 3;
        stats.attack = 20;
        stats.health = 100;
        stats.range = 1;
        stats.cost = 250;
    }

}