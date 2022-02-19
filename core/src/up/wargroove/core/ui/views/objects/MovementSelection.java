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
public class MovementSelection extends ArrayList<Sprite> {
    private final float worldScale;
    StringBuilder path;
    private int initX;
    private int initY;

    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public MovementSelection(float worldScale) {
        this.worldScale = worldScale;
        path = new StringBuilder();
        initX = 0;
        initY = 0;
    }

    /**
     * Reset the movement path.
     */
    public void reset() {
        path.delete(0, path.length());
        this.clear();
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
        char d = direction(coord);
        if (d == 'S') {
            return;
        }
        Sprite sprite = new Sprite(getArrow(assets, d));
        sprite.setPosition(coord.first * worldScale, coord.second * worldScale);
        super.add(sprite);
    }

    /**
     * Gets the next step direction according to the current position.
     *
     * @param next The next step.
     * @return a character that symbolizes the direction to take.
     */
    private char direction(Pair<Integer, Integer> next) {
        int x;
        int y;
        if (size() != 0) {
            var last = get(size() - 1);
            x = (int) (last.getX() / worldScale);
            y = (int) (last.getY() / worldScale);
        } else {
            x = initX;
            y = initY;
        }
        x -= next.first;
        y -= next.second;
        switch (x) {
            case -1:
                return 'R';
            case 1:
                return 'L';
            default:
        }
        switch (y) {
            case -1:
                return 'U';
            case 1:
                return 'D';
            default:
        }
        return 'S';
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
        batch.begin();
        this.forEach(s -> s.draw(batch));
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
}
