package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;


public class Commander extends Character {


	public Commander() {
		this("", Faction.OUTLAWS);
	}

    public Commander (String name, Faction faction) {
        super(name, Type.COMMANDER, faction);
        initialize();
    }

    @Override
    public void initialize() {

	    super.movement = Movement.WALKING;
	    super.movRange = 4;

	    stats.attack = 20;
	    stats.health = 100;
	    stats.range = 1;
	    stats.cost = 500;

		setHealth(stats.health);

    }


}
