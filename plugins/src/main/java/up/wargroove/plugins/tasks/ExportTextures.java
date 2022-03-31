package up.wargroove.plugins.tasks;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.world.Biome;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class ExportTextures {
    private String biome;
    private String path;
    private boolean overwrite = false;

    public ExportTextures(String... args) {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parameter = arg.substring(2).split("=");
                initParameter(parameter);
            }
        }
    }

    private void initParameter(String... parameter) {
        if (parameter.length != 2) return;
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
        }
    }

    @Input
    public String getWorldTexturePath() {
        return "core/assets/data/sprites/world/";
    }

    @Option(option = "biome", description = "Biome of the texture")
    public void setBiome(String biome) {
        this.biome = biome;
    }

    @Option(option = "path", description = "path where the texture will be downloaded")
    public void setPath(String path) {
        this.path = path;
    }

    @Option(option = "overwrite", description = "indicate if a file a the same name a the one given " +
            "in the path or the default one will be be overwritten (Y/N)")
    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite.equalsIgnoreCase("Y");
    }

    @TaskAction
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
            return new File(path + name);
        }
        if (path.endsWith(".png")) {
            return new File(path);
        }
        File file = new File(path);
        return new File(file.getParent() + '/' + name);
    }

    private void copy(File origin, File destination) throws Exception {
        InputStream in = new FileInputStream(origin);
        OutputStream out = new FileOutputStream(destination);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}