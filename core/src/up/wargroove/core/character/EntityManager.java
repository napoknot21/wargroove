package up.wargroove.core.character;

import up.wargroove.core.world.Recruitment;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.Log;

/*
 * Gestionnaire des classes d'entités
 * en singleton
 */

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
	public boolean load() {

		var types = Entity.Type.values();
		boolean status = true;

		for(Entity.Type t : types) {

			try {

				String typeStr = t.toString();

				String fmt = packageURI + 
					"." + 
					typeStr.charAt(0) + 
					typeStr.substring(1, typeStr.length()).toLowerCase();

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
						Recruitment.landEntityClasses.add(subClass);
				   		break;

					case AIR:
						Recruitment.airEntityClasses.add(subClass);
						break;

					case SEA:
						Recruitment.navalEntityClasses.add(subClass);
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
	public static Entity instantiate(Class<Entity> ce) {

		try {

			Constructor<Entity> constructor = ce.getDeclaredConstructor();
			return constructor.newInstance();	
		
		} catch(Exception e) {

			e.printStackTrace();
			Log.print(Log.Status.ERROR, "Erreur lors de l'instantiation!");

		}

		return null;
	}

	/**
	 * getter pour l'instance de Entity Manager
	 * @return
	 */
	public static EntityManager getInstance() {
	
		return instance;

	}

}
