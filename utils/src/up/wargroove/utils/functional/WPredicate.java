package up.wargroove.utils.functional;

import up.wargroove.utils.Pair;

/**
 * Prédicat k-uplet
 */

@FunctionalInterface
public interface WPredicate<T> {
	@SuppressWarnings("unchecked")
	Pair<Integer,Integer> test(T... k);

}
