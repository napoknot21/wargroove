package up.wargroove.utils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class DBEngine {

	private static final DBEngine instance = new DBEngine();

	private boolean connectionStatus;
	private String root;

	private Map<String, Database> databases;

	private DBEngine() {

		this(Constants.DEFAULT_DB_ROOT);

	}

	private DBEngine(String root) {
	
		this.root = root;
		connectionStatus = false;

		databases = new HashMap<>();

	}

	public boolean connect() {

		if(connectionStatus) {

			Log.print("La base est déjà connectée au flux!");
			return false;

		}

		try {

			Log.print("Recherche des bases ...");

			File rootF = new File(root);
			Log.print(rootF.getAbsolutePath());
			var databaseFiles = depthSearch(rootF);

			for(File file : databaseFiles) {

				Database database = new Database(file);
				databases.put(database.getName(), database);

				Log.print(Log.Status.SUCCESS, database.getName());

			}	

		} catch(IOException e) {

			Log.print(Log.Status.ERROR, "Connexion interrompue!");

		}

		return (connectionStatus = true);

	}

	public boolean disconnect() {

		if(!connectionStatus) {

			Log.print("Aucun base n'a été chargée!");
			return false;

		}

		databases.clear();
		
		return !(connectionStatus = false);

	}

	public Database getDatabase(String name) {

		return databases.get(name);

	}

	private Vector<File> depthSearch(File root) throws IOException {

		Vector<File> files = new Vector<>();
		if(!root.isDirectory()) return files;

		File [] children = root.listFiles();

		for(File child : children) {

			if(child.isFile()) {

				String name = child.getName();
				if(name.endsWith(".db")) files.add(child);

				continue;

			}

			files.addAll(depthSearch(child));

		}

		return files;

	}

	public static DBEngine getInstance() {

		return instance;

	}

}
