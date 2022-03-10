package up.wargroove.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Base64;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonString;
import javax.json.JsonWriter;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class Database {

	private File source;
	private String name;

	private JsonObject jsonData;
	private Map<String, DbObject> collections;
	private DbObject collection;

	private JsonBuilderFactory factory;

	private Base64.Encoder encoder;
	private Base64.Decoder decoder;

	public Database(File source) {

		this.source = source;
		String sourceName = source.getName();

		name = sourceName.substring(0, sourceName.length() - 3);
		collections = new HashMap<>();
		factory = Json.createBuilderFactory(null);

		encoder = Base64.getEncoder();
		decoder = Base64.getDecoder();

		read();
		int colls = findCollections();

		Log.print(colls + " collections trouvées");

	}

	/**
	 * Séléction de la collection.
	 *
	 * @param String nom de la collection
	 */

	public void selectCollection(String name) {

		collection = collections.get(name);

	}

	private int findCollections() {
		if (jsonData == null) {
			return -1;
		}
		Set<String> keys = jsonData.keySet();
		int kc = 0;

		for(String key : keys) {

			DbObject dbo = new DbObject(jsonData.getJsonObject(key));
			collections.put(key, dbo);
			Log.print("+ " + key);
			kc++;

		}

		return kc;

	}

	/**
	 * Effectue la requête sur la collection choisie.
	 *
	 * @param String la requête séparée par des / pour les différents niveaux de profondeur.
	 * @return la valeur en chaîne de caractères de l'objet
	 */

	public DbObject get(String req) {

		String [] tags = req.split("/");
		DbObject parent = depthRequest(collection, tags);

		String lTag = tags[tags.length - 1];
		
		return parent.get(lTag);	

	}

	/**
	 * Créée une collection, ne requiert pas de
	 * flush() pour l'écriture.
	 *
	 * @param String le nom de la nouvelle collection
	 */

	public void createCollection(String collectionName) {

		if(collections.containsKey(collectionName)) {

			Log.print(Log.Status.WARN, "Cette collection existe déjà!");
			return;

		}

		DbObject coll = new DbObject();	
		collections.put(collectionName, coll);

		write();

	}

	/**
	 * Supprime une collection existante, ne requiert pas de
	 * flush() pour l'écriture.
	 *
	 * @param String le nom de la collection
	 */

	public void dropCollection(String collectionName) {

		if(collections.containsKey(collectionName)) {
		
			collections.remove(collectionName);
			write();
		
		}

	}

	/**
	 * Met à jour la valeur de la variable à la collection courrante
	 *
	 * @param String la requête de la variable
	 * @param T nouvelle valeur
	 * @return état de la requête
	 */

	public <T> boolean update(String req, T data) {

		String [] tabs = req.split("/");
		DbObject parent = depthRequest(collection, tabs);

		if(parent == null) return false; 

		parent.put(tabs[tabs.length - 1], data);
		
		return true;

	}

	/**
	 * Insert un JsonObject dans la collection courrante
	 */

	public <T> boolean insert(String req, T data) {
	
		return update(req, data);

	}

	/**
	 * Procède à l'écriture des dernières modifications sur la base
	 */

	public void flush() {

		write();

	}

	/*
	 * Reconstruit l'objet Json depuis chacun de ses éléments.
	 */

	private JsonObjectBuilder buildStream(DbObject root) {

		JsonObjectBuilder builder = factory.createObjectBuilder();	
		for(String k : root.keySet()) {

			DbObject value = root.get(k);

			if(!value.isData()) {

				builder.add(k, buildStream((DbObject) value));

			} else
				builder.add(k, value.get());

		}

		return builder;

	}

	/*
	 * Reconstruit la base toute entière à partir des collections
	 */

	private JsonObject buildStream() {
		
		JsonObjectBuilder root = factory.createObjectBuilder();
		
		for(String collectionName : collections.keySet()) {

			DbObject c = collections.get(collectionName);
			root.add(collectionName, buildStream(c));

		}	

		return root.build();
	}

	private void write() {

		JsonObject data = buildStream();
		byte [] encData  = encoder.encode(data.toString().getBytes());
		
		Log.print("Builder: " + data.toString());	

		try {	

			OutputStream os = new FileOutputStream(source);
			//JsonWriter jwriter = Json.createWriter(os);
	
			//jwriter.writeObject(data);

			//jwriter.close();
			
			os.write(encData);
			os.flush();
			os.close();

		} catch (Exception e) {

			Log.print(Log.Status.ERROR, "Erreur lors de l'écriture des données");

		}

	}

	private void read() {

		try {
	
			byte [] data = new byte[(int) source.length()];

			InputStream is = new FileInputStream(source);
			
			is.read(data);	
			is.close();

			String decData = new String(decoder.decode(data));
			JsonReader jreader = Json.createReader(new StringReader(decData));

			jsonData = jreader.readObject();

			jreader.close();	

		} catch(FileNotFoundException e) {
		
			Log.print(Log.Status.ERROR, "La source est corrompue!");

		} catch(IOException e) {}

	}

	/*
	 * Recherche en profondeur dans la base. Les requêtes sont de 
	 * la forme:
	 * 	
	 * 	ma/mb/mc
	 */

	private DbObject depthRequest(DbObject root, String [] tags) {	
	
		DbObject objPointer = root;	

		for(int k = 0; k < tags.length - 1; k++) {

			if(objPointer == null) break;
			objPointer = objPointer.get(tags[k]);

		}
	
		return objPointer;

	}

	public String getName() {

		return name;

	}

}
