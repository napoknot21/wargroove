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
    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position, float scale) {
        super(position,structure, scale);
        System.out.println(scale);
        initialiseSprites();
        stage.addActor(this);
        String texture = getTextureName(structure).toLowerCase(Locale.ROOT);
        actualiseSprite(Assets.getInstance().get(Biome.GRASS).findRegion(texture));
        getSprite().setPosition(position.first * getTileSize(), position.second * getTileSize());
    }

    public StructureUI(Stage stage, Structure structure, Pair<Integer, Integer> position) {
        this(stage,structure,position,1);
    }

    private String getTextureName(Structure structure) {
        if (structure instanceof Recruitment) {
            return ((Recruitment)structure).getRecruitmentType() +"-"+structure.getFaction()+"-1";
        } else {
            return structure.getStructureType() +"-"+structure.getFaction()+"-1";
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!canMove()) exhaust();
        super.draw(batch, parentAlpha);
    }
}
