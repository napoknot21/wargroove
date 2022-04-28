package up.wargroove.plugins.tasks;


import up.wargroove.core.world.*;
import up.wargroove.plugins.Reader;
import up.wargroove.utils.Database;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

/**
 * This is a plugin. Its primary tasks is to import a given map in CLI to the local database.
 */
public class ImportMap {
    private final StringBuilder log = new StringBuilder();
    private final Database db = new Database(getRoot());
    private String[] paths;
    private String biome;
    private final Reader reader;
    private static int players = 0;

    /**
     * Construct the plugin and set the given arguments.
     *
     * @param args The CLI arguments.
     */
    public ImportMap(String... args) throws FileNotFoundException {
        reader = new Reader(getWorldTexturePath());
        reader.load();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parameter = arg.substring(2).split("=");
                initParameter(parameter);
            }
        }
    }

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    public void run() throws Exception {
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
            throw new Exception(log.toString());
        }
    }

    private void initParameter(String... parameter) {
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
        return "core/assets/data/sprites/world/grass.atlas";
    }

    private void load(File file) throws Exception {
        if (!file.getName().split("\\.")[1].equals("csv")) {
            throw new Exception("The file must be a CSV file");
        }
        WorldProperties properties = traduction(file);
        World world = new World(properties);
        System.out.println(players);
        System.out.println(properties.terrain.length);
        db.selectCollection(players+"p");
        if (!world.save(db)) {
            throw  new Exception("Something went wrong during the save");
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
        ArrayList<Tile> array = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            height++;
            String[] data = line.split(",");
            width = data.length;
            for (String d : data) {
                Tile tile = reader.get(Integer.parseInt(d));
                if (tile.entity.isPresent() && tile.entity.get() instanceof Stronghold) {
                    players++;
                }
                array.add(tile);
            }
        }
        return generateProperties(array, width, height, file);
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

    private Tile[] buildTerrain(ArrayList<Tile> array, Pair<Integer, Integer> dimension) {
        Tile[][] terrain = new Tile[dimension.first][dimension.second];
        int arrI = 0;
        for (int j = dimension.first - 1; j>= 0; j--) {
            for (int k = 0; k < dimension.second; k++, arrI++) {
                terrain[j][k] = array.get(arrI);
            }
        }
        Tile[] tiles = new Tile[array.size()];
        int tilesI = 0;
        for (int j = 0; j< dimension.first; j++) {
            for (int k = 0; k<dimension.second; k++, tilesI++) {
                tiles[tilesI] = terrain[j][k];
            }
        }
        return tiles;
    }
}
