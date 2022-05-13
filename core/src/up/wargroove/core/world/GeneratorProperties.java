package up.wargroove.core.world;

import java.util.Vector;
import java.util.function.Function;

/**
 * Terrain generator properties.
 * @see Generator
 */
public class GeneratorProperties {

    public static final int DEFAULT_REPARTITION = 1;
    public static final double DEFAULT_NORMALIZATION = -3.5;
    public static final double DEFAULT_SMOOTH = -12.0;

    /**
     * The default distribution function is a max degree polynomial, an odd integer.
     * This represents the highest odd degree and d = 2*distribution + 1
     */
    public int repartition;

    /**
     * Allows you to specify a distribution function other than the one given by default
     */
    public Function<Double, Integer> repartitionFunction;

    /**
     * The normalization coefficient is used in the normalization function of the values obtained for each coordinate.
     * This normalization becomes uniform when the absolute value of the coefficient increases
     */
    public double normalization;

    /**
     * Similarly, applied to a sigmoid. It allows the more or less smooth transition between the different types of tiles
     */
    public double smooth;

    /**
     * Defines the frequency of tile types respecting a discrete normal law.
     * Types least likely to spawn are at the extremities
     */
    public Vector<Tile.Type> gaussianRep;


    /**
     * Constructor for GeneratorProperties.
     * Generate default properties
     */
    public GeneratorProperties() {
        this(DEFAULT_REPARTITION, DEFAULT_NORMALIZATION, DEFAULT_SMOOTH);
    }


    /**
     * Generate custom properties
     * @param repartition Higher polynomial degree.
     * @param normalization Normalization coefficient.
     * @param smooth Smooth value.
     */
    public GeneratorProperties(int repartition, double normalization, double smooth) {

        this.repartition = repartition;
        this.normalization = normalization;
        this.smooth = smooth;

        repartitionFunction = (x) -> (int) (Math.ceil(Tile.PRIMARY_TILE_TYPE / 2.0) * Math.pow(x, 2 * this.repartition + 1)) + 2;
    }

}
