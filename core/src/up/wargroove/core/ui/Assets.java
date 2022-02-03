package up.wargroove.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;
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
    private final AssetManager manager;

    public Assets() {
        manager = new AssetManager();
    }

    /**
     * Load the assets.
     */
    public void load() {
        loadAllAssets();
        while (!manager.update()) {
            float progress = manager.getProgress();
            System.out.println("Loading ... " + progress * 100 + "%");
        }
        System.out.println("Loading ... " + 100.0 + "%");
    }

    private void loadAllAssets() {
        for (AssetDir dir : AssetDir.values()) {
            if (dir.manifest != null) {
                fileLoader(dir);
            }
        }
    }

    /**
     * Load the assets in the AssetManager.
     *
     * @param dir The constant linked to an asset dir.
     */
    private void fileLoader(AssetDir dir) {
        try {
            FileHandle fileLoader = Gdx.files.internal(dir.path + fs + dir.manifest);
            Scanner scanner = new Scanner(fileLoader.read());
            Class<?> c = AssetType.valueOf(scanner.nextLine()).type;

            while (scanner.hasNextLine()) {
                String path = dir.path + scanner.nextLine();
                manager.load(path, c);
            }
            scanner.close();
        } catch (GdxRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets the fileName's asset.
     *
     * @param fileName the asset file name.
     * @return the asset.
     * @throws GdxRuntimeException if the asset is not loaded.
     */
    public <T> T get(String fileName) {
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
        return manager.get(fileName, type, required);
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
    enum AssetDir {
        DATA("data" + fs),
        SPRITES(DATA.path + "sprites" + fs),
        WORLD(SPRITES.path + "world" + fs, "test" + asmext),
        GRASS(WORLD.path + "grass" + fs),
        ICE(WORLD.path + "ice" + fs, "ice" + asmext);

        // TODO : remplir mes chemins menant au repertoire et leur manifest pour charger les donnees

        private final String path;
        private final String manifest;

        AssetDir(String path, String manifest) {
            this.path = path;
            this.manifest = manifest;
        }

        AssetDir(String path) {
            this(path, null);
        }
    }

    /**
     * List the Assets Textures.
     */
    enum AssetType {
        TEXTURE(Texture.class);
        //TODO : remplir la liste avec les type de donnees a charger

        private final Class<?> type;

        AssetType(Class<?> type) {
            this.type = type;
        }
    }
}
