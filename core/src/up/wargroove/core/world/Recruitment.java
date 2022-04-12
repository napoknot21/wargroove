package up.wargroove.core.world;

import up.wargroove.core.character.EntityManager;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.entities.Villager;

import java.util.Vector;
import java.util.Optional;

public class Recruitment extends Structure {

	public static Vector<Class<? extends Entity>> 	landEntityClasses  = new Vector<>(),
						navalEntityClasses = new Vector<>(),
						airEntityClasses   = new Vector<>();

	public enum Type {

		BARRACKS,
		TOWER,
		PORT

	}

	private Faction faction;
	private Type type;
	private Vector<Class<? extends Entity>> current;

	public Recruitment(Type type, Faction faction) {

		super(Structure.Type.RECRUITMENT, faction);
		this.type = type;

	}

	public void initialize() {

		switch((Type) type) {

			case BARRACKS:
				current = landEntityClasses;
				current.add(Villager.class);
				break;

			case TOWER:
				current = navalEntityClasses;
				break;

			case PORT:
				current = airEntityClasses;
				break;

			default:
				current = null;
				break;
		}

	}

	public Vector<Class<? extends Entity>> trainableEntityClasses() {

		return current;

	}

	public Optional<Entity> buy(Class<? extends Entity> what, double m, String name, Faction faction) {

		if(current == null) return Optional.empty();
	
		Entity entity = EntityManager.instantiate(what,name,faction);
		return Optional.of(entity);

	}



}
