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
    private final float worldScale;
    private final Valids valids;
    private final Movements movements;
    private boolean active;
    private int initX;
    private int initY;
    private int cost;


    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public MovementSelector(float worldScale) {
        this.worldScale = worldScale;
        valids = new Valids();
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
        if (!valids.isValid(coord)) {
            movements.reset();
        } else if (movements.index > cost) {
            movements.addDefaultMovement(assets, coord);
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
        System.out.println(cost);
        System.out.println(cost);
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
        valids.draw(batch);
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
        valids.reset();
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
        vector.forEach(c -> valids.add(assets.getTest(), c));
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
        last.first -= next.first;
        last.second -= next.second;
        return directionSelector(last);
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param last The last movement.
     * @return a string that symbolizes the directions to take.
     */
    private String directionSelector(Pair<Integer, Integer> last) {
        return directionSelector(last.first, last.second);
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
        while (x != 0) {
            if (x > 0) {
                x--;
                s.append('L');
            } else {
                x++;
                s.append('R');
            }
        }
        while (y != 0) {
            if (y > 0) {
                y--;
                s.append('D');
            } else {
                y++;
                s.append('U');
            }
        }
        return s.toString();
    }

    private class Valids extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
        /**
         * Index that point to the last used sprite.
         */
        private int index;

        private Valids() {
            index = 0;
        }

        /**
         * Add a new sprite to the list if all the sprites are already used
         * else it will take a free sprite.
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

        private boolean isValid(Pair<Integer, Integer> coord) {
            for (int i = 0; i < this.index; i++) {
                if (this.get(i).second.equals(coord)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Reset the movement path and reset the list index. All the build sprites
         * are now considered as free.
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
                valids.get(i).first.draw(batch);
            }
        }
    }

    private class Movements extends ArrayList<Pair<Sprite, Pair<Integer, Integer>>> {
        private final StringBuilder path;
        private int index;

        private Movements() {
            index = 0;
            path = new StringBuilder();
        }

        private Pair<Integer, Integer> getDefaultPath(Assets a, Pair<Integer, Integer> c, int d, boolean h) {
            boolean neg = d < 0;
            for (int i = 1; i < Math.abs(d); i++) {
                if (h) {
                    c.first += (neg) ? -i : i;
                } else {
                    c.second += (neg) ? -i : i;
                }
                this.add(a, c);
            }
            return c;
        }

        /**
         * Retrieves the last movements stored in movements. If movements is empty, gets the initial positions.
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
            for (int i = 0; i < d.length(); i++) {
                Pair<Integer, Integer> c = getCoord(d.charAt(i));
                if (!reuse(d.charAt(i), assets, c)) {
                    Sprite sprite = new Sprite(getArrow(assets, d.charAt(i)));
                    sprite.setPosition(c.first * worldScale, c.second * worldScale);
                    movements.add(new Pair<>(sprite, c));
                }
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
            if (d.isBlank() || isPresent(coord)) {
                return;
            }
            if (d.length() > 1) {
                add(assets, d);
            } else if (!reuse(d.charAt(0), assets, coord)) {
                Sprite sprite = new Sprite(getArrow(assets, d.charAt(0)));
                sprite.setPosition(coord.first * worldScale, coord.second * worldScale);
                movements.add(new Pair<>(sprite, coord));
            }
        }

        private void addDefaultMovement(Assets assets, Pair<Integer, Integer> coord) {
            int dx = coord.first - initX;
            int dy = coord.second - initY;
            Pair<Integer, Integer> init = new Pair<>(initX, initY);
            movements.reset();
            var c = this.getDefaultPath(assets, init, dx, true);
            getDefaultPath(assets, c, dy, false);
        }

        private boolean nextTo(Pair<Integer, Integer> coord) {
            Pair<Integer, Integer> last = getLastMovement();
            return nextTo(last, coord);
        }

        private boolean nextTo(Pair<Integer, Integer> last, Pair<Integer, Integer> coord) {
            return nextTo(last.first, last.second, coord.first, coord.second);
        }

        private boolean nextTo(int x1, int y1, int x2, int y2) {
            return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) == 1;
        }


        private boolean reuse(char d, Assets assets, Pair<Integer, Integer> coord) {
            if (index >= size()) {
                return false;
            }
            Pair<Sprite, Pair<Integer, Integer>> tmp = this.get(index);
            tmp.first.setTexture(getArrow(assets, d));
            tmp.first.setPosition(coord.first * worldScale, coord.second * worldScale);
            tmp.second = coord;
            index++;
            path.append(d);
            return true;
        }

        private boolean isPresent(Pair<Integer, Integer> coord) {
            for (int i = index - 1; i >= 0; i--) {
                if (get(i).second.equals(coord)) {
                    index = i + 1;
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
            return assets.get(Assets.AssetDir.ARROWS.getPath() + d + ".png");
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
         * Reset the movement path and reset the list index. All the build sprites
         * are now considered as free.
         */
        private void reset() {
            path.delete(0, path.length());
            index = 0;
        }

        private String getPath() {
            return path.toString();
        }

    }

}
