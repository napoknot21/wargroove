package up.wargroove.core.world;

import com.badlogic.gdx.utils.Null;
import up.wargroove.core.character.Entity;
import up.wargroove.core.character.Faction;
import up.wargroove.utils.BitSet;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;
import up.wargroove.utils.Constants;
import up.wargroove.utils.functional.WPredicate;
import up.wargroove.utils.DBEngine;
import up.wargroove.utils.Database;
import up.wargroove.utils.DbObject;
import java.util.*;
import java.io.IOException;

public class World {

    private final int [] permutations;
    private final WorldProperties properties; 
 
    private Optional<Integer> currentEntityLinPosition;
    private Tile[] terrain;

    private Vector<Player> players;
    private int playerPtr = 0;

    private State currentState;

    private final WPredicate<Integer> canMoveOn = (k) -> {

        Tile toTile = terrain[k[Constants.WG_ZERO]];
       // if (toTile.entity.isPresent() || k[Constants.WG_TWO] == 0) return -1;

	    if(toTile.entity.isPresent()) return -2;
	    if(k[Constants.WG_TWO] == 0) return -1;

        BitSet bitset = new BitSet(toTile.getType().enc, 32);
        BitSet sub = bitset.sub(4 * k[Constants.WG_ONE], 4);

        int val = sub.toInt();

        return val == 0 ? -1 : k[2] - val;

    };

    private final WPredicate<Integer> canAttack = (k) -> {

	    Optional<Entity> rootEntity  = terrain[k[3]].entity, targetEntity = terrain[k[0]].entity;

	    if(!rootEntity.isPresent() || !targetEntity.isPresent()) return -1;

	    boolean status = canMoveOn.test(k) == -2;
        status &= targetEntity.get().getFaction() != rootEntity.get().getFaction();

        return status ? 1 : -1;

    };

    private Stack<State> states;

    public World(WorldProperties properties) {

        this.properties = properties;
        currentEntityLinPosition = Optional.empty();

	permutations = new int[] {
		-properties.dimension.first, 
		1, 
		properties.dimension.first, 
		-1
	};
    states = new Stack<>();
	players = new Vector<>();
	int amt = Math.min(properties.amt, Faction.values().length - 1);

	if(amt < Constants.WG_TWO) {

		amt = Constants.WG_TWO;

	}

	for(int k = 0; k < amt; k++) {

		Player p = new Player(Faction.values()[k], properties.getIncome());
        p.setName("Player "+ (k+1));
		players.add(p);

	}	

    public void addPlayer(Faction faction) {
        players.add(new Player(faction,properties.getIncome()));
    }

    public void removeLastPlayer(){
        if (players.isEmpty()) {
            return;
        }
        players.remove(players.size() - 1);
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
     * Initialise le monde avec ou sans génération procédurale.
     *
     * @param generation procède ou non à la génération
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

    private void nextTurn() {

	    states.push(currentState);
	    currentState = new State(); 

	    players.removeIf(p -> !p.nextTurn());

    }

    public boolean nextPlayer() {

	    playerPtr = (playerPtr + 1) % players.size();

	    if(playerPtr == 0) {

		    nextTurn();

	    }

	    return true;

    }

    public Player getCurrentPlayer() {

	    return players.get(playerPtr);

    }

    public int turns() {

	    return states.size();

    }

    public Tile at(int x, int y) {

        return terrain[y * properties.dimension.first + x];

    }

    @Null
    public Tile at(int linCoordinate) {
        if (!validCoordinates(linCoordinate,getDimension())) {
            return null;
        }
        return terrain[linCoordinate];
    }

    public Tile at(Pair<Integer, Integer> coordinates) {

        return at(coordinates.first, coordinates.second);

    }

    public boolean addEntity(int linCoordinate, Entity entity) {

        Tile spawnTile = terrain[linCoordinate];
        if (spawnTile.entity.isPresent()) return false;

        terrain[linCoordinate].entity = Optional.of(entity);
	    players.get(playerPtr).addEntity(entity);

        return true;

    }

    /**
     * Ajout d'une entité sur le monde
     *
     * @param coordinate la coordonnée
     * @param entity l'entité
     *
     * @return le succès de l'ajout
     */

    public boolean addEntity(Pair<Integer, Integer> coordinate, Entity entity) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        return addEntity(linCoordinate, entity);

    }
    
    public boolean delEntity(int linCoordinate, Entity entity) {

        Tile spawnTile = terrain[linCoordinate];
        terrain[linCoordinate].entity = Optional.empty();
        return true;

    }

    public boolean delEntity(Pair<Integer, Integer> coordinate, Entity entity) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        return delEntity(linCoordinate, entity);

    }

    /**
     * Vérouille l'accès sur l'entité courrante
     *
     * @param coordinate la coordonnée
     */

    public boolean scopeEntity(Pair<Integer, Integer> coordinate) {

        int linCoordinate = coordinatesToInt(coordinate, properties.dimension);
        boolean exists = terrain[linCoordinate].entity.isPresent();

        if (exists) currentEntityLinPosition = Optional.of(linCoordinate);

	    return exists;

    }

    /**
     * Devérouille l'accès sur l'entité courrante
     */

    public void unscopeEntity() {

        currentEntityLinPosition = Optional.empty();

    }

    /**
     * Retourne les tuiles adjacentes
     *
     * @param linCoordinate la coordonnée sur une dimension
     * @return le vecteur des coordonnées
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

    /*
     * Recherche par adjacence basée sur
     * un breadth-first-search.
     *
     * @param int root la racine de l'arbre
     * @param Predicate<Integer> un prédicat sur la validité de la recherche
     *
     * @return le vecteur des coordonnées valides
     */

    private Vector<Integer> coreBreadthFirstSearch(int root, WPredicate<Integer> predicate) {

        Map<Integer, Boolean> checked = new HashMap<>();
	    Queue<Pair<Integer, Integer>> emp = new LinkedList<>();

        Vector<Integer> res = new Vector<>();

	    if(predicate == null) return res;
	
	    Entity entity    = terrain[root].entity.get();
	    Entity.Type type = entity.getType();

	    int movementId   = entity.getMovement().id;
	    int movementCost = entity.getRange();

	    var rootElement = new Pair<>(root, movementCost);

	    emp.add(rootElement);

        while (emp.size() > 0) {

            var element = emp.poll();
            Vector<Integer> adjacent = adjacentOf(element.first);

            for (Integer lin : adjacent) {

                if (checked.containsKey(lin)) continue; 

                if ((movementCost = predicate.test(lin, movementId, element.second, root)) >= 0) {

		            var predicateArg = new Pair<Integer, Integer>(lin, movementCost);
                    res.add(lin);
                    emp.add(predicateArg);

                }

		        checked.put(lin, movementCost >= 0);

            }

        }

        return res;

    }

    private Vector<Pair<Integer,Pair<Integer,Integer>>> breadthFirstSearch(int root, WPredicate<Integer> predicate) {

        Map<Integer, Boolean> checked = new HashMap<>();
	    Queue<Pair<Integer, Integer>> emp = new LinkedList<>();

        Vector<Pair<Integer,Pair<Integer,Integer>>> res = new Vector<>();

	    if(predicate == null) return res;

	    Entity entity    = terrain[root].entity.get();

        int movementId   = entity.getMovement().id;
        int movementCost = entity.getRange();

        var rootElement = new Pair<>(root, movementCost);
    	int parentIndex = -1;

	    emp.add(rootElement);

        while (emp.size() > 0) {

            var element = emp.poll();
            Vector<Integer> adjacent = adjacentOf(element.first);


            for (Integer lin : adjacent) {

                if (checked.containsKey(lin)) continue;

                if ((movementCost = predicate.test(lin, movementId, element.second)) >= 0) {

		            var predicateArg = new Pair<Integer, Integer>(lin, movementCost);
                    BitSet bitset = new BitSet(terrain[lin].getType().enc, 32);
                    BitSet sub = bitset.sub(4 * movementId, 4);
                    Pair<Integer,Integer> intel = new Pair<>(parentIndex,sub.toInt());
                    res.add(new Pair<>(lin,intel));
                    emp.add(predicateArg);

                }

		        checked.put(lin, movementCost >= 0);

            }
            parentIndex++;

        }

        return res;

    }

    /**
     * Recherche des tuiles valides pour
     * l'entité courrante
     *
     * @return le vecteur des positions valides
     */
    
    public Vector<Pair<Integer,Pair<Integer,Integer>>> validMovements() {

        Vector<Pair<Integer,Pair<Integer,Integer>>> positions = new Vector<>();

        if (currentEntityLinPosition.isPresent()) {

            positions = breadthFirstSearch(currentEntityLinPosition.get(), canMoveOn);

        }

        return positions;

    }

    public boolean moveEntity(Integer linCoordinate) {

        if (currentEntityLinPosition.isEmpty()) return false;

        Entity e = terrain[currentEntityLinPosition.get()].entity.get();
        terrain[linCoordinate].entity = Optional.of(e);

        terrain[currentEntityLinPosition.get()].entity = Optional.empty();
        return true;

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
    
    @Null
    public Entity getScopedEntity(){
        if (currentEntityLinPosition.isEmpty() || terrain[currentEntityLinPosition.get()].entity.isEmpty()) {
            return null;
        }
        return terrain[currentEntityLinPosition.get()].entity.get();
    }

    /**
     * Validité de la coordonnée sur le plateau.
     *
     * @param linCoordinate les coordonnées
     * @return l'appartenance des coordonnées au plateau
     */

    public static boolean validCoordinates(Integer linCoordinate, Pair<Integer, Integer> dimension) {

        return validCoordinates(intToCoordinates(linCoordinate, dimension), dimension);

    }

    public static boolean validCoordinates(Pair<Integer, Integer> coordinates, Pair<Integer, Integer> dimension) {

        boolean zero = coordinates.first >= 0 && coordinates.second >= 0;
        boolean dim = coordinates.first < dimension.first && coordinates.second < dimension.second;

        return zero && dim;

    }

    public static Pair<Integer, Integer> intToCoordinates(int k, Pair<Integer, Integer> dimension) {

        int x = k % dimension.first;
        int y = k / dimension.first;

        return new Pair<>(x, y);

    }

    public static int coordinatesToInt(Pair<Integer, Integer> co, Pair<Integer, Integer> dimension) {

        return dimension.first * co.second + co.first;

    }

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

    public boolean save(Database db) {
        db.selectCollection("worlds");
        DbObject worldDBO = properties.toDBO();
        boolean status = db.insert(properties.getName(), worldDBO);
        db.flush();
        return status;
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
}
