package helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.util.Map;

import static java.util.Base64.getEncoder;

// TODO: JAVADOC on class and methods
public class SubmitToGitHub {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String put(String path, String body) throws IOException, InterruptedException {
        String baseUrl = Configuration.getGitHubBaseURL();
        String authorization = Configuration.getGitHubAuthorizationToken();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
                .setHeader("Authorization", authorization)
                .PUT(BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        return response.body();
    }

    /*
    private static String getResourceFile(String filename) throws IOException {
        var fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        return new String(Objects.requireNonNull(fileStream).readAllBytes(), StandardCharsets.UTF_8);
    } */

    /**
     * Encodes the given file as a base 64 string.
     * @param file The file to be encoded.
     * @return The base-64 encoded string, if the encoding was successful; Null, otherwise.
     */
    private static String encodeFileToBase64(File file) {
        if (file == null)
            return null;
        try {
            return getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static String createFile(String filename) throws IOException, InterruptedException {
        String encodedContent = encodeFileToBase64(new File(filename));
        Map<String, String> createMap = Map.of("message", "New file added", "content", encodedContent);

        var requestBody = objectMapper.writeValueAsString(createMap);
        return put("/contents/example.owl", requestBody); // TODO: example.owl? Surely this is a placeholder
    }
}
