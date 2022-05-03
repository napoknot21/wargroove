package up.wargroove.plugins.tasks;

import up.wargroove.core.world.Biome;
import up.wargroove.utils.Log;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * This is a plugin. Its primary task is to export the local textures to the given path.
 * For compatibility reasons, it must be used to draw a map with an external application.
 */
public class ExportTextures extends Plugin {
    private String biome;
    private String path;
    private boolean overwrite = false;

    /**
     * Construct the plugin with the given arguments.
     *
     * @param args The CLI arguments.
     */
    public ExportTextures(String... args) {
        super(args);
    }

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    public void run() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("The path cannot be empty");
        }
        Biome b = (biome == null) ? Biome.GRASS : Biome.valueOf(biome.toUpperCase(Locale.ROOT));
        String name = b.name().toLowerCase() + ".png";
        Path source = Paths.get(getWorldTexturePath() + name);
        File destination = createDestination(getDestination(name));
        File origin = new File(String.valueOf(source));
        if (!origin.isFile()) {
            throw new Exception("A problem occurred during the loading of the requested texture.");
        }

        copy(origin, destination);
        System.out.println("Exportation successful");
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
            case "biome":
                setBiome(parameter[1]);
                break;
            case "path":
                setPath(parameter[1]);
                break;
            case "overwrite":
                setOverwrite(parameter[1]);
                break;
            default:
                Log.print(Log.Status.ERROR, parameter[0] + "is ignored");
        }
    }

    private String getWorldTexturePath() {
        return "core/assets/data/gui/world/";
    }

    //(option = "biome", description = "Biome of the texture")
    private void setBiome(String biome) {
        this.biome = biome;
    }

    //(option = "path", description = "path where the texture will be downloaded")
    private void setPath(String path) {
        this.path = path;
    }

    //(option = "overwrite", description = "indicate if a file a the same name a the one given "
    //       + "in the path or the default one will be be overwritten (Y/N)")
    private void setOverwrite(String overwrite) {
        this.overwrite = overwrite.equalsIgnoreCase("Y");
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


    /**
     * Gets the destination file path.
     *
     * @param name The Map name.
     * @return the destination file. The file might be non existant.
     */
    private File getDestination(String name) {
        char last = path.charAt(path.length() - 1);
        if (last == '/' || last == '\\') {
            return new File(path + name);
        }
        if (path.endsWith(".png")) {
            return new File(path);
        }
        File file = new File(path);
        return new File(file.getParent() + '/' + name);
    }

    /**
     * Copy the origin file to the destination file.
     *
     * @param origin      The origin file.
     * @param destination The destination file.
     * @throws Exception if the file doesn't exist or if the buffers encountered an issues
     */
    private void copy(File origin, File destination) throws Exception {
        InputStream in = new FileInputStream(origin);
        OutputStream out = new FileOutputStream(destination);
        byte[] buf = new byte[2048];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
