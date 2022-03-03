package up.wargroove.plugins;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import up.wargroove.core.world.Biome;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class ExportTextures extends DefaultTask {
    private String biome;
    private String path;
    private boolean overwrite;
    
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
        this.overwrite = overwrite.equals("Y");
    }
    
    @TaskAction
    public void run() throws Exception {
        if (path == null || path.isEmpty()) {
            throw new Exception("The path cannot be empty");
        }
        Biome b = (biome == null)? Biome.GRASS : Biome.valueOf(biome.toUpperCase(Locale.ROOT));
        String name = b.name().toLowerCase() + ".png";
        Path source = Paths.get(getWorldTexturePath() + name);
        File destination = getDestination(name);
        File origin = new File(String.valueOf(source));
        if (!origin.isFile()) {
            throw new Exception("A problem occurred during the loading of the requested texture.");
        }
        createDestination(destination);
        copy(origin,destination);
        System.out.println("Exportation successful");
    }

    private void createDestination(File destination) throws Exception{
        if (overwrite && destination.exists()) {
            return;
        }
        if (!destination.createNewFile()) {
            throw new Exception("A problem occurred during the creation of the destination file");
        }
    }

    private File getDestination(String name) {
        char last = path.charAt(path.length() - 1);
        if (last =='/' || last == '\\') {
            return new File(path+name);
            }
        if (path.endsWith(".png")) {
            return new File(path);
        }
        File file = new File(path);
        return new File(file.getParent() +'/'+ name);
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
