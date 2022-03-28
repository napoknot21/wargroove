package up.wargroove.core.ui.views.objects;

import up.wargroove.core.ui.Assets;
import up.wargroove.utils.Pair;

import java.util.Vector;

public class AttackSelector extends MovementSelector{
    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public AttackSelector(float worldScale) {
        super(worldScale);
    }

    public void showValids(Assets assets, Pair<Vector<Pair<Integer, Integer>>, Vector<Pair<Integer, Integer>>> pair) {
        reset();
        pair.first.forEach(v -> valid.add(assets.get( "data/sprites/world/attack.png"), v));
        valid.addIntel(pair.second);

    }
}
