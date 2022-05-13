package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

/**
 * The screen movement manager.
 */
public class MovementSelector implements Selector {
    /**
     * List of valid positions.
     */
    protected final Valid valid;
    /**
     * The tile scale between the view and the world.
     */
    private final float worldScale;
    /**
     * List of movements.
     */
    private final Movements movements;
    /**
     * Indicate if the movement selector is currently used.
     */
    private boolean active;
    /**
     * Initial x position.
     */
    private int initX;
    /**
     * Initial y position.
     */
    private int initY;
    /**
     * Movement range.
     */
    private int cost;

    private boolean owner = false;


    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public MovementSelector(float worldScale) {
        this.worldScale = worldScale;
        valid = new Valid(worldScale);
        movements = new Movements();
        initX = 0;
        initY = 0;
        cost = 0;
        active = false;
    }

    /**
     * Add a new step on the movement path.
     *
     * @param assets The app assets.
     * @param v      The step coordinates.
     */
    public void addMovement(Assets assets, Vector3 v) {
        this.addMovement(assets, new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Add a new step on the movement path.
     *
     * @param assets The app assets.
     * @param coord  The step coordinates.
     */
    public void addMovement(Assets assets, Pair<Integer, Integer> coord) {
        if (!owner) return;
        int tileIndex = valid.isValid(coord);
        if (tileIndex < 0) {
            movements.reset();
        } else {
            movements.add(assets, coord, tileIndex);
        }
    }

    /**
     * Check if v is a possible position to attack
     */

    public boolean isValidPosition() {
        return valid.isValidPosition();
    }

    /**
     * Check if v is a possible position to attack
     *
     * @param c the coordinate that needed to be checked.
     * @return
     */

    public boolean isValidPosition(Pair<Integer, Integer> c) {
        return this.valid.isValid(c) != -1;
    }

    /**
     * Check if v is a possible position to attack
     *
     * @param v the vector that needed to be checked.
     * @return
     */

    public boolean isValidPosition(Vector3 v) {
        return isValidPosition(new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Sets the unit original position.
     *
     * @param v the unit coordinates in world terrain coordinates.
     */
    public void setEntityInformation(Vector3 v, int cost) {
        movements.reset();
        if (cost == -1) {
            active = false;
            initX = 0;
            initY = 0;
            return;
        }
        initX = (int) v.x;
        initY = (int) v.y;
        this.cost = cost;
        active = true;
    }

    /**
     * Sets the unit original position.
     *
     * @param coord the unit coordinates in world terrain coordinates.
     */
    public void setEntityInformation(Pair<Integer, Integer> coord, int cost) {
        if (cost == -1) {
            active = false;
            initX = 0;
            initY = 0;
            return;
        }
        initX = coord.first;
        initY = coord.second;
        this.cost = cost;
    }


    /**
     * Draw the path on the screen.
     *
     * @param batch The drawer.
     */
    public void draw(Batch batch) {
        if (!active) {
            return;
        }
        movements.draw(batch);
    }

    /**
     * Draws the valid emplacements.
     *
     * @param batch The drawer.
     */
    public void drawValid(Batch batch) {
        valid.draw(batch);
    }

    /**
     * Gets the unit's path as a string.
     *
     * @return The unit's path.
     */
    public String getPath() {
        return movements.getPath();
    }

    public Pair<Integer, Integer> getDestination() {
        return movements.getLastMovement();
    }

    /**
     * Reset the movement selector.
     */
    public void reset() {
        valid.reset();
        movements.reset();
        active = false;
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets.
     * @param data   The sprites coordinates.
     */
    public void showValids(Assets assets, Pair<List<Pair<?, ?>>, List<int[]>> data) {
        reset();
        Texture texture = assets.get(Assets.AssetDir.GUI.path() + "valids.png");
        valid.setData(texture, data.first, data.second, owner);
    }

    public void showValid(Assets assets, List<Pair<?, ?>> coordinates) {
        reset();
        Texture texture = assets.get(Assets.AssetDir.GUI.path() + "valids.png");
        ArrayList<int[]> intel = new ArrayList<>();
        for (int i = 0; i < coordinates.size(); i++) intel.add(new int[]{1, 1, 1, 1});
        valid.setData(texture, coordinates, intel, owner);
    }

    /**
     * Gets the next coordinate according to the next direction.
     *
     * @param c The direction.
     * @return The next coordinates in world terrain dimension
     */
    private Pair<Integer, Integer> getCoord(char c) {
        Pair<Integer, Integer> coord = movements.getLastMovement();
        switch (c) {
            case 'U':
                return new Pair<>(coord.first, coord.second + 1);
            case 'R':
                return new Pair<>(coord.first + 1, coord.second);
            case 'L':
                return new Pair<>(coord.first - 1, coord.second);
            case 'D':
                return new Pair<>(coord.first, coord.second - 1);
            default:
                return coord;
        }
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param next The next step.
     * @return a string that symbolizes the directions to take.
     */
    private String direction(Pair<Integer, Integer> next) {
        Pair<Integer, Integer> last = movements.getLastMovement();
        int dx = last.first - next.first;
        int dy = last.second - next.second;
        return directionSelector(dx, dy);
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param dx The next movement
     * @param dy The next movement
     * @return a string that symbolizes the directions to take.
     */
    private String directionSelector(int dx, int dy) {
        StringBuilder s = new StringBuilder();
        char d = (dx < 0) ? 'R' : 'L';
        int end = Math.abs(dx);
        s.append(String.valueOf(d).repeat(end));
        d = (dy < 0) ? 'U' : 'D';
        end = Math.abs(dy);
        s.append(String.valueOf(d).repeat(end));
        return s.toString();
    }

    /**
     * Removes MovementSelector
     */
    public void dispose() {
        this.reset();
    }

    /***************** setters and getters *****************/

    @Override
    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    /**
     * Path maker. It manages the path and the drawing of the arrows.
     */
    protected class Movements extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
        /**
         * Movements' path.
         */
        private final StringBuilder path;
        /**
         * Index that point to the last used sprite.
         */
        private int index;
        /**
         * Cost of the path.
         */
        private int currentCost;

        private Movements() {
            index = 0;
            path = new StringBuilder();
            currentCost = 0;
        }

        /**
         * Retrieves the last movements stored in movements. If movements is empty, gets the initial position.
         *
         * @return The last movement coordinates
         */
        private Pair<Integer, Integer> getLastMovement() {
            int x;
            int y;
            if (movements.index != 0) {
                var last = movements.get(index - 1).second;
                x = last.first;
                y = last.second;
            } else {
                x = initX;
                y = initY;
            }
            return new Pair<>(x, y);
        }

        /**
         * Retrieves the next to last movements stored in movements. If movements is empty, gets the initial position.
         *
         * @return The last movement coordinates
         */
        private Pair<Integer, Integer> getNextToLastMovement() {
            int x;
            int y;
            if ((movements.index != 0) && (movements.index != 1)) {
                var last = movements.get(index - 2).second;
                x = last.first;
                y = last.second;
            } else {
                x = initX;
                y = initY;
            }
            return new Pair<>(x, y);
        }

        /**
         * Adds the default movements to movements according to the tileIndex. Its follow the BFR order
         *
         * @param assets    The app assets.
         * @param tileIndex The tile's index in valid.
         */
        private void addDefaultMovement(Assets assets, int tileIndex) {
            movements.reset();
            var stack = valid.getDefault(tileIndex);
            while (!stack.empty()) {
                var tmp = stack.pop();
                String d = direction(tmp.first);
                add(assets, tmp.first, tmp.second, d.charAt(0));
            }
        }

        /**
         * Adds arrows according to the given path.
         *
         * @param assets The app assets
         * @param d      the path.
         */
        private boolean add(Assets assets, String d) {
            if (d.isBlank() || index + d.length() > cost) {
                return false;
            }
            for (int i = 0; i < d.length(); i++) {
                Pair<Integer, Integer> c = getCoord(d.charAt(i));
                addMovement(assets, c);
            }
            return true;
        }

        /**
         * Add a new step on the movement path.
         *
         * @param assets The app assets.
         * @param coord  The step coordinates.
         */
        private void add(Assets assets, Pair<Integer, Integer> coord, int tileIndex) {
            if (getLastMovement().equals(coord)) {
                return;
            }
            String d = direction(coord);
            if (d.isBlank() || isPresent(assets, coord, tileIndex)) {
                return;
            }
            if (currentCost + valid.getTileCost(tileIndex) > cost || d.length() > 1) {
                addDefaultMovement(assets, tileIndex);
                return;
            }
            add(assets, coord, tileIndex, d.charAt(0));
        }

        private void add(Assets assets, Pair<Integer, Integer> coord, int tileIndex, char d) {
            if (!reuse(d, assets, coord, tileIndex)) {
                Sprite sprite = new Sprite(getArrow(assets, d));
                if (add(new Pair<>(sprite, coord))) {
                    currentCost += valid.getTileCost(tileIndex);
                    path.append(d);
                }
            }
        }


        @Override
        public boolean add(Pair<Sprite, Pair<Integer, Integer>> o) {
            if (currentCost > cost) {
                return false;
            }
            index++;
            if (o.second.first < 0 || o.second.second < 0) {
                throw new RuntimeException();
            }
            o.first.setPosition(o.second.first * worldScale, o.second.second * worldScale);
            return super.add(o);
        }


        /**
         * Recycles an already built sprite.
         *
         * @param d          The direction encoding char.
         * @param assets     The app assets
         * @param coordinate The next coordinate
         * @return true if a sprite is recycled, false otherwise.
         */
        private boolean reuse(char d, Assets assets, Pair<Integer, Integer> coordinate, int tileIndex) {
            if (index >= size() || currentCost >= cost) {
                return false;
            }
            Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(index);
            tmp.first.setRegion(getArrow(assets, d));
            tmp.second = coordinate;
            tmp.first.setPosition(tmp.second.first * worldScale, tmp.second.second * worldScale);
            path.append(d);
            currentCost += valid.getTileCost(tileIndex);
            index++;
            return true;
        }

        /**
         * Checks if a coordinate is already present in the path.
         *
         * @param assets     The app assets.
         * @param coordinate The next movement coordinate.
         * @return true if the coordinate is present, false otherwise.
         */
        private boolean isPresent(Assets assets, Pair<Integer, Integer> coordinate, int tileIndex) {

            for (int i = index - 1; i >= 0; i--) {
                if (get(i).second.equals(coordinate)) {
                    index = i + 1;
                    path.delete(index, path.length());
                    get(i).first.setRegion(getArrow(assets, path.charAt(i)));
                    currentCost -= valid.getTileCost(tileIndex);
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the texture according to the direction.
         *
         * @param assets The app assets.
         * @param d      The direction.
         * @return The sprite texture.
         */
        private TextureRegion getArrow(Assets assets, char d) {
            TextureAtlas atlas =
                    assets.get(Assets.AssetDir.ARROWS.path() + "arrows.atlas", TextureAtlas.class);
            if (index > 0) {
                String last = String.valueOf(path.charAt(index - 1)) + d;
                get(index - 1).first.setRegion(atlas.findRegion(last));
            }
            return atlas.findRegion(String.valueOf(d));
        }

        /**
         * Draw the path on the screen.
         *
         * @param batch The drawer.
         */
        private void draw(Batch batch) {
            for (int i = 0; i < index; i++) {
                this.get(i).first.draw(batch);
            }
        }

        /**
         * Reset the movements' path and reset the list's index. All the build sprites
         * are now considered as free.
         */
        private void reset() {
            path.delete(0, path.length());
            index = 0;
            currentCost = 0;
        }

        /**
         * Gets the movements' path.
         *
         * @return the path.
         */
        private String getPath() {
            if (path.length() > cost) {
                path.delete(cost + 1, path.length());
            }
            return path.toString();
        }

    }

}
