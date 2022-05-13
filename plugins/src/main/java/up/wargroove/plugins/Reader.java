package up.wargroove.plugins;

import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Stronghold;
import up.wargroove.core.world.Tile;
import up.wargroove.core.world.Village;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Textures atlas reader.
 */
public class Reader {
    String path;
    ArrayList<String> data;

    public Reader(String path) {
        this.path = path;
        data = new ArrayList<>();
    }

    /**
     * Load the data stored in the path' file.
     *
     * @throws FileNotFoundException if the file doesn't exist.
     */
    public void load() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        for (int i = 0; i < 5 && scanner.hasNextLine(); i++) {
            scanner.nextLine();
        }
        while (scanner.hasNextLine()) {
            String key = scanner.nextLine();
            if (!key.isBlank()) {
                data.add(key);
            }
            for (int i = 0; i < 6 && scanner.hasNextLine(); i++) {
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /**
     * Returns the index of the first occurrence of the specified element in this list,
     * or -1 if this list does not contain the element.
     *
     * @param name element to search for.
     * @return the index of the first occurrence of the specified element in this list,
     * or -1 if this list does not contain the element.
     */
    public int indexOf(String name) {
        return data.indexOf(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the tile at the specified position.
     *
     * @param index The index of the element to return.
     * @return the element at the specified position in this list
     */
    public Tile get(int index) {
        String[] arr = data.get(index).split("-");
        switch (arr.length) {
            case 2:
                return buildTile(arr);
            case 3:
                return buildStructure(arr);
            default:
                throw new UnknownFormatFlagsException(
                        "The " + data.get(index) + " file's name isn't correct \n" +
                                "The required syntax is: \n" +
                                "tile_type-texture_version for tiles\n" +
                                "structure_type-faction-texture_version for main bases or villages \n" +
                                "or \n" +
                                "Recruitment_type-faction-texture_version for recruitments"
                );
        }
    }

    /**
     * Build the tile according to the given argument.
     *
     * @param arr The tile information.
     * @return The build tile.
     */
    private Tile buildTile(String[] arr) {
        Tile.Type type = Tile.Type.valueOf(arr[0].toUpperCase());
        int version = Integer.parseInt(arr[1]);
        return new Tile(type, version);
    }

    /**
     * Build the structure according to the given argument.
     *
     * @param arr The structure information.
     * @return The build structure.
     */
    private Tile buildStructure(String[] arr) {
        Faction faction = Faction.valueOf(arr[1].toUpperCase());
        Entity entity;
        if (arr[0].equalsIgnoreCase("stronghold")) {
            entity = new Stronghold(faction);
        } else if (arr[0].equalsIgnoreCase("village")) {
            entity = new Village(faction);
        } else {
            Recruitment.Type rType = Recruitment.Type.valueOf(arr[0].toUpperCase());
            entity = new Recruitment(rType, faction);
        }
        Tile tile = new Tile();
        tile.entity = Optional.of(entity);
        return tile;
    }
}
