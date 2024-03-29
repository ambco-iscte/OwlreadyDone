package helper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains general helper methods useful in some parts of the code.
 */
public class Helper {

    /**
     * Converts a collection of Strings to a JS/HTML-valid list of strings.
     * @param collection A collection of strings.
     * @return A string, compatible with HTML/JS, representing the original collection.
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
     * @param <T> The type of the original elements in the collection.
     * @param <R> The target type of the elements to include in the set.
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
     * @param <T> The type of the original elements in the collection.
     * @param <R> The target type of the elements to include in the sorted set.
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

    /**
     * Gets the value of a given field on an instance of the defining objects, and casts it to a specified type.
     * @param field The field
     * @param obj The object defining the field. Null, for static fields.
     * @param type The class to cast the value of the field to.
     * @param <T> A generic type.
     * @return The value of the given field, on the defining object, cast to type T.
     */
    public static <T> T getFieldValue(Field field, Object obj, Class<T> type) {
        try {
            return type.cast(field.get(obj));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
