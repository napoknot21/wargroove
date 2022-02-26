package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

/**
 * GameView movement selector.
 */
public class MovementSelector {
    static int counter = 0;
    /**
     * The tile scale between the view and the world.
     */
    private final float worldScale;
    /**
     * List of valid positions.
     */
    private final Valid valid;
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


    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public MovementSelector(float worldScale) {
        this.worldScale = worldScale;
        valid = new Valid();
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
    public synchronized void addMovement(Assets assets, Vector3 v) {
        this.addMovement(assets, new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Add a new step on the movement path.
     *
     * @param assets The app assets.
     * @param coord  The step coordinates.
     */
    public synchronized void addMovement(Assets assets, Pair<Integer, Integer> coord) {
        int tileIndex = valid.isValid(coord);
        if (tileIndex < 0) {
            movements.reset();
        } else {
            movements.add(assets, coord, tileIndex);
        }
    }

    /**
     * Sets the unit original position.
     *
     * @param v the unit coordinates in world terrain coordinates.
     */
    public void setEntityInformation(Vector3 v, int cost) {
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
        batch.begin();
        valid.draw(batch);
        movements.draw(batch);
        batch.end();
    }

    /**
     * Gets the unit's path as a string.
     *
     * @return The unit's path.
     */
    public String getPath() {
        return movements.getPath();
    }

    /**
     * Reset the movement selector.
     */
    public synchronized void reset() {
        valid.reset();
        movements.reset();
        active = false;
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     *  @param assets The app assets.
     * @param pair The sprites coordinates.
     */
    public void showValids(Assets assets, Pair<Vector<Pair<Integer, Integer>>, Vector<Pair<Integer, Integer>>> pair) {
        pair.first.forEach(v -> valid.add(assets.getTest(), v));
        valid.addIntel(pair.second);
    }

    /**
     * Gets the next coordinate according to the next direction.
     *
     * @param c The direction.
     * @return The next coordinates in world terrain dimension
     */
    private Pair<Integer, Integer> getCoord(char c) {
        var coord = movements.getLastMovement();
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
    private synchronized String direction(Pair<Integer, Integer> next) {
        Pair<Integer, Integer> last = movements.getLastMovement();
        int dx = last.first - next.first;
        int dy = last.second - next.second;
        return directionSelector(dx, dy);
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param x The next movement
     * @param y The next movement
     * @return a string that symbolizes the directions to take.
     */
    private synchronized String directionSelector(int x, int y) {
        StringBuilder s = new StringBuilder();
        char d = (x < 0) ? 'R' : 'L';
        int end = Math.abs(x);
        s.append(String.valueOf(d).repeat(end));
        d = (y < 0) ? 'U' : 'D';
        end = Math.abs(y);
        s.append(String.valueOf(d).repeat(end));
        return s.toString();
    }

    /**
     * List of valid positions.
     */
    private class Valid extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
        /**
         * Tile useful information. The order of intel is the same as this.
         */
        private final ArrayList<Pair<Integer, Integer>> intel;
        /**
         * Index that point to the last used sprite.
         */
        private int index;

        private Valid() {
            index = 0;
            intel = new ArrayList<>();
        }

        /**
         * Add a new sprite to the list if all the sprites are already used
         * otherwise it will use a free sprite.
         *
         * @param texture The sprite texture.
         * @param coord   The sprites coordinates.
         */
        private void add(Texture texture, Pair<Integer, Integer> coord) {
            int x = (int) (coord.first * worldScale);
            int y = (int) (coord.second * worldScale);
            if (this.index < this.size()) {
                var tmp = this.get(index);
                tmp.first.setPosition(x, y);
                tmp.second = coord;
                this.index++;
                return;
            }
            this.index++;
            Sprite sprite = new Sprite(texture);
            sprite.setPosition(x, y);
            this.add(new Pair<>(sprite, (coord)));
        }

        /**
         * Add all the tile's intel to intel.
         *
         * @param vector the list of intel.
         */
        private void addIntel(Vector<Pair<Integer, Integer>> vector) {
            intel.addAll(vector);
        }

        /**
         * Checks is the coordinate is valid.
         *
         * @param coordinate the coordinate that needed to be checked.
         * @return the index of the corresponding tile, -1 otherwise.
         */
        private int isValid(Pair<Integer, Integer> coordinate) {
            for (int i = 0; i < this.index; i++) {
                if (this.get(i).second.equals(coordinate)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Gets the tile movement cost.
         *
         * @param tileIndex the index of the tile.
         * @return the cost of moving on the tile.
         */
        private int getTileCost(int tileIndex) {
            return intel.get(tileIndex).second;
        }

        /**
         * Gets the default path used in the bfs.
         *
         * @param tileIndex the destination tile index of the path.
         * @return a stack with the character's path to the destination.
         */
        private Stack<Pair<Pair<Integer, Integer>, Integer>> getDefault(int tileIndex) {
            Stack<Pair<Pair<Integer, Integer>, Integer>> res = new Stack<>();
            int i = tileIndex;
            while (i >= 0) {
                var tmp = valid.get(i).second;
                res.push(new Pair<>(new Pair<>(tmp.first, tmp.second), i));
                i = intel.get(i).first;
            }
            return res;
        }

        /**
         * Reset movement path and list index. All the build sprites
         * are now considered free.
         */
        private void reset() {
            index = 0;
        }

        /**
         * Draw the path on the screen.
         *
         * @param batch The drawer.
         */
        private void draw(Batch batch) {
            for (int i = 0; i < index; i++) {
                valid.get(i).first.draw(batch);
            }
        }
    }

    /**
     * Path maker. It manages the path and the drawing of the arrows.
     */
    private class Movements extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
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
        private synchronized Pair<Integer, Integer> getLastMovement() {
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

        private synchronized void addDefaultMovement(Assets assets, int tileIndex) {
            movements.reset();
            var stack = valid.getDefault(tileIndex);
            while (!stack.empty()) {
                var tmp = stack.pop();
                add(assets, tmp.first, tmp.second);
            }
        }

        /**
         * Adds arrows according to the given path.
         *
         * @param assets The app assets
         * @param d      the path.
         */
        private synchronized boolean add(Assets assets, String d) {
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
        private synchronized void add(Assets assets, Pair<Integer, Integer> coord, int tileIndex) {
            if (getLastMovement().equals(coord)) {
                return;
            }
            String d = direction(coord);
            if (d.isBlank() || isPresent(assets, coord, tileIndex)) {
                return;
            }
            if (currentCost + valid.getTileCost(tileIndex) > cost) {
                addDefaultMovement(assets, tileIndex);
                return;
            }
            if (d.length() > 1) {
                addDefaultMovement(assets, tileIndex);
            } else if (!reuse(d.charAt(0), assets, coord, tileIndex)) {
                Sprite sprite = new Sprite(getArrow(assets, d.charAt(0)));
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
        private synchronized boolean reuse(char d, Assets assets, Pair<Integer, Integer> coordinate, int tileIndex) {
            if (index >= size() || currentCost >= cost) {
                return false;
            }
            Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(index);
            tmp.first.setTexture(getArrow(assets, d));
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
        private synchronized boolean isPresent(Assets assets, Pair<Integer, Integer> coordinate, int tileIndex) {

            for (int i = index - 1; i >= 0; i--) {
                if (get(i).second.equals(coordinate)) {
                    index = i + 1;
                    path.delete(index, path.length());
                    get(i).first.setTexture(getArrow(assets, path.charAt(i)));
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
        private synchronized Texture getArrow(Assets assets, char d) {
            if (index > 0 && index < cost) {
                String last = String.valueOf(path.charAt(index - 1)) + d;
                get(index - 1).first.setTexture(assets.get(Assets.AssetDir.ARROWS.getPath() + last + ".png"));
            }
            return assets.get(Assets.AssetDir.ARROWS.getPath() + d + ".png");
        }

        /**
         * Draw the path on the screen.
         *
         * @param batch The drawer.
         */
        private void draw(Batch batch) {
            for (int i = 0; i < index; i++) {
                Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(i);
                tmp.first.draw(batch);
            }
        }

        /**
         * Reset the movements' path and reset the list's index. All the build sprites
         * are now considered as free.
         */
        private synchronized void reset() {
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
