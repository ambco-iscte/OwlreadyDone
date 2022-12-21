package configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class containing methods for accessing the server's properties file.
 * @author Afonso Caniço
 * @author Afonso Sampaio
 * @author Gustavo Ferreira
 * @author Samuel Correia
 */
public class Configuration {

    private static final Properties properties = new Properties();

    /**
     * Loads a property list from the given input stream.
     * @param stream The input stream.
     * @throws IOException If an error occurred when reading from the input stream.
     */
    public static void load(InputStream stream) throws IOException {
        if (stream == null)
            throw new IllegalArgumentException("Input stream of configuration file must be non-null.");
        properties.load(stream);
    }

    /**
     * Searches for the property with the specified key/name in the property list.
     * @param name The key/name of the property.
     * @return The property, if found; Null, otherwise.
     */
    public static String getProperty(String name) {
        return properties.getProperty(name, null);
    }

    /**
     * @return The base GitHub repository URL, to be used in {@link helper.SubmitToGitHub}.
     */
    public static String getGitHubBaseURL() {
        return getProperty("GITHUB_BASE_URL");
    }

    /**
     * @return The GitHub authorization token, to be used in {@link helper.SubmitToGitHub}.
     */
    public static String getGitHubAuthorizationToken() {
        return getProperty("GITHUB_AUTH_TOKEN");
    }
}
