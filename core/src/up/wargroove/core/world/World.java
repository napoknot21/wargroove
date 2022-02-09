package up.wargroove.core.world;

import up.wargroove.core.character.Entity;
import up.wargroove.utils.Pair;
import up.wargroove.utils.Log;

import java.util.function.Predicate;
import java.util.Random;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import javax.sound.sampled.AudioInputStream;

public class World {
	
	private Vector<Entity> entities;

	public String name;
	public String description;

	private Pair<Integer, Integer> dimension;
	private Biome biome;
	private Tile [] terrain;

	private final boolean fog;
	private AudioInputStream music;

	private GeneratorProperties genProperties;

	private Stack<State> states;
	private int turn;

	public World(WorldProperties properties) {

		this.name = properties.name;
		this.description = properties.description;

		this.dimension = properties.dimension;
		this.biome = properties.biome;
		this.terrain = properties.terrain;
		this.biome = properties.biome;	
		this.fog = properties.fog;
		this.music = properties.music;
		this.genProperties = properties.genProperties;

		turn = 1;

		entities = new Vector<>();

	}

	/**
	 * Initialise le monde avec ou sans génération procédurale.
	 *
	 * @param boolean procède ou non à la génération
	 */

	public void initialize(boolean generation) {

		Log.print("Initialisation du monde ...");
		terrain = new Tile[dimension.first * dimension.second];

		if(generation && genProperties != null) {
		
			Generator gen = new Generator(dimension, genProperties);
			terrain = gen.build();
		
		} else {
		
			for(int k = 0; k < terrain.length; k++) terrain[k] = new Tile();
		
		}

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

	/**
	 * Retourne les voisins d'une tuile
	 * selon le degré indiqué
	 *
	 */

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

	/**
	 * Validité de la coordonnée sur le plateau
	 * @param Pair<Integer,Integer> coordonnée
	 *
	 * @return l'appartenance des coordonnées au plateau
	 */

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

	public String toString() {

		StringBuilder builder = new StringBuilder();
		int index = 0;

		for(Tile tile : terrain) {

			builder.append(tile + " ");
			
			if(++index % dimension.first == 0) builder.append('\n');

		}

		return builder.toString();

	}

/*
	public void addEntity(Entity entity) {

		entities.add(entity);

	}

	public void moveEntity() {}

*/

}
