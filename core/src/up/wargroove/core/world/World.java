package up.wargroove.core.world;

import up.wargroove.utils.Pair;
import up.wargroove.utils.Log;

import java.util.Vector;
import java.util.Stack;
import javax.sound.sampled.AudioInputStream;

public class World {
	
//	private Vector<Entity> entities;

	public String name;
	public String description;

	private Pair<Integer, Integer> dimension;
	private Biome biome;
	private Tale [] terrain;

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

	public void initialize() {

		Log.print("Initialisation du monde ...");

		terrain = new Tale[dimension.first * dimension.second];

		for(int k = 0; k < terrain.length; k++) {

			terrain[k] = new Tale();

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

/*
	public void addEntity(Entity entity) {

		entities.add(entity);

	}

*/

}
