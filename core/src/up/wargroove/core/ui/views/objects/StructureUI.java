package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.Biome;
import up.wargroove.core.world.Recruitment;
import up.wargroove.core.world.Structure;
import up.wargroove.utils.Pair;

import java.util.Locale;

public class StructureUI extends EntityUI {
    private final boolean isPlayable;
    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position, float scale) {
        super(position,structure, scale);
        stage.addActor(this);
        initialiseSprites();
        actualiseSprite(Assets.getInstance().get(structure));
        isPlayable = structure instanceof Recruitment;
    }

    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position) {
        this(stage,structure,position,1);
    }

    @Override
    public void positionChanged() {
        System.out.println();
        System.out.println(getCoordinates().first * getTileSize());
        getSprite().setPosition(getCoordinates().first * getTileSize(), getCoordinates().second * getTileSize());
        getStats().setPosition(getX()+getSprite().getWidth()-getStats().getWidth()-1,1);
        super.positionChanged();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isPlayable) exhaust();
        super.draw(batch, parentAlpha);
    }
}
