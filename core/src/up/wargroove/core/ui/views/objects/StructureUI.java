package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Structure;
import up.wargroove.utils.Pair;

import java.util.Locale;

/**
 * The structure sprite.
 *
 * @see up.wargroove.core.ui.views.objects.EntityUI
 * @see Structure
 */
public class StructureUI extends EntityUI {
    private final boolean isPlayable;

    /**
     * Inits the drawable with the structure information. The sprites will be scaled.
     *
     * @param stage     The stage where the actor will be added.
     * @param structure The world' structure.
     * @param position  The world's structure position.
     * @param scale     The sprites scale.
     */
    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position, float scale) {
        super(position, structure, scale);
        stage.addActor(this);
        initialiseSprites();
        actualiseSprite(Assets.getInstance().get(structure));
        isPlayable = structure instanceof Recruitment;
    }

    /**
     * Inits the drawable with the structure information.
     *
     * @param stage     The stage where the actor will be added.
     * @param structure The world' structure.
     * @param position  The world's structure position.
     */
    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position) {
        this(stage, structure, position, 1);
    }

    /**
     * Move the StructureUI, his sprite and his stats
     */
    @Override
    public void positionChanged() {
        getSprite().setPosition(getCoordinates().first * getTileSize(), getCoordinates().second * getTileSize());
        getStats().setPosition(getX() + getSprite().getWidth() - getStats().getWidth() - 1, 1);
        super.positionChanged();
    }

    /**
     * @param batch       The drawer
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isPlayable) exhaust();
        super.draw(batch, parentAlpha);
    }
}
