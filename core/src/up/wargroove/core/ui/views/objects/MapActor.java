package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import up.wargroove.core.ui.controller.Controller;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

public class MapActor {
    private final GameMap map;

    private MapActor(World world, Stage stage, Controller controller) {
        float heightRatio = (stage.getHeight() / (world.getDimension().first * 64));
        float widthRatio = (stage.getWidth() / (world.getDimension().second * 64));
        float scale = Math.min(heightRatio, widthRatio);
        map = new GameMap(world, stage, null, controller, scale, true, false);
    }

    private MapActor() {
        this.map = null;
    }

    public static OrthogonalTiledMapRenderer buildMap(
            World world, Stage stage, Pair<Float, Float> mapSize, Controller controller
    ) {

        MapActor newMap = new MapActor(world, stage, controller);
        mapSize.first = (float) (newMap.getMap().getHeight() * newMap.getMap().getTileSize());
        mapSize.second = (float) (newMap.getMap().getWidth() * newMap.getMap().getTileSize());
        float heightRatio = (stage.getHeight() / mapSize.first);
        float widthRatio = (stage.getWidth() / mapSize.second);

        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(newMap.getMap(), Math.min(heightRatio, widthRatio));
        mapSize.first *= renderer.getUnitScale();
        mapSize.second *= renderer.getUnitScale();
        System.out.println(renderer.getUnitScale());
        return renderer;
    }

    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }

    public GameMap getMap() {
        return map;
    }

}
