package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.utils.BitSet;
import up.wargroove.utils.Log;
import up.wargroove.utils.Pair;
import up.wargroove.utils.Constants;
import up.wargroove.utils.functional.WPredicate;

import java.util.*;

public class World {

    private final int [] permutations;
    private final WorldProperties properties;
    private final Pair<Integer, Integer> dimension;
    private final int turn;
    private Optional<Integer> currentEntityLinPosition;
    private Tile[] terrain;

    private final WPredicate<Integer> canMoveOn = (k) -> {

        Tile toTile = terrain[k[Constants.WG_ZERO]];	

        if (toTile.entity.isPresent() || k[Constants.WG_TWO] == 0) return -1;

        BitSet bitset = new BitSet(toTile.getType().enc, 32);
        BitSet sub = bitset.sub(4 * k[Constants.WG_ONE], 4);

        int val = sub.toInt();

        return val == 0 ? -1 : k[2] - val;

    };

    private Stack<State> states;

    public World(WorldProperties properties) {

        this.properties = properties;
        this.dimension = properties.dimension;

	permutations = new int[]{-dimension.first, 1, dimension.first, -1};

        turn = 1; 

    }

    /**
     * Initialise le monde avec ou sans génération procédurale.
     *
     * @param generation procède ou non à la génération
     */

    public void initialize(boolean generation) {

        Log.print("Initialisation du monde ...");
        terrain = new Tile[dimension.first * dimension.second];

        if (generation && properties.genProperties != null) {

            Generator gen = new Generator(dimension, properties.genProperties);
            terrain = gen.build();

        } else {

            for (int k = 0; k < terrain.length; k++) terrain[k] = new Tile();

        }

        Log.print("Initialisation terminée ...");
    }

    public void push(State state) {

        states.push(state);

    }

    public void pop() {

        states.pop();

    }

    public State last() {

        return states.peek();

    }

    public Tile at(int x, int y) {

        return terrain[y * dimension.first + x];

    }

    public Tile at(Pair<Integer, Integer> coordinates) {

        return at(coordinates.first, coordinates.second);

    }

    public boolean addEntity(int linCoordinate, Entity entity) {

        Tile spawnTile = terrain[linCoordinate];
        if (spawnTile.entity.isPresent()) return false;

        terrain[linCoordinate].entity = Optional.of(entity);

        return true;

    }

    /**
     * Ajout d'une entité sur le monde
     *
     * @param Pair<Integer,Integer> la coordonnée
     * @param Entity l'entité
     *
     * @return le succès de l'ajout
     */

    public boolean addEntity(Pair<Integer, Integer> coordinate, Entity entity) {

        int linCoordinate = coordinatesToInt(coordinate, dimension);
        return addEntity(linCoordinate, entity);

    }

    /**
     * Vérouille l'accès sur l'entité courrante
     *
     * @param Pair<Integer,Integer> la coordonnée
     */

    public void scopeEntity(Pair<Integer, Integer> coordinate) {

        int linCoordinate = coordinatesToInt(coordinate, dimension);
        boolean exists = terrain[linCoordinate].entity.isPresent();

        if (exists) currentEntityLinPosition = Optional.of(linCoordinate);

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
     * @param int la coordonnée sur une dimension
     * @return le vecteur des coordonnées
     */

    public Vector<Integer> adjacentOf(int linCoordinate) {

        var adjacent = new Vector<Integer>(permutations.length);

        for (int delta : permutations) {

            int lco = linCoordinate + delta;

	    int lncMod = linCoordinate % dimension.first;
	    int lcoMod = lco % dimension.first;

            boolean isValid = Math.abs(lncMod - lcoMod) <= 1 && validCoordinates(lco, dimension);

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

    private Vector<Integer> breadthFirstSearch(int root, WPredicate<Integer> predicate) {

        Map<Integer, Boolean> checked = new HashMap<>();
	Queue<Pair<Integer, Integer>> emp = new LinkedList<>();

        Vector<Integer> res = new Vector<>();

	if(predicate == null) return res;
	
	Entity entity    = terrain[root].entity.get();
	Entity.Type type = entity.getType();

	int movementId   = type.movement.id;
	int movementCost = type.movementCost;

	var rootElement = new Pair<>(root, movementCost);

	emp.add(rootElement);	 

        while (emp.size() > 0) {

            var element = emp.poll();
            Vector<Integer> adjacent = adjacentOf(element.first);

            for (Integer lin : adjacent) {

                if (checked.containsKey(lin)) continue; 

                if ((movementCost = predicate.test(lin, movementId, element.second)) >= 0) {

		    var predicateArg = new Pair<Integer, Integer>(lin, movementCost);
                    res.add(lin);
                    emp.add(predicateArg);

                }

		checked.put(lin, movementCost >= 0);

            } 

        }

        return res;

    }

    /**
     * Recherche des tuiles valides pour
     * l'entité courrante
     *
     * @return le vecteur des positions valides
     */

    public Vector<Integer> validMovements() {

        Vector<Integer> positions = new Vector<>();

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

        return dimension;

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
    
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        int index = 0;

        for (Tile tile : terrain) {

            builder.append(tile).append(" ");

            if (++index % dimension.first == 0) builder.append('\n');

        }

        return builder.toString();

    }
}
