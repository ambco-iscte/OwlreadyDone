package helper;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains general helper methods useful in some parts of the code.
 */
public class Helper {

    /**
     * Splits a camelCase or TitleCase string to include spaces.
     * @author https://stackoverflow.com/users/367273/npe
     */
    public static String camelCaseToSpaces(String str) {
        return String.join(" ", str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
    }

    /**
     * @param set Any set of elements of type T.
     * @param mapper a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *               <a href="package-summary.html#Statelessness">stateless</a>
     *               function to apply to each element.
     * @return A new set given by applying the specified mapper function to the elements of the set.
     */
    public static <T, R> Set<R> map(Set<T> set, Function<? super T, ? extends R> mapper) {
        return set.stream().map(mapper).collect(Collectors.toSet());
    }
}
