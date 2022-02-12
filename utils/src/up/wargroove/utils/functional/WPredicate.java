package up.wargroove.utils.functional;

/**
 * Prédicat k-uplet
 */

@FunctionalInterface
public interface WPredicate<T> {

	Integer test(T... k);

}
