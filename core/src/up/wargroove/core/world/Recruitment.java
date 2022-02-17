package up.wargroove.core.world;

import up.wargroove.core.character.EntityManager;
import up.wargroove.core.character.Entity;
import java.util.Vector;
import java.util.Optional;

public class Recruitment extends Structure {

	public static Vector<Class<Entity>> 	landEntityClasses  = new Vector<>(),
						navalEntityClasses = new Vector<>(),
						airEntityClasses   = new Vector<>();

	public enum Type implements Structure.Type {

		BARRACKS,
		TOWER,
		PORT

	}

	private Vector<Class<Entity>> current;

	public Recruitment(Type type) {

		super(type);	

		switch((Type) type) {

			case BARRACKS:
				current = landEntityClasses;
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

	public Vector<Class<Entity>> trainableEntityClasses() {

		return current;

	}

	public Optional<Entity> buy(Class<Entity> what, double m) {

		if(current == null) return Optional.empty();
	
		Entity entity = EntityManager.instantiate(what);
		return Optional.of(entity);

	}



}
