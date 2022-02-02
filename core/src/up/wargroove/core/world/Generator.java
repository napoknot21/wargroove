package up.wargroove.core.world;

import up.wargroove.utils.Pair;
import up.wargroove.utils.Log;

import java.util.Random;
import java.util.Vector;

public class Generator {

	private Pair<Integer, Integer> dimension;
	private Tile [] terrain;

	private final static float CELL_SUBDIVISION = 8.0f;
	private final static int POLYGON_DIMENSION  = 4;

	private Pair<Double, Double> [][] generationGradients;

	private Random rdSeed;
	
	public Generator(Pair<Integer, Integer> dimension) {
	
		this.dimension = dimension;
		terrain = new Tile[dimension.first * dimension.second];
	
		rdSeed = new Random();
		
		generateGradients();
	
	}
	
	public Tile [] build() {
	
		float fillFactor = Tile.PRIMARY_TILE_TYPE / 2.0f;
		Vector<Pair<Integer, Integer>> mountainsCoordinates = new Vector<>();	

		for(int k = 0; k < terrain.length; k++) {
					
			var coordinates = World.intToCoordinates(k, dimension);	

			double noiseVal = noise(coordinates.first, coordinates.second);
			int val = (int) Math.floor((noiseVal + 1) * fillFactor);

			Tile.TileType type = Tile.TileType.values()[val];
			terrain[k] = new Tile(type);

			if(type == Tile.TileType.MOUNTAIN) {

				mountainsCoordinates.add(coordinates);

			}

		}

		/*
		 * Quantité de rivière à générer ?
		 */

		if(mountainsCoordinates.size() > 0) generateRiver(mountainsCoordinates);
		
		return terrain;
	
	}
	
	/*
	 * Génération procédurale basée
	 * sur le bruit de Perlin
	 */

	private void generateGradients() {

		int sizeX = dimension.first + 1;
		int sizeY = dimension.second + 1;

		generationGradients = new Pair[sizeY][sizeX];	

		for(int k = 0; k < sizeX * sizeY; k++) {
		
			double radius = Math.toRadians(rdSeed.nextFloat() * 360.0);
		
			double x = Math.cos(radius);
			double y = Math.sin(radius);

			Pair<Double, Double> vector = new Pair<>(x, y);
			generationGradients[(int) (k / sizeX)][k % sizeX] = vector;
		
		}

	}

	/**
	 * Ajuster le type de certaines tuiles
	 */

	private void sharp() {}

	private double distance(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
	
		int dX = Math.abs(a.first - b.first);
		int dY = Math.abs(a.second - b.second);

		return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

	}

	/**
	 * Renvoie la coordonnée de la tuile la plus proche
	 * du type indiqué
	 */

	private Pair<Integer, Integer> nearestOf(Pair<Integer, Integer> from, Tile.TileType type) {

		int index = 0;
		double dist = -1.0;

		Pair<Integer, Integer> res = new Pair<>();

		/*
		 * TODO
		 * !! Parcourir à partir de la coordonée
		 * initiale plutôt que de parcourir tout
		 * le plateau
		 */

		for(Tile tile : terrain) {			

			var to = World.intToCoordinates(index, dimension);
			double tmpDistance = distance(from, to);

			if(tile.getType() == type && (tmpDistance < dist || dist == -1.0)) {	

				dist = tmpDistance;
				res.swap(to);

			}

			index++;	

		}

		return res; 

	}

	/**
	 * Génère une rivière à partir des coordonnées
	 * d'une montagne
	 */

	private void generateRiver(Vector<Pair<Integer, Integer>> mountains) {

		int index = rdSeed.nextInt(mountains.size());
		var coordinates = mountains.get(index);

		mountains.removeElementAt(index);

		/*
		 * Simulation de l'écoulement
		 * à partir du point d'eau le
		 * plus proche 
		 */
	
		var nearWater = nearestOf(coordinates, Tile.TileType.SEA);

		int nx, ny;
		boolean swap = false;

		do {

			nx = nearWater.first - coordinates.first;
			ny = nearWater.second - coordinates.second;	

			nx = nx == 0 ? 0 : nx / Math.abs(nx);
			ny = ny == 0 ? 0 : ny / Math.abs(ny);

			if(swap && nx != 0) coordinates.first += nx;
			else coordinates.second += ny;

			swap = !swap;	

			int tile = World.coordinatesToInt(coordinates, dimension);
			terrain[tile] = new Tile(Tile.TileType.RIVER);

		} while(nx != 0 || ny != 0);

	}

	/**
	 * Permet une interpolation plus progressive
	 *
	 * @param double x compris entre -1.0 et 1.0
	 * 
	 */

	private double smooth(double x) {

		return 1.0 / (1.0 + Math.exp(-2.0 * x));

	}

	private double interpolation(double a, double b, double w) {
	
		return a + smooth(w) * (b - a);

	}

	/**
	 * Produit du vecteur w (1x2) préalablement généré, à
	 * la position de la coordonnée (x, y) entière
	 *
	 * @param int floor_x coordonnée en x entière
	 * @param int floor_y coordonnée en y entière
	 * @param double x coordonnée en x
	 * @param double y coordonnée en y
	 *
	 * @return le produit scalaire des vecteurs de différence et w
	 */

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

		double double_x = (float) x / (CELL_SUBDIVISION/* * dimension.first*/);
		double double_y = (float) y / (CELL_SUBDIVISION/* * dimension.second*/);

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

		return interpolation(interpolationAlpha, interpolationBeta, polY);

	}

}
