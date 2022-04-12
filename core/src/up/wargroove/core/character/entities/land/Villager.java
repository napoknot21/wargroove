package up.wargroove.core.character.entities.land;

import up.wargroove.core.character.entities.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Villager extends Character {

	public Villager() {

		this("", Faction.OUTLAWS);

	}

    public Villager(String name, Faction faction) {
        
	    super(name, Type.VILLAGER, faction);
	    initialize();

    }

    @Override
    public void initialize()
    { 
	    super.movRange = 4;
	    super.movement = Movement.WALKING;

	    stats.attack = 0;
	    stats.defense = 10;
	    stats.health = 100;
	    stats.capture = true;
	    stats.sight = 2;
	    stats.range = 0;
	    stats.cost = 50;

		super.initialize();
    }

}
