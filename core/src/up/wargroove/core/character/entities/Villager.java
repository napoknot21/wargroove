package up.wargroove.core.character.entities;

import up.wargroove.core.character.Character;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;

public class Villager extends Character {

	/**
	 * Constructor for Villager
	 */
	public Villager() {
		this("", Faction.OUTLAWS);
	}


	/**
	 * Constructor for Villager
	 * @param name character name
	 * @param faction character faction
	 */
    public Villager(String name, Faction faction) {
	    super(name, Type.VILLAGER, faction);
	    initialize();
    }

    @Override
    public void initialize() {
	    super.movRange = 4;
	    super.movement = Movement.WALKING;
	    stats.attack = 0;
	    stats.health = 100;
	    stats.range = 0;
	    stats.cost = 50;
		super.initialize();
    }

}
