package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import up.wargroove.utils.Constants;
import up.wargroove.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * List of valid positions.
 */
class Valid extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
    private final float worldScale;
    private boolean validPosition;
    /**
     * Tile useful information. The order of intel is the same as this.
     * The first is the parentIndex, the second is the tile's movement cost.
     */
    private final ArrayList<int[]> intel;

    /**
     * Index that point to the last used sprite.
     */

    protected Valid(float worldScale) {
        intel = new ArrayList<>();
        this.worldScale = worldScale;
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * otherwise it will use a free sprite.
     *
     * @param texture     The sprite texture.
     * @param coordinates The sprites coordinates.
     * @param owner
     */
    private void add(TextureRegion texture, Pair<?, ?> coordinates, boolean owner) {
        if (!(coordinates.first instanceof Integer) || !(coordinates.second instanceof Integer)) return;
        Pair<Integer, Integer> coord = new Pair<>((int) coordinates.first, (int) coordinates.second);
        int x = (int) (coord.first * worldScale);
        int y = (int) (coord.second * worldScale);
        Sprite sprite = new Sprite(texture);
        if (!owner) sprite.setColor(Color.BLACK);
        else sprite.setColor(Color.WHITE);
        if ((intel.size() > size()) && intel.get(size())[Constants.BFS_VALID] < 0) {
            sprite.setAlpha(0);
        }
        sprite.setPosition(x, y);
        this.add(new Pair<>(sprite, (coord)));
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * otherwise it will use a free sprite.
     *
     * @param texture The sprite texture.
     * @param coord   The sprites coordinates.
     * @param owner
     */
    void add(Texture texture, Pair<?, ?> coord, boolean owner) {
        this.add(new TextureRegion(texture), coord, owner);
    }

    /**
     * Add all the tile's intel to intel.
     *
     * @param vector the list of intel.
     */
    private void addIntel(List<int[]> vector) {
        intel.addAll(vector);
    }

    /**
     * Checks is the coordinate is valid.
     *
     * @param coordinate the coordinate that needed to be checked.
     * @return the index of the corresponding tile, -1 otherwise.
     */
    protected int isValid(Pair<Integer, Integer> coordinate) {
        for (int i = 0; i < size(); i++) {
            if (this.get(i).second.equals(coordinate)) {
                validPosition = intel.get(i)[Constants.BFS_VALID] >= 0;
                return validPosition ? i : -1;
            }
        }
        validPosition = false;
        return -1;
    }

    /**
     * Gets the tile movement cost.
     *
     * @param tileIndex the index of the tile.
     * @return the cost of moving on the tile.
     */
    protected int getTileCost(int tileIndex) {
        return intel.get(tileIndex)[Constants.BFS_MV_COST];
    }

    /**
     * Gets the default path used in the bfs.
     *
     * @param tileIndex the destination tile index of the path.
     * @return a stack with the character's path to the destination.
     */
    protected Stack<Pair<Pair<Integer, Integer>, Integer>> getDefault(int tileIndex) {
        Stack<Pair<Pair<Integer, Integer>, Integer>> res = new Stack<>();
        int i = tileIndex;
        while (i >= 0) {
            var tmp = this.get(i).second;
            res.push(new Pair<>(new Pair<>(tmp.first, tmp.second), i));
            i = intel.get(i)[Constants.BFS_PARENT];
        }
        return res;
    }

    /**
     * Reset movement path and list index. All the build sprites
     * are now considered free.
     */
    protected void reset() {
        intel.clear();
        clear();
    }

    public boolean isValidPosition() {
        return validPosition;
    }

    /**
     * Draw the path on the screen.
     *
     * @param batch The drawer.
     */
    protected void draw(Batch batch) {
        for (Pair<Sprite, Pair<Integer, Integer>> spritePairPair : this) {
            spritePairPair.first.draw(batch);
        }
    }

    /**
     * According to the intel, add the texture  in the list of coorfinates
     *
     * @param texture
     * @param coordinates
     * @param intel
     * @param owner
     */
    public void setData(Texture texture, List<Pair<?, ?>> coordinates, List<int[]> intel, boolean owner) {
        addIntel(intel);
        coordinates.forEach(v -> add(texture, v, owner));
    }

    /**
     * Creates the first intel
     *
     * @param texture
     * @param results
     * @param owner
     */
    public void setData(Texture texture, List<Pair<?, ?>> results, boolean owner) {
        ArrayList<int[]> intel = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            intel.add(new int[]{1, 1, 1, 1});
        }
        setData(texture, results, intel, owner);
    }
}
