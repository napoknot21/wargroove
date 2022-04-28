package up.wargroove.plugins.tasks;


import up.wargroove.core.world.*;
import up.wargroove.plugins.Reader;
import up.wargroove.utils.Database;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

/**
 * This is a plugin. Its primary task is to export a map from the local database to the given path.
 */
public class ExportMap {
    /**
     * Name of the map stored in the database.
     */
    private String name;

    /**
     * Name of the map stored in the database.
     */
    private String collection;
    /**
     * The path where the map will be exported.
     */
    private String path;
    /**
     * Select if the map will be generated before being exported.
     */
    private boolean gen = false;
    /**
     * Indicate if a file with the same name as the one given in the path or the default one will be overwritten.
     */
    private boolean overwrite = false;
    /**
     * Generations parameters repartition.
     */
    private int repartition = 3;
    /**
     * Generations parameters normalization.
     */
    private double normalization = -3.2;
    /**
     * Generations parameters smooth.
     */
    private double smooth = -12D;
    /**
     * World dimension. The syntaxe <b>MUST</b> be the following : <i>dimension.first, dimension.second</i>
     */
    private Pair<Integer, Integer> dimension;

    /**
     * Constructs the plugin with the given arguments.
     *
     * @param args The CLI arguments.
     * @throws Exception if an error occurred.
     */
    public ExportMap(String... args) throws Exception {
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
        if (name == null || name.isBlank() || path == null || path.isBlank()) {
            throw new Exception("The args cannot be empty");
        }
        WorldProperties properties = (gen) ? generate() : getWorld();
        dimension = properties.dimension;
        File file = createDestination(getDestination(name));
        write(file, properties);
    }

    private void initParameter(String... parameter) throws Exception {
        if (parameter.length != 2) {
            return;
        }
        switch (parameter[0]) {
            case "name":
                setName(parameter[1]);
                break;
            case "path":
                setPath(parameter[1]);
                break;
            case "gen":
                setGeneration(parameter[1]);
                break;
            case "dim":
                setDimension(parameter[1]);
                break;
            case "r":
                setRepartition(parameter[1]);
                break;
            case "overwrite":
                setOverwrite(parameter[1]);
                break;
            case "n":
                setNormalization(parameter[1]);
                break;
            case "s":
                setSmooth(parameter[1]);
                break;
            default:
        }
    }


    private String getWorldTexturePath() {
        return "core/assets/data/sprites/world/grass.atlas";
    }
    //(option = "name", description = "name of the map stored in the database")
    private void setName(String name) {
        String[] s = name.split("/");
        this.collection = s[0];
        this.name = s[1];

    }

    //(option = "path", description = "The path where the map will be exported")
    private void setPath(String path) {
        this.path = path;
    }

    //(option = "gen", description = "Select if the map will be generated before being exported (Y/N)")
    private void setGeneration(String generation) {
        gen = generation.equalsIgnoreCase("Y");
    }

    /**
     * Gets the default map dimension.
     *
     * @return the default dimension.
     */
    private Pair<Integer, Integer> getDimension() {
        return new Pair<>(20, 20);
    }

    /**
     * Set the dimension with the given argument. <br>
     * The syntaxe <b>MUST</b> be the following : <i>dimension.first, dimension.second</i>
     *
     * @param ds The CLI argument.
     * @throws Exception the argument doesn't respect the format
     */
    /*(option = "dim", description = "World dimension. The syntaxe MUST be the following : "
            + "dimension.first, dimension.second")*/
    private void setDimension(String ds) throws Exception {
        String[] dim = ds.split(",");
        if (dim.length != 2) {
            throw new Exception("The dimension syntaxe is wrong");
        }
        int first = Integer.parseInt(dim[0]);
        int second = Integer.parseInt(dim[1]);
        dimension = new Pair<>(first, second);
    }

    //(option = "r", description = "Generations parameters repartition")
    private void setRepartition(String repartition) {
        this.repartition = Integer.parseInt(repartition);
    }

    /*(option = "overwrite", description = "indicate if a file with the same name as the one given "
            + "in the path or the default one will be be overwritten (Y/N)")*/
    private void setOverwrite(String overwrite) {
        this.overwrite = overwrite.equalsIgnoreCase("Y");
    }

    //(option = "n", description = "Generations parameters normalization")
    private void setNormalization(String normalization) {
        this.normalization = Double.parseDouble(normalization);
    }

    //(option = "s", description = "Generations parameters smooth")
    private void setSmooth(String smooth) {
        this.smooth = Double.parseDouble(smooth);
    }

    private File getRoot() {
        return new File("db/wargroove.db");
    }

    private String getExtension() {
        return ".csv";
    }

    // TODO: 10/03/2022 : Improvement of enum gestion
    private void write(File file, WorldProperties properties) throws Exception {

        int[][] tiles = readTerrain(properties);
        StringBuilder builder = new StringBuilder();
        for (int i = tiles.length - 1; i>= 0; i--) {
            for (int id : tiles[i]) {
                builder.append(id).append(";");
            }
            builder.append('\n');
        }
        FileWriter writer = new FileWriter(file);
        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    private int[][] readTerrain(WorldProperties properties) throws FileNotFoundException {
        int[][] array = new int[properties.dimension.first][properties.dimension.second];
        Reader reader = new Reader(getWorldTexturePath());
        reader.load();
        for (int i = array.length - 1; i >= 0 ; i--) {
            for (int j = 0; j < array[i].length ; j++) {
                Tile tile = properties.terrain[i * dimension.first + j];
                array[i][j] = getTileId(reader,tile);
            }
        }
        return array;
    }

    private int getTileId(Reader reader, Tile tile) {
        String s;
        if (tile.entity.isEmpty()) {
            s = (tile.getType().toString());
        }
        else {

            if (tile.entity.get() instanceof Stronghold || tile.entity.get() instanceof Village) {
                s = String.valueOf(((Structure) tile.entity.get()).getStructureType());
            } else {
                s = ((Recruitment)tile.entity.get()).getRecruitmentType().toString();
            }

            s += "-" + tile.entity.get().getFaction();
        }

        s += "-" +tile.getTextureVersion();
        return reader.indexOf(s);
    }

    private File createDestination(File destination) throws Exception {
        if (overwrite && destination.exists()) {
            return destination;
        }
        if (!destination.createNewFile()) {
            throw new Exception("A problem occurred during the creation of the destination file");
        }
        return destination;
    }

    private File getDestination(String name) {
        char last = path.charAt(path.length() - 1);
        if (last == '/' || last == '\\') {
            return new File(path + name + getExtension());
        }
        if (path.endsWith(".csv")) {
            return new File(path);
        }
        File file = new File(path);
        return new File(file.getParent() + '/' + name + getExtension());
    }

    private WorldProperties getWorld() throws Exception {
        Database data = new Database(getRoot());
        data.selectCollection(collection);
        DbObject object = data.get(name);
        if (object == null) {
            throw new Exception("The specified map doesn't exist");
        }
        WorldProperties properties = new WorldProperties();
        properties.load(object);
        return properties;
    }

    private WorldProperties generate() {
        WorldProperties properties = new WorldProperties();
        properties.dimension = (dimension == null) ? getDimension() : dimension;
        properties.genProperties = new GeneratorProperties(repartition, normalization, smooth);
        properties.setName(name);
        World world = new World(properties);
        world.initialize(true);
        return properties;
    }
}
