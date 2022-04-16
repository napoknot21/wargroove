package up.wargroove.core.world;

import java.util.Queue;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.entities.Commander;

public class Player {

	private Queue<Entity> entities;
	private Faction faction;
	private String name;
	private int money;
	private int income;
	private final float ratio;


	public Player(Faction faction, float ratio) {
	
		entities = new LinkedList<>();
		this.faction = faction;
		this.money = (int)(1000 * ratio);
		addEntity(new Commander());
		this.ratio = ratio;

	}

	/*
	 * Gestion des personnages à disposition
	 * du joueur
	 */

	public void addEntity(Entity character)
	{
		if (character instanceof Structure) {
			addIncome(((Structure)character).getBonus());
		}
		entities.add(character);
	}

	public void removeEntity(Entity character) {
	
		entities.remove(character);

	}

	public boolean hasNext() {

		return !entities.element().isExhausted();

	}

	@Null
	public Entity next() {
		if(entities.isEmpty() || entities.peek().isExhausted()) return null;

		Entity entity = entities.poll();
		entities.add(entity);

		return entity;

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
		money = Math.max(money + income, 0);
		return isCommanderAlive;

	}

	public Faction getFaction() {
		return faction;
	}

	@Null
	public Entity nextPlayableEntity() {
		int size = entities.size();
		for (int i = 0; i<size; i++) {
			Entity e = next();
			if (e != null) {
				return e;
			}
		}
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getMoney() {
		return money;
	}

	public void addIncome(int income){
		this.income += (income*ratio);
	}

	public int getIncome() {
		return income;
	}

	public void buy(int amount) {
		this.money -= amount;
	}
}
