package up.wargroove.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import up.wargroove.core.character.Entity;
import up.wargroove.core.world.Tile;
import up.wargroove.utils.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Manage the assets.
 */
public class Assets {
    private static final char fs = File.separatorChar;
    /**
     * Asset manifest extension.
     */
    private static final String asmext = ".asman";
    private static Assets instance = new Assets();
    private final AssetManager manager;
    private final Map<Class<?>, Object> defaults;
    private final Map<Entity.Type, FileHandle> entitiesDescriptions;
    private final Map<Tile.Type, FileHandle> tilesDescriptions;

    private Assets() {
        manager = new AssetManager();
        defaults = new HashMap<>();
        entitiesDescriptions = new HashMap<>();
        tilesDescriptions = new HashMap<>();
        instance = this;
    }

    public static Assets getInstance() {
        return instance;
    }

    /**
     * Loads the assets.
     */
    public void load() {
        loadAllAssets();
        printLoading();
    }

    /**
     * Loads all the assets in the Asset dir given in parameter.
     *
     * @param dir The assetDir constant that contains the assets to load.
     */
    public void load(AssetDir dir) {
        fileLoader(dir);
        printLoading();
    }

    /**
     * Loads a single file.
     *
     * @param path    The file path
     * @param type    The assets type (Texture.class, etc.)
     * @param isAlone if true, the asset manager will load file,
     *                else the manager will put the file in queue waiting to be updated. <br>
     *                <b>You must call printLoading() to load all queued files. </b>
     */
    public void load(String path, Class<?> type, boolean isAlone) {
        manager.load(path, type);
        if (isAlone) {
            printLoading();
        }
    }


    /**
     * Loads all the assets in the manifest.
     *
     * @param manifestPath The manifest listing the assets to load
     * @param isAlone      if true, the asset manager will load file,
     *                     else the manager will put the file in queue waiting to be updated. <br>
     *                     <b>You must call printLoading() to load all queued files. </b>
     */
    public void load(String manifestPath, boolean isAlone) {
        FileHandle fileLoader = Gdx.files.internal(manifestPath);
        if (fileLoader.extension().equals(asmext)) {
            fileLoader(fileLoader.parent().path(), fileLoader.name());
        }
        if (isAlone) {
            printLoading();
        }
    }

    /**
     * Loads the entity descriptions in the description directory.
     */
    public void loadEntitiesDescription() {
        FileHandle fileLoader = Gdx.files.internal(AssetDir.DESCRIPTION.path + AssetDir.DESCRIPTION.manifest[0]);
        Scanner scanner = new Scanner(fileLoader.read());
        while (scanner.hasNextLine()) {
            String path = AssetDir.DESCRIPTION.path + scanner.nextLine();
            FileHandle file = Gdx.files.internal(path);
            Entity.Type type = Entity.Type.valueOf(file.nameWithoutExtension().toUpperCase(Locale.ROOT));

            entitiesDescriptions.put(type, file);
        }
        scanner.close();
    }

    /**
     * Loads the tile description on the description directory.
     */
    public void loadTilesDescription() {
        FileHandle fileLoader = Gdx.files.internal(AssetDir.DESCRIPTION.path + AssetDir.DESCRIPTION.manifest[1]);
        Scanner scanner = new Scanner(fileLoader.read());
        while (scanner.hasNextLine()) {
            String path = AssetDir.DESCRIPTION.path + scanner.nextLine();
            FileHandle file = Gdx.files.internal(path);
            Tile.Type type = Tile.Type.valueOf(file.nameWithoutExtension().toUpperCase(Locale.ROOT));

            tilesDescriptions.put(type, file);
        }
        scanner.close();
    }


    @SuppressWarnings("all")
    public <T> T getDefault(Class<T> defaultClass) {
        return (T) defaults.get(defaultClass);
    }

    /**
     * Loads all the assets contained in the AssetDir directories.
     */
    /*
    private void loadAllAssets() {
        for (AssetDir dir : AssetDir.values()) {
            if (dir.manifest != null) {
                fileLoader(dir);
            }
        }
    }
    */
    private void loadAllAssets() {
        ThreadGroup group = new ThreadGroup("allDir");
        for (AssetDir dir : AssetDir.values()) {
            new Thread(group, () -> fileLoader(dir)).start();
        }
        while (group.activeCount() > 0) {
        }
    }

    /**
     * Loads the defaults assets.
     */
    public void loadDefault() {
        Skin defaultSkin = new Skin(Gdx.files.internal(AssetDir.SKIN.path + "rusty-robot-ui.json"));
        defaults.put(Skin.class, defaultSkin);
        Texture texture = new Texture(Gdx.files.internal(AssetDir.SKIN.path + "uiskin.png"));
        defaults.put(Texture.class, texture);
        Sound defaultSound = Gdx.audio.newSound(Gdx.files.internal("data/gui/sound/switch.wav"));
        defaults.put(Sound.class, defaultSound);
        Music defaultMusic = Gdx.audio.newMusic(Gdx.files.internal("data/gui/sound/theme.mp3"));
        defaults.put(Music.class, defaultMusic);
    }

    /**
     * Print the loading.
     */
    public void printLoading() {
        while (!manager.update()) {
            float progress = manager.getProgress();
            Log.print("Loading ... " + progress * 100 + "%");
        }
        Log.print("Loading ... " + 100.0 + "%");
    }

    /**
     * Load the assets in the AssetManager.
     *
     * @param dirPath  The directory path.
     * @param manifest The manifest name with extension
     */
    private void fileLoader(String dirPath, String manifest) {
        if (manifest.isBlank()) {
            return;
        }
        try {
            FileHandle fileLoader = Gdx.files.internal(dirPath + fs + manifest);
            Scanner scanner = new Scanner(fileLoader.read());
            Class<?> c = AssetType.valueOf(scanner.nextLine()).type;
            if (c.equals(Skin.class)) {
                loadSkin(dirPath, scanner);
            } else {

                while (scanner.hasNextLine()) {
                    String path = dirPath + scanner.nextLine();
                    manager.load(path, c);
                }
            }
            scanner.close();
        } catch (GdxRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Load the assets in the AssetManager.
     *
     * @param dir The constant linked to an asset dir.
     */
    private void fileLoader(AssetDir dir) {
        if (dir.manifest == null) {
            return;
        }
        for (String s : dir.manifest) {
            fileLoader(dir.path, s);
        }
    }

    private void loadSkin(String dirPath, Scanner scanner) {
        while (scanner.hasNextLine()) {
            String path = dirPath + scanner.nextLine();
            manager.load(path, Skin.class, new SkinLoader.SkinParameter());
        }
    }

    public Texture getTest() {
        return get(AssetDir.WORLD.getPath() + "test.png");
    }

    /**
     * Gets the fileName's asset.
     *
     * @param fileName the asset file name.
     * @return the asset.
     * @throws GdxRuntimeException if the asset is not loaded.
     */
    public <T> T get(String fileName) {
        fileName = fileName.replace('\\', '/');
        return manager.get(fileName);
    }

    /**
     * Gets the fileName's asset with the specified type.
     *
     * @param fileName the asset file name.
     * @param type     the asset type.
     * @return the asset.
     * @throws GdxRuntimeException if the asset is not loaded.
     */
    public <T> T get(String fileName, Class<T> type) {
        fileName = fileName.replace('\\', '/');
        return manager.get(fileName, type);
    }

    /**
     * Gets the fileName's asset.
     *
     * @param fileName the asset file name.
     * @param required true to throw GdxRuntimeException if the asset is not loaded, else null is returned.
     * @return the asset or null if it is not loaded and required is false.
     */
    public <T> T get(String fileName, boolean required) {
        fileName = fileName.replace('\\', '/');
        return manager.get(fileName, required);
    }

    /**
     * Gets the fileName's asset.
     *
     * @param fileName the asset file name.
     * @param type     the asset type.
     * @param required true to throw GdxRuntimeException if the asset is not loaded, else null is returned.
     * @return the asset or null if it is not loaded and required is false.
     */
    public <T> T get(String fileName, Class<T> type, boolean required) {
        fileName = fileName.replace('\\', '/');
        return manager.get(fileName, type, required);
    }

    /**
     * Gets the description of the given type.
     *
     * @param type The tile type.
     * @param lineLength The length of the lines.
     * @return the description as a String. Each line is of length lineLength.
     * @throws FileNotFoundException if the description wasn't loaded
     */
    public String get(Tile.Type type, int lineLength) throws FileNotFoundException {
        FileHandle f = tilesDescriptions.get(type);
        if (f == null) {
            throw new RuntimeException("The asset was not loaded for the Tile type : " + type.name());
        }
        return readFile(f, lineLength);
    }

    /**
     * Gets the description of the given type.
     *
     * @param type The entity type.
     * @param lineLength The length of the lines.
     * @return the description as a String. Each line is of length lineLength.
     * @throws FileNotFoundException if the description wasn't loaded
     */
    public String get(Entity.Type type, int lineLength) throws FileNotFoundException {
        if (lineLength < 1) {
            return "";
        }
        FileHandle f = entitiesDescriptions.get(type);
        if (f == null) {
            throw new RuntimeException("The asset was not loaded for the Entity type : " + type.name());
        }
        return readFile(f, lineLength);
    }

    private String readFile(FileHandle file, int lineLength) {
        Scanner scanner = new Scanner(file.read());
        StringBuilder builder = new StringBuilder();
        int len = 0;
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split(" ");
            for (String l : line) {
                if (len + l.length() >= lineLength) {
                    builder.append("\n");
                    len = 0;
                }
                builder.append(l).append(" ");
                len += l.length() + 1;
            }
            builder.append("\n");
        }
        scanner.close();
        return builder.toString();
    }

    /**
     * Gets all the assets.
     *
     * @param type the asset type.
     * @return all the assets matching the specified type.
     */
    public <T> Array<T> getAll(Class<T> type, Array<T> out) {
        return manager.getAll(type, out);
    }

    public void dispose() {
        manager.dispose();
    }

    /**
     * List the assets directories and their manifest.
     */
    public enum AssetDir {
        DATA("data" + fs), GUI(DATA.path + "gui" + fs),
        SKIN(GUI.path + "skin" + fs, "skin"), SOUND(GUI.path + "sound" + fs, "sound"),
        SPRITES(DATA.path + "sprites" + fs),
        ARROWS(SPRITES.path + "arrows" + fs, "arrows"),
        CHARACTER(SPRITES.path + "character" + fs),
        CHERRYSTONE_KINGDOM(CHARACTER.path + "CHERRYSTONE_KINGDOM" + fs, "CHERRYSTONE_KINGDOM"),
        FELHEIM_LEGION(CHARACTER.path + "FELHEIM_LEGION" + fs, "FELHEIM_LEGION"),
        FLORAN_TRIBES(CHARACTER.path + "FLORAN_TRIBES" + fs, "FLORAN_TRIBES"),
        HEAVENSONG_EMPIRE(CHARACTER.path + "HEAVENSONG_EMPIRE" + fs, "HEAVENSONG_EMPIRE"),
        STATS(CHARACTER.path + "STATS" + fs, "STATS"),
        WORLD(SPRITES.path + "world" + fs, "test"),
        GRASS(WORLD.path + "grass" + fs),
        ICE(WORLD.path + "ice" + fs, "ice"),
        DESCRIPTION(DATA.path + "descriptions" + fs, "entities", "tiles");

        // TODO : remplir les chemins menant au repertoire et leur manifest pour charger les donnees

        private final String path;
        private final String[] manifest;

        AssetDir(String path, String... manifest) {
            this.path = path;
            this.manifest = manifest;
            if (manifest != null) {
                for (int i = 0; i < manifest.length; i++) {
                    if (!manifest[i].isBlank()) {
                        manifest[i] += asmext;
                    }
                }
            }
        }

        AssetDir(String path) {
            this(path, "");
        }

        public String getPath() {
            return path;
        }

        public String[] getManifest() {
            return manifest;
        }
    }


    /**
     * List the Assets Types.
     */
    public enum AssetType {
        TEXTURE(Texture.class), SKIN(Skin.class), SOUND(Sound.class);
        //TODO : remplir la liste avec les type de donnees a charger

        private final Class<?> type;

        AssetType(Class<?> type) {
            this.type = type;
        }
    }
}
