package up.wargroove.utils.functional;

import up.wargroove.utils.Pair;

/**
 * Prédicat k-uplet
 */

@FunctionalInterface
public interface WPredicate<T> {

	Pair<Integer,Integer> test(T... k);

}
