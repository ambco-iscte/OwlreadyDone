package helper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains general helper methods useful in some parts of the code.
 */
public class Helper {

    /**
     * Converts a collection of Strings to a JS/HTML-valid list of strings.
     */
    public static String toJSStringList(Collection<String> collection) {
        return "[" + collection.stream().collect(Collectors.joining("','", "'", "'")) + "]";
    }

    /**
     * Maps a collection to a set using a specified mapping function.
     * @param collection Any set of elements of type T.
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element.
     * @return A new set given by applying the specified mapper function to the elements of the set.
     */
    public static <T, R> Set<R> map(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        return collection.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * Maps a collection to a sorted set using a specified mapping function.
     * @param collection Any set of elements of type T.
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element.
     * @param comparator A comparator of elements of type R.
     * @return A sorted set containing the sorted, unique elements of the original collection.
     */
    public static <T, R> SortedSet<R> mapSorted(Collection<T> collection, Function<? super T, ? extends R> mapper, Comparator<R> comparator) {
        return collectionToSortedSet(map(collection, mapper), comparator);
    }

    /**
     * Converts a collection of elements to a sorted set.
     * @param collection The original, not necessarily sorted, collection.
     * @param comparator The comparator used to sort the elements in the collection.
     * @param <T> The type of the parameters in the collection.
     * @return A SortedSet containing the sorted, unique elements in the original collection.
     */
    public static <T> SortedSet<T> collectionToSortedSet(Collection<T> collection, Comparator<T> comparator) {
        TreeSet<T> tree = new TreeSet<>(comparator);
        tree.addAll(collection);
        return tree;
    }
}
