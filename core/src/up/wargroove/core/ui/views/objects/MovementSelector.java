package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

import java.util.ArrayList;

/**
 * GameView movement selector.
 */
public class MovementSelector {
    private final float worldScale;
    private final ArrayList<Pair<Sprite, Pair<Integer, Integer>>> valid;
    private final ArrayList<Pair<Sprite, Pair<Integer, Integer>>> movements;
    private final StringBuilder path;
    private int initX;
    private int initY;
    /**
     * Index that point to the last used sprite.
     */
    private int index = 0;

    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public MovementSelector(float worldScale) {
        this.worldScale = worldScale;
        path = new StringBuilder();
        valid = new ArrayList<>();
        movements = new ArrayList<>();
        initX = 0;
        initY = 0;
    }

    /**
     * Reset the movement path and reset the list index. All the build sprites
     * are now considered as free.
     */
    public void reset() {
        path.delete(0, path.length());
        index = 0;
        movements.clear();
    }

    /**
     * Add a new step on the movement path.
     *
     * @param assets The app assets.
     * @param v      The step coordinates.
     */
    public void add(Assets assets, Vector3 v) {
        add(assets, new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Add a new step on the movement path.
     *
     * @param assets The app assets.
     * @param coord  The step coordinates.
     */
    public void add(Assets assets, Pair<Integer, Integer> coord) {
        String d = direction(coord);
        if (d.isBlank()) {
            return;
        }
        if (d.length() > 1) {
            add(assets, d);
        } else {
            Sprite sprite = new Sprite(getArrow(assets, d.charAt(0)));
            sprite.setPosition(coord.first * worldScale, coord.second * worldScale);
            movements.add(new Pair<>(sprite, coord));
        }
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
            Sprite sprite = new Sprite(getArrow(assets, d.charAt(i)));
            sprite.setPosition(c.first * worldScale, c.second * worldScale);
            movements.add(new Pair<>(sprite, c));
        }
    }

    /**
     * Gets the next coordinate according to the next direction.
     *
     * @param c The direction.
     * @return The next coordinates in world terrain dimension
     */
    private Pair<Integer, Integer> getCoord(char c) {
        var coord = getLastMovement();
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
     * Retrieves the last movements stored in movements. If movements is empty, gets the initial positions.
     *
     * @return The last movement coordinates
     */
    private Pair<Integer, Integer> getLastMovement() {
        int x;
        int y;
        if (movements.size() != 0) {
            var last = movements.get(movements.size() - 1).second;
            x = last.first;
            y = last.second;
        } else {
            x = initX;
            y = initY;
        }
        return new Pair<>(x, y);
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param next The next step.
     * @return a string that symbolizes the directions to take.
     */
    private String direction(Pair<Integer, Integer> next) {
        Pair<Integer, Integer> last = getLastMovement();
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
        String s = "";
        switch (x) {
            case -1:
                s += 'R';
                break;
            case 1:
                s += 'L';
                break;
            default:
        }
        switch (y) {
            case -1:
                s += 'U';
                break;
            case 1:
                s += 'D';
                break;
            default:
        }
        return s;
    }

    /**
     * Sets the unit original position.
     *
     * @param v the unit coordinates in world terrain coordinates.
     */
    public void setInitialPosition(Vector3 v) {
        initX = (int) v.x;
        initY = (int) v.y;
    }

    /**
     * Sets the unit original position.
     *
     * @param coord the unit coordinates in world terrain coordinates.
     */
    public void setInitialPosition(Pair<Integer, Integer> coord) {
        initX = coord.first;
        initY = coord.second;
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
    public void draw(Batch batch) {
        if (index == 0) {
            return;
        }
        batch.begin();
        for (int i = 0; i < index; i++) {
            valid.get(i).first.draw(batch);
        }
        movements.forEach(s -> s.first.draw(batch));
        batch.end();
    }

    /**
     * Gets the unit's path as a string.
     *
     * @return The unit's path.
     */
    public String getPath() {
        return path.toString();
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets
     * @param coord  The sprites coordinates
     */
    public void addValids(Assets assets, Pair<Integer, Integer> coord) {
        addValids(assets.getTest(), coord);
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param texture The sprite texture.
     * @param coord   The sprites coordinates
     */
    public void addValids(Texture texture, Pair<Integer, Integer> coord) {
        int x = (int) (coord.first * worldScale);
        int y = (int) (coord.second * worldScale);
        if (index < valid.size()) {
            var tmp = valid.get(index);
            tmp.first.setPosition(x, y);
            tmp.second = coord;
            index++;
            return;
        }
        index++;
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        valid.add(new Pair<>(sprite, coord));
    }

}
