package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.utils.Pair;
import up.wargroove.utils.Log;
import up.wargroove.utils.BitSet;

import java.util.function.Predicate;
import java.util.Random;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import javax.sound.sampled.AudioInputStream;

public class World {

	private WorldProperties properties;

	private Optional<Integer> currentEntityLinPosition;

	private Pair<Integer, Integer> dimension;
	private final int [] adjDeltas;

	private Tile [] terrain;

	private Stack<State> states;
	private int turn;

	private Predicate<Pair<Integer, Integer>> canMoveOn = (linCoordinates) -> {

		Tile toTile = terrain[linCoordinates.second];
		Tile fromTile = terrain[linCoordinates.first];

		Entity e = fromTile.entity.get();

		if(toTile.entity.isPresent() || e.tmpCost == 0) return false;
	
		BitSet bitset = new BitSet(toTile.getType().enc, 32);
		BitSet sub = bitset.sub(4 * 1/*e.getType().movement.id*/, 4);

		int val = sub.toInt();

		if(val > e.tmpCost) return false;

		return true;

	};

	public World(WorldProperties properties) {

		this.properties = properties;
		this.dimension = properties.dimension;

		adjDeltas = new int [] {-dimension.first, 1, dimension.first, -1};
		turn = 1;

		//entities = new HashMap<>();

	}

	/**
	 * Initialise le monde avec ou sans génération procédurale.
	 *
	 * @param generation procède ou non à la génération
	 */

	public void initialize(boolean generation) {

		Log.print("Initialisation du monde ...");
		terrain = new Tile[dimension.first * dimension.second];

		if(generation && properties.genProperties != null) {
			Generator gen = new Generator(dimension, properties.genProperties);
			terrain = gen.build();

		} else {

			for(int k = 0; k < terrain.length; k++) terrain[k] = new Tile();

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

	/*
	 * Retourne les voisins d'une tuile.
	 * selon le degré indiqué
	 *
	 */

	/*
	public List<Tile> neighbours(Pair<Integer, Integer> coordinates, Predicate<Tile> pred, int deg) {

		ArrayList<Tile> array = new ArrayList<>();

		if(!validCoordinates(coordinates, dimension)) return array;

		int beginX = coordinates.first - deg;
		int beginY = coordinates.second - deg;

		if(beginX < 0) beginX = 0;
		if(beginY < 0) beginY = 0;

		var startCoordinates = new Pair<Integer, Integer>(beginX, beginY);

		final int bor = 2 * deg + 1;
		final int per = (int) Math.pow(bor, 2) - (int) Math.pow(bor - 2, 2);

		var directionalVector = new Pair<Boolean, Boolean>(true, false);
		int blCoef = 1;

		for(int k = 0; k < per; k++) {

			if(validCoordinates(startCoordinates, dimension)) {

				Tile tile = at(startCoordinates);
				if(pred == null || pred.test(tile)) array.add(tile);

				//predValue |= pred.test(tile);

			}

			startCoordinates.first += (directionalVector.first ? 1 : 0) * blCoef;
			startCoordinates.second += (directionalVector.second ? 1 : 0) * blCoef;

			int cor = (k + 1) % (bor - 1);

			if(k > 0 && cor == 0) {

				directionalVector.first = !directionalVector.first;
				directionalVector.second = !directionalVector.second;

				if((k + 1) == per / 2) blCoef *= -1;

			}

		}

		return array;

	}
	*/

	/**
	 * Validité de la coordonnée sur le plateau.
	 * @param coordinates les coordonnées
	 *
	 * @return l'appartenance des coordonnées au plateau
	 */

	public static boolean validCoordinates(Integer linCoordinate, Pair<Integer, Integer> dimension) {

		return validCoordinates(intToCoordinates(linCoordinate, dimension), dimension);

	}

	public static boolean validCoordinates(Pair<Integer, Integer> coordinates, Pair<Integer, Integer> dimension) {

		boolean zero = coordinates.first >= 0 && coordinates.second >= 0;
		boolean dim  = coordinates.first < dimension.first && coordinates.second < dimension.second;

		return zero && dim;

	}

	public static Pair<Integer, Integer> intToCoordinates(int k, Pair<Integer, Integer> dimension) {

		int x = k % dimension.first;
		int y = (int) (k / dimension.first);

		return new Pair<>(x, y);

	}

	public static int coordinatesToInt(Pair<Integer, Integer> co, Pair<Integer, Integer> dimension) {

		return dimension.first * co.second + co.first;

	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		int index = 0;

		for(Tile tile : terrain) {

			builder.append(tile + " ");

			if(++index % dimension.first == 0) builder.append('\n');

		}

		return builder.toString();

	}

	public boolean addEntity(int linCoordinate, Entity entity) {

		Tile spawnTile = terrain[linCoordinate];
		if(spawnTile.entity.isPresent()) return false;

		terrain[linCoordinate].entity = Optional.of(entity);
		
		return true;

	}

	public boolean addEntity(Pair<Integer, Integer> coordinate, Entity entity) {

		int linCoordinate = coordinatesToInt(coordinate, dimension);
		return addEntity(linCoordinate, entity);	

	}

	public void scopeEntity(Pair<Integer, Integer> coordinate) {

		int linCoordinate = coordinatesToInt(coordinate, dimension);
		boolean exists = terrain[linCoordinate].entity.isPresent();

		if(exists) currentEntityLinPosition = Optional.of(linCoordinate);	

	}

	public void unscopeEntity() {

		currentEntityLinPosition = Optional.empty();

	}

	public Vector<Integer> adjacentOf(int linCoordinate) {

		var adjacent = new Vector<Integer>(adjDeltas.length);

		for(int delta : adjDeltas) {

			int lco = linCoordinate + delta;
			boolean isValid = validCoordinates(lco, dimension);

			if(isValid) adjacent.add(linCoordinate + delta);

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

	private Vector<Integer> breadthFirstSearch(int root, Predicate<Pair<Integer, Integer>> predicate) {

		Map<Integer, Boolean> checked = new HashMap<>();
		Queue<Integer> emp = new LinkedList<>();

		Vector<Integer> res = new Vector<>();

		emp.add(root);

		while(emp.size() > 0) {

			var element = emp.poll();
			Vector<Integer> adjacent = adjacentOf(element);

			for(Integer lin : adjacent) {

				if(checked.containsKey(lin)) continue;

				checked.put(lin, true);	
				var couple = new Pair<Integer, Integer>(root, lin);

				if(predicate == null || predicate.test(couple)) {
			
					res.add(lin);
					emp.add(lin);

				}	

			}

			terrain[root].entity.get().tmpCost--;

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

		if(currentEntityLinPosition.isPresent()) {	

			positions = breadthFirstSearch(currentEntityLinPosition.get(), canMoveOn);

		}

		return positions;

	}

	public boolean moveEntity(Integer linCoordinate) {
	
		if(!currentEntityLinPosition.isPresent()) return false;
		
		Entity e = terrain[currentEntityLinPosition.get()].entity.get();
		terrain[linCoordinate].entity = Optional.of(e);

		terrain[currentEntityLinPosition.get()].entity = Optional.empty();	
		return true;

	}


	public Pair<Integer, Integer> getDimension() {

		return dimension;
	
	}
}
