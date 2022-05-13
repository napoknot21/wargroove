package up.wargroove.core.world;

import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.core.character.Movement;
import up.wargroove.utils.BitSet;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;
import up.wargroove.utils.Constants;
import up.wargroove.utils.functional.WPredicate;
import up.wargroove.utils.DBEngine;
import up.wargroove.utils.Database;
import up.wargroove.utils.DbObject;

import java.util.*;

public class World {

    private final int[] permutations;
    private final WorldProperties properties;

    private Optional<Integer> currentEntityLinPosition;
    private Tile[] terrain;

    private Vector<Player> players;
    private int playerPtr = 0;

    private State currentState;

    /**
     * Predicate that search entities target to attack for bfs
     */
    private final WPredicate<Integer> canMoveOn = (k) -> {

        Optional<Entity> rootEntity = terrain[k[3]].entity;
        Tile toTile = terrain[k[Constants.WG_ZERO]];
        if (k[Constants.WG_ZERO].equals(k[3]) || rootEntity.isEmpty()) return new Pair<>(-1,0);
        if (k[Constants.WG_TWO] <= 0) return new Pair<>(-1, -2);

        BitSet bitset = new BitSet(toTile.getType().enc(), 32);
        BitSet sub = bitset.sub(4 * k[Constants.WG_ONE], 4);

        int val = sub.toInt();
        if (val == 0) return new Pair<>(-1, -2);
        else if (toTile.entity.isPresent()) {
            if (toTile.entity.get().getFaction().equals(rootEntity.get().getFaction())) {
                return new Pair<>(k[2] - val, -2);
            }
            else {
                return new Pair<>(-1,-2);
            }
        }
        else return new Pair<>(k[2] - val, 2);

    };


    /**
     * Predicate that take attack range for bfs
     */
    private final WPredicate<Integer> withinRange = (k) -> {
        int attackRange = k[4];
        if (k[2] >= 0) return new Pair<>(1, 0);
        if (attackRange > 0) {
            return new Pair<>(attackRange - 1, 1);
        }
        return new Pair<>(-1, -1);
    };


    /**
     * Predicate that search all structures targets to attack (and verify if it's possible)
     */
    private final WPredicate<Integer> canAttack = (k) -> {

        Optional<Entity> rootEntity = terrain[k[3]].entity, targetEntity = terrain[k[0]].entity;

        if (rootEntity.isEmpty() || targetEntity.isEmpty()) return new Pair<>(-1, 0);
        if (k[4] < 0) return new Pair<>(-2, 0);

        boolean status = targetEntity.get().getFaction() != rootEntity.get().getFaction();

        return new Pair<>(status ? 1 : -1, 0);

    };

    private Stack<State> states;

    /**
     * Constructor for World
     * @param properties WorldProperties
     */
    public World(WorldProperties properties) {

        this.properties = properties;
        this.terrain = properties.terrain;

        currentEntityLinPosition = Optional.empty();

        permutations = new int[]{
                -properties.dimension.first,
                1,
                properties.dimension.first,
                -1
        };
        states = new Stack<>();
        players = new Vector<>();
    }


    /**
     * Load all players to the game
     */
    public void loadPlayers() {
        int amt = Math.min(properties.amt, Faction.values().length - 1);

        if (amt < Constants.WG_TWO) {
            amt = Constants.WG_TWO;

        }
        int k=0;
        for (Tile t : terrain) {
            if (t.entity.isPresent() && t.entity.get() instanceof Stronghold) {
                Player p = new Player(t.entity.get().getFaction(), properties.getIncome());
                p.setName("Player " + (k + 1));
                players.add(p);
            }
        }

    }


    /**
     * Add of a new Player
     * @param faction player faction
     */
    public void addPlayer(Faction faction) {
        players.add(new Player(faction, properties.getIncome()));
    }


    /**
     * Remove last player from the players list
     */
    public void removeLastPlayer() {
        if (players.isEmpty()) {
            return;
        }
        players.remove(players.size() - 1);
    }


    /**
     * Remove a player from the players list
     * @param faction player faction
     * @return the new player removed
     */
    public Player removePlayer(Faction faction) {
        Player removed = null;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getFaction().equals(faction)) {
                removed = players.remove(i);
                if (i <= playerPtr) playerPtr--;
                break;
            }
        }
        return removed;
    }

    @Null
    public Player getPlayer(Faction faction) {
        for (Player player : players) {
            if (player.getFaction().equals(faction)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Test if there is only one player in the players list
     * @return True if it's the case, False else
     */
    public boolean isTheLastPlayer() {
        return players.size() == 1;
    }


    /**
     * Initializes the world with or without procedural generation.
     * @param generation generation or not
     */
    public void initialize(boolean generation) {

        Log.print("Initialisation du monde ...");
        terrain = new Tile[properties.dimension.first * properties.dimension.second];

        if (generation && properties.genProperties != null) {

            Generator gen = new Generator(properties.dimension, properties.genProperties);
            terrain = gen.build();

        } else {

            for (int k = 0; k < terrain.length; k++) terrain[k] = new Tile();

        }

        properties.terrain = terrain;

        Log.print("Initialisation terminée ...");
    }

    /**
     * Pushes a currentState into the state list
     */
    private void nextTurn() {
        states.push(currentState);
        currentState = new State();
    }


    /**
     * Next player
     */
    public void nextPlayer() {

        playerPtr = (playerPtr + 1) % players.size();

        if (playerPtr == 0) {

            nextTurn();

        }
    }


    /**
     * number of turns
     * @return number of turns
     */
    public int turns() {

        return states.size() + 1;

    }

    /**
     * Search a tile from a coordinate
     * @param x x-coordinate
     * @param y y-coordinate
     * @return
     */
    public Tile at(int x, int y) {

        return terrain[y * properties.dimension.first + x];

    }

    @Null
    public Tile at(int linCoordinate) {
        if (!validCoordinates(linCoordinate, getDimension())) {
            return null;
        }
        return terrain[linCoordinate];
    }

    /**
     * Check the tile from the Pair coordinates
     * @param coordinates The pair coordinate
     * @return
     */
    public Tile at(Pair<Integer, Integer> coordinates) {

        return at(coordinates.first, coordinates.second);

    }

    /**
     * Adding and entity with a linear coordinate
     * @param linCoordinate linear coordinate
     * @param entity the entity
     * @return success of adding
     */
    public boolean addEntity(int linCoordinate, Entity entity) {

        Tile spawnTile = terrain[linCoordinate];
        if (spawnTile.entity.isPresent()) return false;

        terrain[linCoordinate].entity = Optional.of(entity);
        players.get(playerPtr).addEntity(entity);

        return true;

    }


    /**
     * Adding an entity to the world
     * @param coordinate the coordinate
     * @param entity the entity
     * @return success of adding
     */
    public boolean addEntity(Pair<Integer, Integer> coordinate, Entity entity) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        return addEntity(linCoordinate, entity);

    }


    /**
     * delete an entity
     * @param linCoordinate linear coordinate
     * @param entity the entity
     * @return success of deletion
     */
    public boolean delEntity(int linCoordinate, Entity entity) {

        Tile spawnTile = terrain[linCoordinate];
        terrain[linCoordinate].entity = Optional.empty();
        return true;

    }


    /**
     * Delete an entity
     * @param coordinate Pair coordinate
     * @param entity the entity
     * @return success of deletion
     */
    public boolean delEntity(Pair<Integer, Integer> coordinate, Entity entity) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        return delEntity(linCoordinate, entity);

    }


    /**
     * Lock access to the current entity
     * @param coordinate the coordinate
     */
    public boolean scopeEntity(Pair<Integer, Integer> coordinate) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        boolean exists = terrain[linCoordinate].entity.isPresent();

        if (exists) currentEntityLinPosition = Optional.of(linCoordinate);

        return exists;

    }


    /**
     * unlock access to the current entity
     */
    public void unscopeEntity() {
        currentEntityLinPosition = Optional.empty();
    }


    /**
     * Update an entity from a specific tile
     * @param coordinate the coordinate tile
     */
    public void actualiseEntity(Pair<Integer, Integer> coordinate) {
        unscopeEntity();
        scopeEntity(coordinate);
    }


    /**
     * Verify if an entity is present in the coordinate
     * @param coordinate the coordinate tile
     * @return true if it's the case, false then
     */
    public boolean checkEntity(Pair<Integer, Integer> coordinate) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);

        return terrain[linCoordinate].entity.isPresent();
    }


    /**
     * Check the adjacent tiles
     * @param linCoordinate the one dimensin coordinate
     * @return the coordinate vector
     */
    public Vector<Integer> adjacentOf(int linCoordinate) {

        var adjacent = new Vector<Integer>(permutations.length);

        for (int delta : permutations) {

            int lco = linCoordinate + delta;

            int lncMod = linCoordinate % properties.dimension.first;
            int lcoMod = lco % properties.dimension.first;

            boolean isValid = Math.abs(lncMod - lcoMod) <= 1 && validCoordinates(lco, properties.dimension);

            if (isValid) adjacent.add(linCoordinate + delta);

        }

        return adjacent;

    }


    /**
     * Adjacency search based on a breadth-first-search.
     * @param root the root tree
     * @return the vector of valid coordinates
     */
    @SuppressWarnings("unchecked")
    private Vector<int[]> attackBreadthFirstSearch(int root) {

        Map<Integer, Integer> checked = new HashMap<>();
        HashMap<Integer,Pair<Integer,Integer>> mouvements = new HashMap<>();
        Queue<Pair<Integer, Pair<Integer, Integer>>> emp = new LinkedList<>();
        Vector<int[]> res = new Vector<>();
        mouvements.put(root,new Pair<>(root,1));

        if (terrain[root].entity.isEmpty()) return res;
        //a predicate on the validity of the search
        WPredicate<Integer>[] predicates = new WPredicate[]{canMoveOn, withinRange, canAttack};

        Entity entity = terrain[root].entity.get();

        if (entity.getMovement() == null || entity.getMovement() == Movement.NULL) return res;
        int movementId = entity.getMovement().id;
        int movementCost = entity.getMovRange();
        int attackRange = entity.getRange();

        var rootElement = new Pair<>(root, new Pair<>(movementCost, attackRange));
        int parentIndex = -1;

        emp.add(rootElement);

        while (emp.size() > 0) {

            Pair<Integer, Pair<Integer, Integer>> element = emp.poll();
            Vector<Integer> adjacent = adjacentOf(element.first);


            for (Integer lin : adjacent) {
                if (checked.containsKey(lin) && checked.get(lin) > 4) continue;


                boolean added = false;
                Pair<Integer, Integer> result = new Pair<>(-1, 0);

                /*
                The first predicates indicate if it is possible, the last informs the value of movement cost
                    The pair is constructed as follows:
                        first = result of the predicate
                        second indicates if first is the movement cost
                 */
                movementCost = element.second.first;
                attackRange = element.second.second;
                for (var p : predicates) {
                    //linearPosition, movementId, movementCost, currentAttackRange, rootPosition.
                    result = p.test(lin, movementId, movementCost, root, attackRange);
                    switch (Math.abs(result.second)) {
                        case 1:
                            attackRange = result.first;
                            break;
                        case 2:
                            movementCost = result.first;
                            if (movementCost >= 0) mouvements.put(lin,result);
                            break;
                        default:
                    }
                    if (result.first >= 0) {
                        if (!added) {
                            added = emp.add(new Pair<>(lin, new Pair<>(movementCost, attackRange)));
                        }
                    }
                }
                if (result.first >= 0 && correctAlignement(mouvements, lin, entity.getRange())) {
                    res.add(bfsBuildResultValue(movementId, parentIndex, lin, result.second));
                }
                checked.put(lin, checked.getOrDefault(lin, 0) + 1);
            }
            parentIndex++;

        }
        return res;
    }

    /**
     * Bfs value results
     * @param movementId id movement
     * @param parentIndex father node tree
     */
    private int[] bfsBuildResultValue(int movementId, int parentIndex, int lin, int valid) {
        BitSet bitset = new BitSet(terrain[lin].getType().enc(), 32);
        BitSet sub = bitset.sub(4 * movementId, 4);
        return new int[]{lin, parentIndex, sub.toInt(), valid};
    }

    /**
     * Correct alignment
     * @param mouvements movement hashmap
     * @param range range
     * @return true if success
     */
    private boolean correctAlignement(HashMap<Integer, Pair<Integer, Integer>> mouvements, int lin, int range) {
        Pair<Integer, Integer> pos = intToCoordinates(lin, getDimension());
        boolean valid = false;
        for (int i = 0; i <= range && !valid; i++) {
            valid = checkMouvement(mouvements, coordinatesToInt(new Pair<>(pos.first + i, pos.second), getDimension()));
            valid |= checkMouvement(mouvements, coordinatesToInt(new Pair<>(pos.first - i, pos.second), getDimension()));
            valid |= checkMouvement(mouvements, coordinatesToInt(new Pair<>(pos.first, pos.second + i), getDimension()));
            valid |= checkMouvement(mouvements, coordinatesToInt(new Pair<>(pos.first, pos.second - i), getDimension()));
        }
        return valid;
    }

    /**
     * check movement with validity
     * @param mouvements movement hashMap
     * @param lin validity
     * @return true if success
     */
    private boolean checkMouvement(HashMap<Integer, Pair<Integer, Integer>> mouvements, int lin){
        Pair<Integer, Integer> m = mouvements.get(lin);
        return m != null && m.second > 0;
    }

    @SafeVarargs
    private Vector<int[]> breadthFirstSearch(int root, WPredicate<Integer>... predicates) {

        Map<Integer, Integer> checked = new HashMap<>();
        Queue<Pair<Integer, Integer>> emp = new LinkedList<>();
        Vector<int[]> res = new Vector<>();

        if (predicates.length == 0 || terrain[root].entity.isEmpty()) return res;

        Entity entity = terrain[root].entity.get();

        if (entity.getMovement() == null || entity.getMovement() == Movement.NULL) return res;
        int movementId = entity.getMovement().id;
        int movementCost = entity.getMovRange();

        var rootElement = new Pair<>(root, movementCost);
        int parentIndex = -1;

        emp.add(rootElement);

        while (emp.size() > 0) {

            var element = emp.poll();
            Vector<Integer> adjacent = adjacentOf(element.first);


            for (Integer lin : adjacent) {

                if (checked.containsKey(lin) && checked.get(lin) > 4) continue;


                boolean added = false;
                Pair<Integer, Integer> result = new Pair<>(-1, 0);

                /*
                Les premiers predicats indique si c'est possible, le dernier renseigne la valeur de movement cost
                La paire est constuite comme suit:
                    first = resultat du predicat
                    second indique si first est le movement cost
                 */
                for (var p : predicates) {
                    //linearPosition, movementId, movementCost, rootLinPosition
                    result = p.test(lin, movementId, element.second, root);
                    if (Math.abs(result.second) == 2) {
                        movementCost = result.first;
                    }
                    if (result.first >= 0) {
                        if (!added) {
                            added = emp.add(new Pair<>(lin, movementCost));
                        }
                    }
                }
                if (result.first >= 0) {
                    res.add(bfsBuildResultValue(movementId, parentIndex, lin,result.second));
                }
                checked.put(lin, checked.getOrDefault(lin, 0) + 1);
            }
            parentIndex++;

        }
        return res;
    }


    /**
     * Finding valid tiles for the current entity
     * @return the vector of valid positions
     */
    public Vector<int[]> validMovements() {

        Vector<int[]> positions = new Vector<>();

        if (currentEntityLinPosition.isPresent()) {

            positions = breadthFirstSearch(currentEntityLinPosition.get(), canMoveOn);

        }

        return positions;

    }


    /**
     * Store in a vector all valid targets
     * @return the vector
     */
    public Vector<int[]> validTargets() {

        Vector<int[]> positions = new Vector<>();

        if (currentEntityLinPosition.isPresent()) {

            positions = attackBreadthFirstSearch(currentEntityLinPosition.get());

        }

        return positions;

    }


    /**
     * Verify the entity movement
     * @param linCoordinate linear coordinate
     * @return status (success or not)
     */
    public boolean moveEntity(Integer linCoordinate) {

        if (currentEntityLinPosition.isEmpty()) return false;

        Entity e = terrain[currentEntityLinPosition.get()].entity.get();
        terrain[currentEntityLinPosition.get()].entity = Optional.empty();
        terrain[linCoordinate].entity = Optional.of(e);

        return true;

    }


    @Null
    public Entity getScopedEntity() {
        if (currentEntityLinPosition.isEmpty() || terrain[currentEntityLinPosition.get()].entity.isEmpty()) {
            return null;
        }
        return terrain[currentEntityLinPosition.get()].entity.get();
    }

    /**
     * check the validity of the coordinate on the board.
     * @param linCoordinate the coordinates
     * @return the belonging of the coordinates to the board
     */
    public static boolean validCoordinates(Integer linCoordinate, Pair<Integer, Integer> dimension) {

        return validCoordinates(intToCoordinates(linCoordinate, dimension), dimension);

    }


    /**
     * check the validity of the coordinate on the board.
     * @param coordinates Pair coordinates
     * @param dimension dimension of coordinate
     * @return status
     */
    public static boolean validCoordinates(Pair<Integer, Integer> coordinates, Pair<Integer, Integer> dimension) {

        boolean zero = coordinates.first >= 0 && coordinates.second >= 0;
        boolean dim = coordinates.first < dimension.first && coordinates.second < dimension.second;

        return zero && dim;

    }


    /**
     * Transform an integer to coordinate pair
     * @param k integer
     * @param dimension Pair coordinate
     * @return A new pair structure
     */
    public static Pair<Integer, Integer> intToCoordinates(int k, Pair<Integer, Integer> dimension) {

        int x = k % dimension.first;
        int y = k / dimension.first;

        return new Pair<>(x, y);

    }

    /**
     * Transform a Pair coordinate to integer
     * @param co Pair coordinate
     * @param dimension pair dimension
     */
    public static int coordinatesToInt(Pair<Integer, Integer> co, Pair<Integer, Integer> dimension) {

        return dimension.first * co.second + co.first;

    }

    /**
     * Save config and setting of the world
     * @return
     */
    public boolean save() {

        DBEngine engine = DBEngine.getInstance();
        engine.connect();

        Database db = engine.getDatabase("wargroove");
        db.selectCollection("worlds");

        DbObject worldDBO = properties.toDBO();
        boolean status = db.insert(properties.getName(), worldDBO);

        db.flush();
        engine.disconnect();

        return status;

    }


    /**
     * save the world config in a database
     * @param db database
     * @param collectionName collection name database
     * @return status (success or not)
     */
    public boolean save(Database db, String collectionName) {
        db.selectCollection(collectionName);
        DbObject worldDBO = properties.toDBO();
        boolean status = db.insert(properties.getName(), worldDBO);
        db.flush();
        return status;
    }


    /**
     * save the world config in a database
     * @param db database
     */
    public boolean save(Database db) {
        return save(db, "worlds");
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        int index = 0;

        for (Tile tile : terrain) {

            builder.append(tile).append(" ");

            if (++index % properties.dimension.first == 0) builder.append('\n');

        }

        return builder.toString();

    }

    /***************** setters and getters *****************/

    public Player getCurrentPlayer() {
        return players.get(playerPtr);
    }

    public Pair<Integer, Integer> getDimension() {

        return properties.dimension;

    }

    public String getName() {

        return properties.getName();

    }

    public String getDescription() {

        return properties.getDescription();

    }

    public Tile[] getTerrain() {
        return terrain;
    }
}
