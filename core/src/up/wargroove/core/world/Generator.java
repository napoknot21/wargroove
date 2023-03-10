package up.wargroove.core.world;

import java.util.Random;
import java.util.Vector;
import up.wargroove.utils.Pair;

/**
 * Terrain generator.
 */
public class Generator {

    private static final float CELL_SUBDIVISION = 9.0f;
    private static final int POLYGON_DIMENSION = 4;
    private final Pair<Integer, Integer> dimension;
    private final GeneratorProperties properties;
    private final Tile[] terrain;
    private final Random rdSeed;
    private Pair<Double, Double>[][] generationGradients;


    /**
     * Initialization of the generator with the given arguments.
     * @param dimension  The world dimension.
     * @param properties The generator properties.
     */
    public Generator(Pair<Integer, Integer> dimension, GeneratorProperties properties) {

        this.dimension = dimension;
        this.properties = properties;
        terrain = new Tile[dimension.first * dimension.second];

        rdSeed = new Random();

        generateGradients();

        var gaussianRepInit = this.properties.gaussianRep;

        if (gaussianRepInit == null) {
            gaussianRepInit = new Vector<>();
        }

        if (gaussianRepInit.size() < Tile.PRIMARY_TILE_TYPE) {

            for (int k = 0; k < Tile.PRIMARY_TILE_TYPE; k++) {

                Tile.Type type = Tile.Type.values()[k];
                if (gaussianRepInit.contains(type)) {
                    continue;
                }
                gaussianRepInit.add(type);

            }
        }
        this.properties.gaussianRep = gaussianRepInit;
    }


    /**
     * Builds the terrain.
     * @return The built terrain.
     */
    public Tile[] build() {

        Vector<Pair<Integer, Integer>> mountainsCoordinates = new Vector<>();

        for (int k = 0; k < terrain.length; k++) {

            var coordinates = World.intToCoordinates(k, dimension);

            double noiseVal = noise(coordinates.first, coordinates.second);
            double normalized = 1.0 / (0.5 + Math.exp(properties.normalization * noiseVal)) - 1.0;

            int val = properties.repartitionFunction.apply(normalized);

            Tile.Type type = properties.gaussianRep.get(val);
            terrain[k] = new Tile(type);

            if (type == Tile.Type.MOUNTAIN) {
                mountainsCoordinates.add(coordinates);
            }
        }

        //Quantit?? de rivi??res ?? g??n??rer ?

        if (mountainsCoordinates.size() > 0) {
            generateRiver(mountainsCoordinates);
        }

        return terrain;
    }


    /**
     * Procedural generation based on Perlin noise
     */
    @SuppressWarnings("unchecked")
    private void generateGradients() {

        int sizeX = dimension.first + 1;
        int sizeY = dimension.second + 1;

        generationGradients = new Pair[sizeY][sizeX];

        for (int k = 0; k < sizeX * sizeY; k++) {

            double radius = Math.toRadians(rdSeed.nextFloat() * 360.0);

            double x = Math.cos(radius);
            double y = Math.sin(radius);

            Pair<Double, Double> vector = new Pair<>(x, y);
            generationGradients[k / sizeX][k % sizeX] = vector;
        }

    }


    /**
     * Set the type of certain tuiles
     */
    private void sharp() {}


    /**
     * Calculates the distance between a and b.
     * @param a The a's world coordinate.
     * @param b The b's world coordinate
     * @return The distance between a and b.
     */
    private double distance(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {

        int dx = Math.abs(a.first - b.first);
        int dy = Math.abs(a.second - b.second);

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }


    /**
     * Search and return the nearest coordinate tile as Pair object
     * @param from Tile coordinate
     * @param type Tile type
     * @return The nearest tile coordinate tile
     */
    private Pair<Integer, Integer> nearestOf(Pair<Integer, Integer> from, Tile.Type type) {

        int index = 0;
        double dist = -1.0;

        Pair<Integer, Integer> res = new Pair<>(-1, -1);

        for (Tile tile : terrain) {

            var to = World.intToCoordinates(index, dimension);
            double tmpDistance = distance(from, to);

            if (tile.getType() == type && (tmpDistance < dist || dist == -1.0)) {
                dist = tmpDistance;
                res.swap(to);
            }

            index++;
        }

        return res;
    }



    /**
     * Generates a river from the coordinates of a mountain
     * @param mountains mountain coordinates
     */
    private void generateRiver(Vector<Pair<Integer, Integer>> mountains) {

        int index = rdSeed.nextInt(mountains.size());
        var coordinates = mountains.get(index);

        mountains.removeElementAt(index);

        //Simulation of the flow from the nearest water point

        var nearWater = nearestOf(coordinates, Tile.Type.SEA);

        int nx;
        int ny;
        boolean swap = false;

        do {

            nx = nearWater.first - coordinates.first;
            ny = nearWater.second - coordinates.second;

            nx = nx == 0 ? 0 : nx / Math.abs(nx);
            ny = ny == 0 ? 0 : ny / Math.abs(ny);

            if (swap && nx != 0) {
                coordinates.first += nx;
            } else {
                coordinates.second += ny;
            }

            swap = !swap;
            if (!World.validCoordinates(coordinates, dimension)) {
                break;
            }

            int tile = World.coordinatesToInt(coordinates, dimension);
            terrain[tile] = new Tile(Tile.Type.RIVER);

        } while (nx != 0 || ny != 0);

    }


    /**
     * Allows more gradual interpolation
     * @param x between -1.0 and 1.0
     */
    private double smooth(double x) {
        return 1.0 / (1.0 + Math.exp(properties.smooth * x));
    }


    /**
     * Interpolation of a, b, c
     * @param a double
     * @param b double
     * @param w double
     * @return the Interpolation of a, b, c
     */
    private double interpolation(double a, double b, double w) {
        return a + smooth(w) * (b - a);
    }


    /**
     * Product of the vector w (1x2) previously generated, at the position of the integer (x, y) coordinate.
     * @param floorX integer x-coordinate
     * @param floorY integer y-coordinate
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the dot product of the difference vectors and w
     */
    private double cornerDotProduct(int floorX, int floorY, double x, double y) {

        double deltaX = x - (double) floorX;
        double deltaY = y - (double) floorY;

        var vector = generationGradients[floorX][floorY];

        double kx = deltaX * vector.first;
        double ky = deltaY * vector.second;

        return kx + ky;

    }


    /**
     * Noise from Perlin Noise
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Interpolation of the two interpolations obtained
     */
    private double noise(int x, int y) {

        double doubleX = (double) x / (CELL_SUBDIVISION/* * dimension.first*/);
        double double_y = (double) y / (CELL_SUBDIVISION/* * dimension.second*/);

        int intCoXAlpha = (int) Math.floor(doubleX);
        int intCoYAlpha = (int) Math.floor(double_y);

        int intCoXBeta = intCoXAlpha + 1;
        int intCoYBeta = intCoYAlpha + 1;

        /*
         * Interpolation:
         *
         * - On the two vectors of coordinate y_0 of the cell
         *
         * - On the two last ones
         *
         * - Interpolation of the two interpolations obtained
         */

        double cornerAlpha;
        double cornerBeta;

        double polX = doubleX - (double) intCoXAlpha;
        double polY = double_y - (double) intCoYAlpha;

        /* first interpolation */
        cornerAlpha = cornerDotProduct(intCoXAlpha, intCoYAlpha, doubleX, double_y);
        cornerBeta = cornerDotProduct(intCoXBeta, intCoYAlpha, doubleX, double_y);

        double interpolationAlpha = interpolation(cornerAlpha, cornerBeta, polX);

        /* second interpolation */
        cornerAlpha = cornerDotProduct(intCoXAlpha, intCoYBeta, doubleX, double_y);
        cornerBeta = cornerDotProduct(intCoXBeta, intCoYBeta, doubleX, double_y);

        double interpolationBeta = interpolation(cornerAlpha, cornerBeta, polX);

        return interpolation(interpolationAlpha, interpolationBeta, polY);
    }

}
