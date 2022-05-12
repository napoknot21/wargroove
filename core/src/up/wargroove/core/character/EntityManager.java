package up.wargroove.core.character;

import com.badlogic.gdx.utils.Null;
import up.wargroove.core.world.Recruitment;
import up.wargroove.utils.Log;

import java.lang.reflect.Constructor;
import java.util.Vector;

public class EntityManager {

	public static EntityManager instance = new EntityManager();
	public static Vector<Class<Entity>> entitySubClasses;

	private static String packageURI = "up.wargroove.core.character.entities";

	/**
	 * Constructor for entityManager
	 */
	private EntityManager() {
		entitySubClasses = new Vector<>();
	}


	/**
	 * Load primitives and additives structures of entity by reflexion
	 * @return true if all loaded successfully or false (exception) else null
	 */
	@SuppressWarnings("unchecked")
	public boolean load() {

		var types = Entity.Type.values();
		boolean status = true;

		for(Entity.Type t : types) {
			if (t.equals(Entity.Type.COMMANDER) || t.equals(Entity.Type.STRUCTURE)) {
				continue;
			}
			try {
				String typeStr = t.toString();
				String fmt = packageURI + 
					"." + 
					typeStr.charAt(0) + 
					typeStr.substring(1).toLowerCase();

				Log.print("Refléxion sur " + fmt);

				Class<Entity> subClass = (Class<Entity>) Class.forName(fmt);
				entitySubClasses.add(subClass);

				/*
				 * Example of instantiation of class
				 */
				Entity e = instantiate(subClass);
				if(e == null) throw new Exception();
				Movement mov = e.movement;
				switch(mov.component) {
					case GROUND: 
						Recruitment.landEntityClasses.add(e);
				   		break;
					case AIR:
						Recruitment.airEntityClasses.add(e);
						break;
					case SEA:
						Recruitment.navalEntityClasses.add(e);
						break;
				}
				Log.print(Log.Status.SUCCESS, "Ajout effectué avec succès!");
			} catch (Exception e) {
				Log.print(Log.Status.ERROR, "Erreur au chargement des personnages primitifs!");
				status = false;
			}
		}
		return status;
	}


	/**
	 * Transform the class extended by entity in an Entity object
	 * @param ce the class searched by reflexion
	 * @return Entity object
	 */
	public static Entity instantiate(Class<? extends Entity> ce, String name, Faction faction) {
		try {
			Constructor<? extends Entity> constructor = ce.getDeclaredConstructor(String.class, Faction.class);
			return constructor.newInstance(name,faction);
		} catch(Exception e) {
			e.printStackTrace();
			Log.print(Log.Status.ERROR, "Erreur lors de l'instantiation!");
		}
		return null;
	}


	@Null
	public static Entity instantiate(Entity.Type type, String name, Faction faction) {
		for (Class<? extends Entity> c : entitySubClasses) {
			if (c.getSimpleName().equalsIgnoreCase(type.toString())) {
				return instantiate(c,name,faction);
			}
		}
		return null;
	}


	/**
	 * Transform a class in Entity object
	 * @param ce class searched by reflexion
	 * @return un Object Entity
	 */
	public static Entity instantiate(Class<? extends Entity> ce) {
		return instantiate(ce,"",Faction.OUTLAWS);
	}


	/***************** setters and getters *****************/

	public static EntityManager getInstance() {
		return instance;
	}

}
