package up.wargroove.plugins.tasks;

import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.world.GeneratorProperties;
import up.wargroove.core.world.World;
import up.wargroove.core.world.WorldProperties;
import up.wargroove.utils.Database;
import up.wargroove.utils.DbObject;
import up.wargroove.utils.Pair;

import java.io.File;
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
    @TaskAction
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


    @Option(option = "name", description = "name of the map stored in the database")
    private void setName(String name) {
        this.name = name;
    }

    @Option(option = "path", description = "The path where the map will be exported")
    private void setPath(String path) {
        this.path = path;
    }

    @Option(option = "gen", description = "Select if the map will be generated before being exported (Y/N)")
    private void setGeneration(String generation) {
        gen = generation.equalsIgnoreCase("Y");
    }

    /**
     * Gets the default map dimension.
     *
     * @return the default dimension.
     */
    @Input
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
    @Option(option = "dim", description = "World dimension. The syntaxe MUST be the following : "
            + "dimension.first, dimension.second")
    private void setDimension(String ds) throws Exception {
        String[] dim = ds.split(",");
        if (dim.length != 2) {
            throw new Exception("The dimension syntaxe is wrong");
        }
        int first = Integer.parseInt(dim[0]);
        int second = Integer.parseInt(dim[1]);
        dimension = new Pair<>(first, second);
    }

    @Option(option = "r", description = "Generations parameters repartition")
    private void setRepartition(String repartition) {
        this.repartition = Integer.parseInt(repartition);
    }

    @Option(option = "overwrite", description = "indicate if a file with the same name as the one given "
            + "in the path or the default one will be be overwritten (Y/N)")
    private void setOverwrite(String overwrite) {
        this.overwrite = overwrite.equalsIgnoreCase("Y");
    }

    @Option(option = "n", description = "Generations parameters normalization")
    private void setNormalization(String normalization) {
        this.normalization = Double.parseDouble(normalization);
    }

    @Option(option = "s", description = "Generations parameters smooth")
    private void setSmooth(String smooth) {
        this.smooth = Double.parseDouble(smooth);
    }

    @InputFile
    private File getRoot() {
        return new File("db/wargroove.db");
    }

    @Input
    private String getExtension() {
        return ".csv";
    }

    // TODO: 10/03/2022 : Improvement of enum gestion
    private void write(File file, WorldProperties properties) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimension.first; i++) {
            for (int j = 0; j < dimension.second; j++) {
                int key = properties.terrain[i * dimension.first + j].getType().ordinal();
                builder.append(key).append(";");
            }
            builder.append('\n');
        }
        FileWriter writer = new FileWriter(file);
        writer.write(builder.toString());
        writer.flush();
        writer.close();
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
        data.selectCollection("worlds");
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
