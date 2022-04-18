package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Mage extends Character {

    public Mage () {
        this("", Faction.OUTLAWS);
    }

    public Mage (String name, Faction faction) {
        super(name, Type.SPEARMAN, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 5;

        stats.attack = 20;
        stats.defense = 20;
        stats.health = 100;
        stats.capture = true;
        stats.sight = 2;
        stats.range = 1;
        stats.cost = 400;
    }

}
