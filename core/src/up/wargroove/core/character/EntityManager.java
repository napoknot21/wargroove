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
	 * constructeur
	 */
	private EntityManager() {
	
		entitySubClasses = new Vector<>();

	}

	/**
	 * Chargement des structures primitives et additives d'entity par réflexions
	 * @return true si tout a été bien chargé, false (si il y a une exception) et null sinon
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
				 * Exemple d'instanciation de la classe
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
	 * Transforme la classe Entity passé en paramètre en un objet Entity
	 * @param ce classe cherché par reflexion
	 * @return un objet entity
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
	 * Transforme la classe Entity passé en paramètre en un objet Entity
	 * @param ce classe cherché par reflexion
	 * @return un objet entity
	 */
	public static Entity instantiate(Class<? extends Entity> ce) {
		return instantiate(ce,"",Faction.OUTLAWS);
	}

	/**
	 * getter pour l'instance de Entity Manager
	 * @return
	 */
	public static EntityManager getInstance() {
	
		return instance;

	}

}
