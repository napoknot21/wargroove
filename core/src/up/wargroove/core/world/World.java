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
	private Tale [] terrain;

	private final boolean fog;
	private AudioInputStream music;

	private Stack<State> states;
	private int turn;

	private final static float CELL_SUBDIVISION = 8.0f;
	private final static int POLYGON_DIMENSION  = 4;

	private Pair<Double, Double> [][] generationGradients;

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

		if(generation) generateGradients();
		terrain = new Tale[dimension.first * dimension.second];

		for(int k = 0; k < terrain.length; k++) {

			if(generation) {
				
				double val = noise(k % dimension.first, (int) (k / dimension.second));
				Log.print("Noise value = " + val);

			} else
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
	 * Génération procédurale basée
	 * sur le bruit de Perlin
	 *
	 */

	private void generateGradients() {

		int sizeX = dimension.first + 1;
		int sizeY = dimension.second + 1;

		generationGradients = new Pair[sizeX][sizeY];
		Random rdSeed = new Random();

		for(int k = 0; k < sizeX * sizeY; k++) {
		
			double radius = rdSeed.nextFloat();
		
			double x = Math.cos(radius);
			double y = Math.sin(radius);

			Pair<Double, Double> vector = new Pair<>(x, y);
			generationGradients[(int) (k / sizeY)][k % sizeX] = vector;
		
		}

	}

	private double interpolation(double a, double b, double w) {

		return w * b + (1 - w) * a;

	}

	private double cornerDotProduct(int floor_x, int floor_y, double x, double y) {

		double deltaX = x - (double) floor_x;
		double deltaY = y - (double) floor_y;

		var vector = generationGradients[floor_x][floor_y];

		double kX = deltaX * vector.first;
		double kY = deltaY * vector.second;
	
		return kX + kY;	

	}

	private double noise(int x, int y) {

		/*
		 * Sous division de la cellule en 8
		 */

		double double_x = (float) x / (CELL_SUBDIVISION * dimension.first);
		double double_y = (float) y / (CELL_SUBDIVISION * dimension.second);

		int intCoXAlpha = (int) Math.floor(double_x);
		int intCoYAlpha = (int) Math.floor(double_y);

		double polX = double_x - (double) intCoXAlpha;
		double polY = double_y - (double) intCoYAlpha;

		int intCoXBeta = intCoXAlpha + 1;
		int intCoYBeta = intCoYAlpha + 1;

		/*
		 * Interpolation: 
		 * 	
		 * 	- Sur les deux vecteurs de coordonnée y_0
		 * 	de la cellulle
		 *
		 * 	- Sur les deux dernières
		 *
		 * 	- Interpolation des deux interpolations
		 * 	obtenues
		 */

		double cornerAlpha, cornerBeta;

		/*
		 * Première interpolation
		 */

		cornerAlpha = cornerDotProduct(intCoXAlpha, intCoYAlpha, double_x, double_y);
		cornerBeta  = cornerDotProduct(intCoXBeta, intCoYAlpha, double_x, double_y);

		double interpolationAlpha = interpolation(cornerAlpha, cornerBeta, polX);
		
		/*
		 * Seconde interpolation
		 */

		cornerAlpha = cornerDotProduct(intCoXAlpha, intCoYBeta, double_x, double_y);
		cornerBeta  = cornerDotProduct(intCoXBeta, intCoYBeta, double_x, double_y);

		double interpolationBeta = interpolation(cornerAlpha, cornerBeta, polX);

		return interpolation(interpolationAlpha, interpolationBeta, polX);

	}

/*
	public void addEntity(Entity entity) {

		entities.add(entity);

	}

	public void moveEntity() {}

*/

}
