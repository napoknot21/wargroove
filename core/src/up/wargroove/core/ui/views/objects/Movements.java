package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

/**
 * List of movements.
 */
public class Movements extends ArrayList<Sprite> {
    /**
     * Index that point to the last used sprite.
     */
    private int index = 0;

    /**
     * Add a new sprite to the list if all the sprites are already used
     * else it will take a free sprite.
     *
     * @param assets The app assets
     * @param coord The sprites coordinates
     */
    public void add(Assets assets, Pair<Integer, Integer> coord) {
        if (index == this.size()) {
            Sprite sprite = new Sprite(assets.getTest());
            sprite.setPosition(coord.first, coord.second);
            index++;
            super.add(sprite);
        } else {
            get(index).setPosition(coord.first, coord.second);
            index++;
        }
        Sprite sprite = new Sprite(assets.getTest());
        sprite.setPosition(coord.first, coord.second);
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
        for (int i = 0; i <= index; i++) {
            get(i).draw(batch);
        }
        batch.end();
    }
}
