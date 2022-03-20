package up.wargroove.plugins;

import org.gradle.api.DefaultTask;
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

public class ExportMap extends DefaultTask {
    private String name;
    private String path;
    private boolean gen = false;
    private boolean overwrite = false;
    private int repartition = 3;
    private double normalization = -3.2;
    private double smooth = -12D;
    private Pair<Integer, Integer> dimension;

    @Option(option = "name", description = "name of the map stored in the database")
    public void setName(String name) {
        this.name = name;
    }

    @Option(option = "path", description = "The path where the map will be exported")
    public void setPath(String path) {
        this.path = path;
    }

    @Option(option = "gen", description = "Select if the map will be generated before being exported (Y/N)")
    public void setGeneration(String generation) {
        gen = generation.equalsIgnoreCase("Y");
    }

    @Input
    public Pair<Integer, Integer> getDimension() {
        return new Pair<>(20, 20);
    }

    @Option(option = "dim", description = "World dimension. The syntaxe MUST be the following : " +
            "dimension.first, dimension.second")
    public void setDimension(String ds) throws Exception {
        String[] dim = ds.split(",");
        if (dim.length != 2) {
            throw new Exception("The dimension syntaxe is wrong");
        }
        int first = Integer.parseInt(dim[0]);
        int second = Integer.parseInt(dim[1]);
        dimension = new Pair<>(first, second);
    }

    @Option(option = "r", description = "Generations parameters repartition")
    public void setRepartition(String repartition) {
        this.repartition = Integer.parseInt(repartition);
    }

    @Option(option = "overwrite", description = "indicate if a file a the same name a the one given " +
            "in the path or the default one will be be overwritten (Y/N)")
    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite.equalsIgnoreCase("Y");
    }

    @Option(option = "n", description = "Generations parameters normalization")
    public void setNormalization(String normalization) {
        this.normalization = Double.parseDouble(normalization);
    }

    @Option(option = "s", description = "Generations parameters smooth")
    public void setSmooth(String smooth) {
        this.smooth = Double.parseDouble(smooth);
    }

    @InputFile
    public File getRoot() {
        return new File("db/wargroove.db");
    }

    @Input
    public String getExtension() {
        return ".csv";
    }

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
        properties.name = name;
        World world = new World(properties);
        world.initialize(true);
        return properties;
    }
}
