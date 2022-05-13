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
import java.util.*;
import javax.annotation.Nullable;
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

	/**
	 * Constructor for database
	 * @param source File src
	 */
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
	 * selection of the selection
	 * @param name collection name
	 */
	public void selectCollection(String name) {

		collection = collections.get(name);

	}

	/**
	 * find of the selection
	 * @return -1 if jsonData is null, kc = collection keys then
	 */
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
	 * Performs the query on the chosen collection.
	 * @param req the query separated by / for the different depth levels.
	 * @return the string value of the object
	 */
	public DbObject get(String req) {

		String [] tags = req.split("/");
		DbObject parent = depthRequest(collection, tags);

		String lTag = tags[tags.length - 1];
		
		return parent.get(lTag);	

	}



	/**
	 * Creates a collection, does not require flush() for writing.
	 * @param collectionName new collection name
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
	 * Removes an existing collection, does not require flush() for writing.
	 * @param collectionName collection name
	 */
	public void dropCollection(String collectionName) {

		if(collections.containsKey(collectionName)) {
		
			collections.remove(collectionName);
			write();
		
		}

	}


	/**
	 * Update the value of the variable to the current collection
	 * @param req the variable query
	 * @param data data
	 * @param <T> new value
	 * @return query status
	 */
	public <T> boolean update(String req, T data) {

		String [] tabs = req.split("/");
		DbObject parent = depthRequest(collection, tabs);

		if(parent == null) return false; 

		parent.put(tabs[tabs.length - 1], data);
		
		return true;

	}


	/**
	 * Insert a JsonObject into the current collection
	 */
	public <T> boolean insert(String req, T data) {
	
		return update(req, data);

	}

	public List<String> getKeys() {
		if(collection == null) {
			return null;
		}
		return new LinkedList<>(collection.keySet());
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

				builder.add(k, buildStream(value));

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
