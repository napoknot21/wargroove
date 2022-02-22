package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Vector;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

/**
 * GameView movement selector.
 */
public class MovementSelector {
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
        if (movements.index > cost || !valid.isValid(coord)) {
            movements.reset();
        } else {
            movements.add(assets, coord);
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
    public void reset() {
        valid.reset();
        movements.reset();
        active = false;
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets
     * @param vector The sprites coordinates
     */
    public void showValids(Assets assets, Vector<Pair<Integer, Integer>> vector) {
        vector.forEach(c -> valid.add(assets.getTest(), c));
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
    private String direction(Pair<Integer, Integer> next) {
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
    private String directionSelector(int x, int y) {
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
         * Index that point to the last used sprite.
         */
        private int index;

        private Valid() {
            index = 0;
        }

        /**
         * Add a new sprite to the list if all the sprites are already used
         * otherwise it will use a free sprite.
         *
         * @param texture The sprite texture.
         * @param coord   The sprites coordinates
         */
        private void add(Texture texture, Pair<Integer, Integer> coord) {
            int x = (int) (coord.first * worldScale);
            int y = (int) (coord.second * worldScale);
            if (this.index < this.size()) {
                Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(index);
                tmp.first.setPosition(x, y);
                tmp.second = coord;
                this.index++;
                return;
            }
            this.index++;
            Sprite sprite = new Sprite(texture);
            sprite.setPosition(x, y);
            this.add(new Pair<>(sprite, coord));
        }

        private boolean isValid(Pair<Integer, Integer> coordinate) {
            for (int i = 0; i < this.index; i++) {
                if (this.get(i).second.equals(coordinate)) {
                    return true;
                }
            }
            return false;
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

        private Movements() {
            index = 0;
            path = new StringBuilder();
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
         * Adds arrows according to the given path.
         *
         * @param assets The app assets
         * @param d      the path.
         */
        private void add(Assets assets, String d) {
            if (d.isBlank()) {
                return;
            }
            for (int i = 0; i < d.length() && index < cost; i++) {
                Pair<Integer, Integer> c = getCoord(d.charAt(i));
                addMovement(assets, c);
            }
        }

        /**
         * Add a new step on the movement path.
         *
         * @param assets The app assets.
         * @param coord  The step coordinates.
         */
        private void add(Assets assets, Pair<Integer, Integer> coord) {
            String d = direction(coord);
            if (d.isBlank() || isPresent(assets, coord)) {
                return;
            }
            if (d.length() > 1) {
                add(assets, d);
            } else if (!reuse(d.charAt(0), assets, coord)) {
                Sprite sprite = new Sprite(getArrow(assets, d.charAt(0)));
                sprite.setPosition(coord.first * worldScale, coord.second * worldScale);
                add(new Pair<>(sprite, coord));
                path.append(d);

            }
        }

        @Override
        public boolean add(Pair<Sprite, Pair<Integer, Integer>> spritePairPair) {
            index++;
            return super.add(spritePairPair);
        }


        /**
         * Recycles an already built sprite.
         *
         * @param d          The direction encoding char.
         * @param assets     The app assets
         * @param coordinate The next coordinate
         * @return true if a sprite is recycled, false otherwise.
         */
        private boolean reuse(char d, Assets assets, Pair<Integer, Integer> coordinate) {
            if (index >= size() || index >= cost || path.length() >= cost) {
                return false;
            }
            Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(index);
            tmp.first.setTexture(getArrow(assets, d));
            tmp.first.setPosition(coordinate.first * worldScale, coordinate.second * worldScale);
            tmp.second = coordinate;
            path.append(d);
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
        private boolean isPresent(Assets assets, Pair<Integer, Integer> coordinate) {
            for (int i = index - 1; i >= 0; i--) {
                if (get(i).second.equals(coordinate)) {
                    index = i + 1;
                    path.delete(index, path.length());
                    get(i).first.setTexture(getArrow(assets, path.charAt(index - 1)));
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
        private Texture getArrow(Assets assets, char d) {
            if (index > 0) {
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
            int end = Math.min(cost, index);
            for (int i = 0; i < end; i++) {
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
