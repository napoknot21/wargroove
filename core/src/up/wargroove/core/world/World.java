package up.wargroove.core.world;

import up.wargroove.utils.Pair;
import up.wargroove.utils.Log;

import java.util.Random;
import java.util.Vector;
import java.util.Stack;
import javax.sound.sampled.AudioInputStream;

public class World {
	
//	private Vector<Entity> entities;

	public String name;
	public String description;

	private Pair<Integer, Integer> dimension;
	private Biome biome;
	private Tile [] terrain;

	private final boolean fog;
	private AudioInputStream music;

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
		this.music = music;

		turn = 1;

//		entities   = new Vector<>();

	}

	/**
	 * Initialise le monde avec ou sans génération procédurale.
	 *
	 * @param boolean procède ou non à la génération
	 */

	public void initialize(boolean generation) {

		Log.print("Initialisation du monde ...");
		terrain = new Tile[dimension.first * dimension.second];

		if(generation) {
		
			Generator gen = new Generator(dimension);
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

			builder.append(tile);
			
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
