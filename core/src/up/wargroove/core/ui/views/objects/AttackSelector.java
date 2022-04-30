package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

import java.util.List;

public class AttackSelector extends MovementSelector {
    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public AttackSelector(float worldScale) {
        super(worldScale);
    }

    public void showValids(Assets assets, Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> pair) {
        reset();
        Texture texture = assets.get(Assets.AssetDir.GUI.path()+"attack.png", Texture.class);
        pair.first.forEach(v -> valid.add(texture, v));
        valid.addIntel(pair.second);

    }
}
