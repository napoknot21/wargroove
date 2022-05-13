package up.wargroove.core.ui.views.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import up.wargroove.core.ui.Assets;
import up.wargroove.core.world.World;
import up.wargroove.utils.Pair;

import java.util.*;

public class AttackSelector implements Selector {

    private final Valid valid;
    private final Valid availableAttackPositions;
    private Pair<Integer, Integer> postionAttack;
    private Pair<Integer, Integer> targetPosition;
    private Pair<Integer, Integer> initialPosition;
    private String path = "";
    private int attackRange = 0;
    private boolean active;
    private boolean owner = false;

    /**
     * Constructs a Attack selector.
     *
     * @param worldScale The world scale used to place the sprites.
     */
    public AttackSelector(float worldScale) {
        valid = new Valid(worldScale);
        availableAttackPositions = new Valid(worldScale) {
            @Override
            protected void reset() {
                path = "";
                postionAttack = null;
                super.reset();
            }
        };
        active = false;
    }

    /**
     * @param assets The app assets.
     * @param data
     */
    public void showValids(Assets assets, Pair<List<Pair<?, ?>>, List<int[]>> data) {
        reset();
        Texture texture = assets.get(Assets.AssetDir.GUI.path() + "attack.png", Texture.class);
        valid.setData(texture, data.first, data.second, owner);
    }

    /**
     * Add a new step on the attack path.
     *
     * @param worldPosition the position
     */
    public void addMovement(Vector3 worldPosition) {
        if (!isValidPosition(worldPosition)) {
            return;
        }
        targetPosition = new Pair<>((int) worldPosition.x, (int) worldPosition.y);
    }

    @Override
    public void showValid(Assets assets, List<Pair<?, ?>> coordinates) {

    }

    /**
     * Use vector to change the initialPosition and assigned new attackRange
     *
     * @param v           the unit coordinates in world terrain coordinates.
     * @param attackRange entity's range attack
     */
    @Override
    public void setEntityInformation(Vector3 v, int attackRange) {
        initialPosition = new Pair<>((int) v.x, (int) v.y);
        this.attackRange = attackRange;
    }

    /**
     * Change in terrain coordinaites the initialPosition and assigned new attackRange
     *
     * @param coord       In world terrain coordinates.
     * @param attackRange entity's range attack
     */
    @Override
    public void setEntityInformation(Pair<Integer, Integer> coord, int attackRange) {
        initialPosition = coord;
        this.attackRange = attackRange;
    }

    /**
     * Actualise the owner, and for the others, attackSelector is show in black
     *
     * @param owner only player how keeps the right colors in attackSelector
     */
    @Override
    public void setOwner(boolean owner) {
        this.owner = owner;
        if (!owner) {
            valid.forEach(p -> p.first.setColor(Color.BLACK));
        }
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    /**
     * Verify the possibles positions and add it int hte
     *
     * @param movements
     * @param world
     */
    public void setAvailableAttackPosition(MovementSelector movements, World world) {
        availableAttackPositions.reset();
        if (!owner) return;
        Pair<?, ?>[] results = breathFirstResearch(movements, world);
        if (results.length == 0) {
            return;
        }
        Texture texture = Assets.getInstance().get(Assets.AssetDir.GUI.path() + "attackPosition.png", Texture.class);
        availableAttackPositions.setData(texture, List.of(results), owner);
        active = true;
    }

    /**
     * Defines attack postions according to movement selector since a position and fills the path
     *
     * @param worldPosition the position
     * @param movements
     */
    public void selectAttackPosition(Vector3 worldPosition, MovementSelector movements) {
        if (!owner) return;
        if (!isPositionAvailable(worldPosition)) {
            availableAttackPositions.reset();
        } else {
            Pair<Integer, Integer> pos = new Pair<>((int) worldPosition.x, (int) worldPosition.y);
            postionAttack = pos;
            movements.addMovement(Assets.getInstance(), pos);
            path = movements.getPath();
        }
    }

    /**
     * Calculate the possibles attacks positions
     *
     * @param movements
     * @param world
     * @return
     */
    private Pair<?, ?>[] breathFirstResearch(MovementSelector movements, World world) {
        Map<Integer, Boolean> checked = new HashMap<>();
        Queue<Integer> emp = new LinkedList<>();
        Set<Pair<Integer, Integer>> results = new HashSet<>();
        int tileIndex;
        int range = attackRange;
        emp.add(World.coordinatesToInt(targetPosition, world.getDimension()));
        while (range-- > 0 && emp.size() > 0) {
            int size = emp.size();
            while (size-- > 0) {
                var element = emp.poll();
                List<Integer> list = world.adjacentOf(element);
                for (int lin : list) {
                    if (checked.containsKey(lin)) continue;

                    Pair<Integer, Integer> worldCoordinates = World.intToCoordinates(lin, world.getDimension());
                    tileIndex = movements.valid.isValid(worldCoordinates);
                    if (checkAlignement(worldCoordinates)) {
                        if (tileIndex >= 0) {
                            results.add(movements.valid.get(tileIndex).second);
                        } else if (worldCoordinates.equals(initialPosition)) {
                            results.add(initialPosition);
                        }
                    }
                    checked.put(lin, attackRange >= 0);
                    emp.add(World.coordinatesToInt(worldCoordinates, world.getDimension()));
                }
            }
        }
        return results.toArray(new Pair[0]);
    }

    /**
     * Check if the target and c, are vertical or horizonatal
     *
     * @param c coordinates
     * @return
     */
    private boolean checkAlignement(Pair<Integer, Integer> c) {
        return c.first - targetPosition.first == 0 || c.second - targetPosition.second == 0;
    }

    @Override
    public boolean isValidPosition() {
        return valid.isValidPosition();
    }

    /**
     * Check if v is a possible target
     *
     * @param c the coordinate that needed to be checked.
     * @return
     */

    @Override
    public boolean isValidPosition(Pair<Integer, Integer> c) {
        return valid.isValid(c) > -1;
    }

    /**
     * Check if v is a possible target
     *
     * @param v the vector that needed to be checked.
     * @return
     */
    @Override
    public boolean isValidPosition(Vector3 v) {
        return isValidPosition(new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Check if v is a possible position to attack
     *
     * @param v the vector that needed to be checked.
     * @return
     */
    public boolean isPositionAvailable(Vector3 v) {
        return isPositionAvailable(new Pair<>((int) v.x, (int) v.y));
    }

    /**
     * Check if v is a possible position to attack
     *
     * @param c the coordinate that needed to be checked.
     * @return
     */

    public boolean isPositionAvailable(Pair<Integer, Integer> c) {
        return availableAttackPositions.isValid(c) > -1;
    }

    /**
     * @param batch The drawer.
     */
    @Override
    public void draw(Batch batch) {
        valid.draw(batch);
        availableAttackPositions.draw(batch);
    }

    /**
     * @return Way from the attacker to the target
     */
    @Override
    public String getPath() {
        if (targetPosition == null || postionAttack == null) return "";
        int dx = targetPosition.first - postionAttack.first;
        int dy = targetPosition.second - postionAttack.second;
        return path + getAttackDirection(dx, dy);
    }

    @Override
    public Pair<Integer, Integer> getDestination() {
        return targetPosition;
    }

    public Pair<Integer, Integer> getPositionAttack() {
        return postionAttack;
    }

    /**
     * Free all the attributes of the class
     */
    @Override
    public void reset() {
        active = false;
        valid.reset();
        availableAttackPositions.reset();
        path = "";
        initialPosition = null;
        targetPosition = null;
        attackRange = 0;
        postionAttack = null;
    }

    /**
     * * Compare two points to get the direction
     *
     * @param dx first point
     * @param dy second point
     * @return the last step on the movement path.
     */

    private char getAttackDirection(int dx, int dy) {
        if (dx > 0) {
            return 'R';
        } else if (dx < 0) {
            return 'L';
        } else if (dy > 0) {
            return 'U';
        } else {
            return 'D';
        }
    }

    public Pair<Integer, Integer> getInitialPosition() {
        return initialPosition;
    }

    public boolean isActive() {
        return active;
    }
}
