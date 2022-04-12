package up.wargroove.core.world;

import java.util.Queue;
import java.util.LinkedList;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Entity;

public class Player {

	private Queue<Entity> entities;
	private Faction faction;

	public Player(Faction faction) {
	
		entities = new LinkedList<>();
		this.faction = faction;

	}

	/*
	 * Gestion des personnages à disposition
	 * du joueur
	 */

	public void addEntity(Entity character)
	{
		entities.add(character);
	}

	public void removeEntity(Entity character) {
	
		entities.remove(character);

	}

	public boolean hasNext() {

		return !entities.element().isExhausted();

	}

	public boolean next() {

		if(entities.peek().isExhausted()) return false;

		Entity entity = entities.poll();
		entities.add(entity);

		return true;

	}

	public Entity peekEntity() {

		return entities.peek();

	}

	/**
	 * Passe le tour du joueur
	 *
	 * @return faux si le commandant n'existe plus et vrai sinon
	 */
	public boolean nextTurn() {

		boolean isCommanderAlive = false;

		for(Entity c : entities) {
		
			c.nextTurn();
			isCommanderAlive |= c.getType() == Entity.Type.COMMANDER;
		
		}

		return isCommanderAlive;

	}

	public Faction getFaction() {
		return faction;
	}
}
