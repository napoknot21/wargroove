package up.wargroove.core.character;

/*
 * Gestionnaire des classes d'entités
 * en singleton
 */

import java.util.Vector;

public class EntityManager {

	public static EntityManager instance = new EntityManager();
	public static Vector<Entity> entitySubClasses;

	private EntityManager() {
	
		entitySubClasses = new Vector<>();

	}

	public boolean load() {

		return false;

	}

	public static EntityManager getInstance() {
	
		return instance;

	}

}
