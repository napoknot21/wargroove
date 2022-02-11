package up.wargroove.core.world;

import java.util.Vector;
import java.util.function.Function;

public class GeneratorProperties {

	public static final int DEFAULT_REPARTITION = 1;
	public static final double DEFAULT_NORMALIZATION = -3.5;
	public static final double DEFAULT_SMOOTH = -12.0;

	/*
	 * La fonction de répartition par
	 * défaut est un polynôme de degré
	 * max, un entier impair.
	 *
	 * Représente le degré impair le plus haut
	 * et d = 2*repartition + 1
	 */

	public int repartition;
	
	/*
	 * Permet d'indiquer une fonction de
	 * répartition autre que celle donnée
	 * par défaut
	 *
	 */

	public Function<Double, Integer> repartitionFunction;	

	/*
	 * Le coefficient de normalisation
	 * est utilisé dans la fonction de
	 * normalisation des valeurs obtenues
	 * pour chaque coordonnée. Cette normalisation
	 * devient uniforme quand la valeur
	 * absolue du coef grandit
	 */

	public double normalization;

	/*
	 * De même, appliqué sur une sigmoïde.
	 * Il permet la transition plus ou moins
	 * douce entre les différents types de
	 * tuiles.
	 */

	public double smooth;

	/*
	 * Définit la fréquence de types de tuiles
	 * respectant une loi normale discrète.
	 * Les types les moins enclins à apparaîtres
	 * sont aux extrêmités
	 */

	public Vector<Tile.Type> gaussianRep;

	public GeneratorProperties() {

		this(DEFAULT_REPARTITION, DEFAULT_NORMALIZATION, DEFAULT_SMOOTH);

	}

	public GeneratorProperties(int repartition, double normalization, double smooth) {

		this.repartition = repartition;
		this.normalization = normalization;
		this.smooth = smooth;

		repartitionFunction =
			(x) -> (int) (Math.ceil(Tile.PRIMARY_TILE_TYPE / 2.0) * Math.pow(x, 2 * this.repartition + 1)) + 2; 

	}

}
