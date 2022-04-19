package up.wargroove.plugins.tasks;


import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.character.Faction;
import up.wargroove.core.world.*;
import up.wargroove.utils.Database;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This is a plugin. Its primary tasks is to import a given map in CLI to the local database.
 */
public class ImportMap {
    private final StringBuilder log = new StringBuilder();
    private final Database db = new Database(getRoot());
    private String[] paths;
    private String biome;
    private final Reader reader;

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

    @InputFile
    private String getWorldTexturePath() {
        return "core/assets/data/sprites/world/grass.txt";
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
                array.add(reader.get(Integer.parseInt(d)));
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

    private Tile getTile(int data) throws Exception {
        return null;
    }

    /*private Tile buildStructure(int data) throws Exception {
        try {
            Tile tile = new Tile();
            int id = data - Tile.Type.values().length - 1;
            int enumId = 0;
            while (id - 4 > 0) {
                enumId++;
                id -= 4;
            }
            if (enumId < 3) {
                tile.entity = Optional.of(
                        new Recruitment(Recruitment.Type.values()[enumId], Faction.values()[id])
                );
            } else {
                //Build a base
            }
            return tile;
        } catch (Exception unknown) {
            throw new Exception("A problem occurred during the load of a file");
        }
    }*/

    private static class Reader {
        String path;
        ArrayList<String> data;
        int separator = Tile.Type.values().length;
        private Reader(String path) {
            this.path = path;
            data = new ArrayList<>();
        }

        private void load() throws FileNotFoundException {
            Scanner scanner = new Scanner(new File(path));
            for (int i = 0; i< 5 && scanner.hasNextLine(); i++) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String key = scanner.nextLine();
                data.add(key);
                for (int i = 0; i< 6 && scanner.hasNextLine(); i++) {
                    scanner.nextLine();
                }
            }
            scanner.close();
        }

        private Tile get(int i) {
            if (i < separator) {
                return new Tile(Tile.Type.valueOf(data.get(i).toUpperCase()));
            }
            String[] arr = data.get(i).split("_");
            if (arr.length == 2) {
                return buildBase(arr);
            } else if( arr.length == 3) {
                return buildStructure(arr);
            } else {
                throw new UnknownFormatFlagsException(
                        "The " + data.get(i) +" file's name isn't correct \n" +
                                "The required syntax is: \n" +
                                "structureType_faction for main bases\n" +
                                "or \n" +
                                "structureType_RecruitmentType_faction for regular structures"
                );
            }
        }

        private Tile buildStructure(String[] arr) {
            Tile tile = new Tile();
            Recruitment.Type rType = Recruitment.Type.valueOf(arr[1].toUpperCase());
            Faction faction = Faction.valueOf(arr[2].toUpperCase());
            tile.entity = Optional.of(new Recruitment(rType,faction));
            return tile;
        }

        private Tile buildBase(String[] arr) {
            return new Tile();
        }
    }
}
