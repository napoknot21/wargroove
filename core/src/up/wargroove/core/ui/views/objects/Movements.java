package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

/**
 * List of movements.
 */
public class Movements extends ArrayList<Sprite> {
    private final float worldScale;
    /**
     * Index that point to the last used sprite.
     */
    private int index = 0;

    public Movements(float worldScale) {
        super();
        this.worldScale = worldScale;
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets
     * @param coord  The sprites coordinates
     */
    public void add(Assets assets, Pair<Integer, Integer> coord) {
        add(assets.getTest(), coord);
    }

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param texture The sprite texture.
     * @param coord  The sprites coordinates
     */
    public void add(Texture texture, Pair<Integer, Integer> coord) {
        int x = (int) (coord.first * worldScale);
        int y = (int) (coord.second * worldScale);
        if (index < size()) {
            get(index).setPosition(x, y);
        }
        index++;
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        super.add(sprite);
    }

    /**
     * Reset the list index. All the build sprites
     * are now considered as free.
     */
    public void reset() {
        index = 0;
    }

    @Override
    public void clear() {
        index = 0;
        super.clear();
    }

    /**
     * Draw all the movements sprites.
     *
     * @param batch the batch drawer
     */
    public void draw(Batch batch) {
        if (index == 0) {
            return;
        }
        batch.begin();
        for (int i = 0; i < index; i++) {
            get(i).draw(batch);
        }
        batch.end();
    }

    public int getIndex() {
        return index;
    }

    public float getScale() {
        return worldScale;
    }
}
