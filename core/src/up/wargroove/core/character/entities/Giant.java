package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Giant extends Character {

    public Giant () {
        this("", Faction.OUTLAWS);
    }

    public Giant (String name, Faction faction) {
        super(name, Type.GIANT, faction);
        initialize();
    }

    @Override
    public void initialize() {
        super.movement = Movement.WALKING;
        super.movRange = 5;

        stats.attack = 20;
        stats.health = 100;
        stats.range = 1;
        stats.cost = 1200;
    }

}
