package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Archer extends Character {

    public Archer () {
        this("", Faction.OUTLAWS);
    }

    public Archer (String name, Faction faction) {
        super(name, Type.ARCHER, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 3;

        stats.attack = 20;
        stats.defense = 20;
        stats.health = 100;
        stats.capture = true;
        stats.sight = 2;
        stats.range = 3;
        stats.cost = 500;
    }

}
