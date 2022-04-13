package up.wargroove.plugins.tasks;

import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Database;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

/**
 * This is a plugin. Its primary tasks is to import a given map in CLI to the local database.
 */
public class ImportMap {
    private final StringBuilder log = new StringBuilder();
    private final Tile.Type[] tileType = Tile.Type.values();
    private final Database db = new Database(getRoot());
    private String[] paths;
    private String biome;

    /**
     * Construct the plugin and set the given arguments.
     *
     * @param args The CLI arguments.
     */
    public ImportMap(String... args) {
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
    @TaskAction
    public void run() throws Exception {
        System.out.println(Paths.get(".").toAbsolutePath());
        if (paths == null || paths.length == 0) {
            throw new Exception("The args cannot be empty");
        }

        for (String path : paths) {
            try {
                File file = new File(path);
                if (!file.exists() || file.isDirectory()) {
                    log.append("The specified path must indicate a file (").append(path).append(")").append('\n');
                }
                load(file);
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

    @InputFile
    private File getRoot() {
        return new File("db/wargroove.db");
    }

    @Option(option = "paths", description = "List of file paths that point to a map that needed to be loaded")
    private void setPaths(String args) {
        this.paths = args.split(";");
    }

    @Option(option = "biome", description = "Biome of the world")
    private void setBiome(String biome) {
        this.biome = biome;
    }




    private void load(File file) throws Exception {
        if (!file.getName().split("\\.")[1].equals("csv")) {
            throw new Exception("The file must be a CSV file");
        }
        WorldProperties properties = traduction(file);
        World world = new World(properties);
        world.save(db);
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
                array.add(getTile(Integer.parseInt(d)));
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
        properties.terrain = array.toArray(new Tile[0]);
        properties.setDescription("Map imported from " + file.getName() + " on " + Calendar.getInstance().getTime());
        properties.setName(file.getName().split("\\.")[0]);
        properties.setBiome((biome == null) ? Biome.GRASS : Biome.valueOf(biome.toUpperCase(Locale.ROOT)));
        return properties;
    }

    private Tile getTile(Integer data) throws Exception {
        if (data < 0 || data > tileType.length) {
            throw new Exception("A problem occurred during the load of a file");
        }
        return new Tile(tileType[data]);
    }
}
