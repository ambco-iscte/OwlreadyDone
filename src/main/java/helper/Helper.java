package helper;

import java.util.Collection;
import java.util.Set;
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
     * @param set Any set of elements of type T.
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element.
     * @return A new set given by applying the specified mapper function to the elements of the set.
     */
    public static <T, R> Set<R> map(Collection<T> set, Function<? super T, ? extends R> mapper) {
        return set.stream().map(mapper).collect(Collectors.toSet());
    }
}
