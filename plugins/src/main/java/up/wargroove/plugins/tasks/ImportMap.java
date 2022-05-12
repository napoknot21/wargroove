package up.wargroove.plugins.tasks;


import up.wargroove.core.character.Faction;
import up.wargroove.core.world.*;
import up.wargroove.plugins.Reader;
import up.wargroove.utils.Database;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;

import java.io.File;
import java.util.*;

/**
 * This is a plugin. Its primary tasks is to import a given map in CLI to the local database.
 */
public class ImportMap extends Plugin {
    private static int players = 0;
    private final StringBuilder log = new StringBuilder();
    private final Database db = new Database(getRoot());
    private Reader reader;
    private String[] paths;
    private String biome;

    /**
     * Construct the plugin and set the given arguments.
     *
     * @param args The CLI arguments.
     */
    public ImportMap(String... args) {
        super(args);

    }

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    public void run() throws Exception {
        reader = new Reader(getWorldTexturePath());
        reader.load();
        if (paths == null || paths.length == 0) {
            throw new Exception("The args cannot be empty");
        }

        for (String path : paths) {
            try {
                File file = new File(path);
                if (!file.exists() || file.isDirectory()) {
                    log.append("The specified path must indicate a file (").append(path).append(")").append('\n');
                } else {
                    load(file);
                }
            } catch (Exception e) {
                log.append("An error occurred during the load of ").append(path)
                        .append("(").append(e.getMessage()).append(")");
            }
        }

        if (log.length() != 0) {
            throw new Exception(String.valueOf(log));
        }
    }

    /**
     * Inits the tasks parameters according to the given arguments.
     *
     * @param parameter the CLI arguments.
     */
    void initParameter(String... parameter) {
        if (parameter.length != 2) {
            return;
        }
        switch (parameter[0]) {
            case "paths":
                setPaths(parameter[1]);
                break;
            case "biome":
                setBiome(parameter[1]);
                break;
            default:
                Log.print(Log.Status.ERROR, parameter[0] + "is ignored");
        }
    }

    private File getRoot() {
        return new File("db/wargroove.db");
    }

    //option = "paths", description = "List of file paths that point to a map that needed to be loaded"
    private void setPaths(String args) {
        this.paths = args.split(";");
    }

    //(option = "biome", description = "Biome of the world")
    private void setBiome(String biome) {
        this.biome = biome;
    }

    private String getWorldTexturePath() {
        return "core/assets/data/gui/world/grass.atlas";
    }

    /**
     * Load the given file into the right database collection.
     *
     * @param file the file that contain the map to import.
     * @throws Exception if something went wrong
     */
    private void load(File file) throws Exception {
        if (!file.getName().split("\\.")[1].equals("csv")) {
            throw new Exception("The file must be a CSV file");
        }
        WorldProperties properties = traduction(file);
        World world = new World(properties);
        if (!world.save(db, players + "_players")) {
            throw new Exception("Something went wrong during the save");
        }
    }

    /**
     * Translate from a csv file the data needed by the world.
     *
     * @param file The csv file.
     * @return The generated world properties.
     * @throws Exception if a problem occurred during the generation (Tile atlas index is out of range)
     */
    private WorldProperties traduction(File file) throws Exception {
        Scanner scanner = new Scanner(file);
        int width = 0;
        int height = 0;
        ArrayList<Faction> factions = new ArrayList<>(Arrays.asList(Faction.values()));
        ArrayList<Tile> array = new ArrayList<>();
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                height++;
                String[] data = line.split(",");
                width = data.length;
                for (String d : data) {
                    Tile tile = reader.get(Integer.parseInt(d));
                    checkPlayers(tile, factions);
                    array.add(tile);
                }
            }
        } finally {
            scanner.close();
        }
        return generateProperties(array, width, height, file);
    }

    /**
     * Check if the number of player is correct and if each player have at most one stronghold.
     *
     * @param tile     The tile that will be checked.
     * @param factions The plugin factions' list
     * @throws Exception if the configuration is wrong.
     */
    private void checkPlayers(Tile tile, ArrayList<Faction> factions) throws Exception {
        if (factions.isEmpty()) throw new Exception("There is an issue with the structure configuration");
        if (tile.entity.isPresent() && tile.entity.get() instanceof Stronghold) {
            if (!factions.remove(tile.entity.get().getFaction())) {
                throw new Exception("There is an issue with the structure configuration");
            }
            players++;
        }
    }

    /**
     * Generates the World properties.
     *
     * @param array  the world terrain.
     * @param width  the world terrain width.
     * @param height the world terrain height.
     * @param file   the csv file.
     * @return The world properties corresponding to the file.
     */
    private WorldProperties generateProperties(ArrayList<Tile> array, int width, int height, File file) {
        WorldProperties properties = new WorldProperties();
        properties.dimension = new Pair<>(width, height);
        properties.terrain = buildTerrain(array, properties.dimension);
        properties.setDescription("Map imported from " + file.getName() + " on " + Calendar.getInstance().getTime());
        properties.setName(file.getName().split("\\.")[0]);
        properties.setBiome((biome == null) ? Biome.GRASS : Biome.valueOf(biome.toUpperCase(Locale.ROOT)));
        return properties;
    }

    /**
     * Generate the properties' terrain.
     *
     * @param array     The properties' terrain data.
     * @param dimension the dimension of the properties' terrain
     * @return The generated terrain.
     */
    private Tile[] buildTerrain(ArrayList<Tile> array, Pair<Integer, Integer> dimension) {
        Tile[][] terrain = new Tile[dimension.first][dimension.second];
        int arrI = 0;
        for (int j = dimension.first - 1; j >= 0; j--) {
            for (int k = 0; k < dimension.second; k++, arrI++) {
                terrain[j][k] = array.get(arrI);
            }
        }
        Tile[] tiles = new Tile[array.size()];
        int tilesI = 0;
        for (int j = 0; j < dimension.first; j++) {
            for (int k = 0; k < dimension.second; k++, tilesI++) {
                tiles[tilesI] = terrain[j][k];
            }
        }
        return tiles;
    }
}
