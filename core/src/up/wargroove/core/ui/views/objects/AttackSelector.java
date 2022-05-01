package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

import java.util.*;

public class AttackSelector implements Selector{

    private final Valid valid;
    private Pair<Integer,Integer> postionAttack;
    private Pair<Integer,Integer> targetPosition;
    private Pair<Integer, Integer> initialPosition;
    private String path = "";
    private int attackRange = 0;
    /**
     * Constructs a Movement selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public AttackSelector(float worldScale) {
        valid = new Valid(worldScale);
    }

    public void showValids(Assets assets, Pair<List<Pair<Integer, Integer>>, List<Pair<Integer, Integer>>> pair) {
        valid.reset();
        pair.first.forEach(v -> valid.add(assets.get( "data/sprites/world/attack.png", Texture.class), v));
        valid.addIntel(pair.second);

    }

    @Override
    public void showValid(Assets assets, List<Pair<Integer, Integer>> coordinates) {

    }

    @Override
    public void setEntityInformation(Vector3 v, int attackRange) {
        initialPosition = new Pair<>((int)v.x,(int) v.y);
        this.attackRange = attackRange;
    }

    @Override
    public void setEntityInformation(Pair<Integer, Integer> coord, int attackRange) {
        initialPosition = coord;
        this.attackRange = attackRange;
    }

    public void addMovement(Vector3 worldPosition, MovementSelector movementSelector, World world) {
        if (!isValidPosition(worldPosition)) {
            return;
        }
        targetPosition = new Pair<>((int) worldPosition.x, (int) worldPosition.y);
        postionAttack = breathFirstResearch(movementSelector,world);
        if (postionAttack == null) return;
        movementSelector.addMovement(Assets.getInstance(),postionAttack);
        path = movementSelector.getPath();
    }

    private Pair<Integer, Integer> breathFirstResearch(MovementSelector movements, World world) {
        Map<Integer, Boolean> checked = new HashMap<>();
        Queue<Pair<Integer, Integer>> emp = new LinkedList<>();
        List<Pair<Integer,Integer>> results = new LinkedList<>();
        int tileIndex;
        int range = attackRange;
        emp.add(new Pair<>(World.coordinatesToInt(targetPosition, world.getDimension()), range));
        while (range-- >= 0 && emp.size() > 0) {
            int size = emp.size();
            while(size-- > 0) {
                var element = emp.poll();
                List<Integer> list = world.adjacentOf(element.first);
                for (int lin : list) {
                    if (checked.containsKey(lin)) continue;
                    Pair<Integer, Integer> worldCoordinates = World.intToCoordinates(lin, world.getDimension());
                    if (worldCoordinates.equals(initialPosition)) return initialPosition;
                    tileIndex = movements.valid.isValid(worldCoordinates);
                    if (tileIndex >= 0) results.add(movements.valid.get(tileIndex).second);
                    checked.put(lin, attackRange >= 0);
                    emp.add(worldCoordinates);
                }
            }
        }
        results.sort((p1,p2) -> {
            double x1 = Math.pow(initialPosition.first - p1.first,2);
            double y1 = Math.pow(initialPosition.second - p1.second,2);
            double x2 = Math.pow(initialPosition.first - p2.first,2);
            double y2 = Math.pow(initialPosition.second - p2.second,2);
            return (int) (Math.sqrt(x1+y1) - Math.sqrt(x2+y2));
        });
        return results.get(0);
    }

    @Override
    public boolean isValidPosition() {
        return valid.isValidPosition();
    }

    @Override
    public boolean isValidPosition(Pair<Integer, Integer> c) {
        return valid.isValid(c) > -1;
    }

    @Override
    public boolean isValidPosition(Vector3 v) {
        return isValidPosition(new Pair<>((int) v.x, (int)v.y));
    }

    @Override
    public void draw(Batch batch) {
        valid.draw(batch);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Pair<Integer, Integer> getDestination() {
        return targetPosition;
    }

    public Pair<Integer, Integer> getPositionAttack() {
        return postionAttack;
    }

    @Override
    public void reset() {
        valid.reset();
        path = "";
        initialPosition = null;
        targetPosition = null;
        attackRange = 0;
        postionAttack = null;
    }

    public Pair<Integer, Integer> getInitialPosition() {
        return initialPosition;
    }
}
