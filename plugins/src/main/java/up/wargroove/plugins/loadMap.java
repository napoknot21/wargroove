package up.wargroove.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Database;
import up.wargroove.utils.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

public class loadMap extends DefaultTask {
    private final StringBuilder log = new StringBuilder();
    private final Tile.Type[] tileType = Tile.Type.values();
    private final Database db = new Database(new File(getRoot()));
    private String[] paths;
    private String biome;

    @Input
    public String getRoot() {
        return "db/wargroove.db";
    }

    @Option(option = "paths", description = "List of file paths that point to a map that needed to be loaded")
    public void setPaths(String args) {
        this.paths = args.split(" ");
    }

    @Option(option = "biome", description = "Biome of the world")
    public void setBiome(String biome) {
        this.biome = biome;
    }


    @TaskAction
    public void run() throws Exception {
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
                    log.append("An error occurred during the load of ").append(path).append("(").append(e.getMessage()).append(")");
                }
            }

        if (log.length() != 0) {
            throw new Exception(log.toString());
        }
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
            width = line.length();
            String[] data = line.split(",");
            for (String d : data) {
                array.add(getTile(Integer.parseInt(d)));
            }
        }
        return generateProperties(array, width, height, file);
    }

    /**
     * Generates the World properties.
     *
     * @param array the world terrain.
     * @param width the world terrain width.
     * @param height the world terrain height.
     * @param file the csv file.
     * @return The world properties corresponding to the file.
     */
    private WorldProperties generateProperties(ArrayList<Tile> array, int width, int height, File file) {
        WorldProperties properties = new WorldProperties();
        properties.dimension = new Pair<>(width, height);
        properties.terrain = array.toArray(new Tile[0]);
        properties.description = "Map imported from " + file.getName() + " on " + Calendar.getInstance().getTime();
        properties.name = file.getName().split("\\.")[0];
        properties.biome = (biome == null) ? Biome.GRASS : Biome.valueOf(biome.toUpperCase(Locale.ROOT));
        return properties;
    }

    private Tile getTile(Integer data) throws Exception {
        if (data < 0 || data > tileType.length) {
            throw new Exception("A problem occurred during the load of a file");
        }
        return new Tile(tileType[data]);
    }
}
