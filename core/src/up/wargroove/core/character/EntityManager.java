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

	private EntityManager() {
	
		entitySubClasses = new Vector<>();

	}

	public boolean load() {

		/*
		 * Chargement des classes primitives
		 * d'entité
		 */

		var types = Entity.Type.values();
		boolean status = true;

		for(Entity.Type t : types) {

			try {

				String typeStr = t.toString();

				String fmt = packageURI + 
					"." + 
					typeStr.charAt(0) + 
					typeStr.substring(1, typeStr.length()).toLowerCase();
	
				Class<Entity> subClass = (Class<Entity>) Class.forName(fmt);
				entitySubClasses.add(subClass);

				/*
				 * Exemple d'instanciation de la classe
				 */
	
				Entity e = instantiate(subClass);
				if(e == null) throw new Exception();

				Movement mov = e.getType().movement;

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

		/*
		 * Chargement des classes additives
		 * d'entité
		 */	

		return status;

	}

	public static Entity instantiate(Class<Entity> ce) {

		try {

			Constructor<Entity> constructor = ce.getDeclaredConstructor();
			return constructor.newInstance();	
		
		} catch(Exception e) {

			Log.print(Log.Status.ERROR, "Erreur lors de l'instantiation!");

		}

		return null;
	}

	public static EntityManager getInstance() {
	
		return instance;

	}

}
