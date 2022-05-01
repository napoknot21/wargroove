package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

import java.util.List;

public interface Selector {

    boolean isValidPosition();

    boolean isValidPosition(Pair<Integer, Integer> c);

    boolean isValidPosition(Vector3 v);

    /**
     * Draw the path on the screen.
     *
     * @param batch The drawer.
     */
    void draw(Batch batch);

    /**
     * Gets the unit's path as a string.
     *
     * @return The unit's path.
     */
    String getPath();

    Pair<Integer, Integer> getDestination();

    /**
     * Reset the movement selector.
     */
    void reset();

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets.
     * @param pair   The sprites coordinates.
     */
    void showValids(Assets assets, Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> pair);

    void showValid(Assets assets, List<Pair<Integer, Integer>> coordinates);

    /**
     * Sets the unit original position.
     *
     * @param v the unit coordinates in world terrain coordinates.
     */
    void setEntityInformation(Vector3 v, int cost) ;

    /**
     * Sets the unit original position.
     *
     * @param coord the unit coordinates in world terrain coordinates.
     */
    void setEntityInformation(Pair<Integer, Integer> coord, int cost);
}
