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

/**
 * Contains methods for uploading files to a GitHub repository through GitHub's REST API.
 * @author Afonso Sampaio
 */
public class SubmitToGitHub {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a PUT request to GitHub's REST API.
     * @param path The path to the ontology file to upload.
     * @param body The body of the PUT request.
     * @return The body of the response sent by the server.
     * @throws IOException If an I/O error occurs when sending or receiving.
     * @throws InterruptedException If the operation is interrupted.
     */
    private static String put(String path, String body) throws IOException, InterruptedException {
        String baseUrl = Configuration.getGitHubAPIURL();
        String authorization = Configuration.getGitHubAuthorizationToken();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
                .setHeader("Authorization", authorization)
                .PUT(BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Encodes the given file as a base 64 string.
     * @param file The file to be encoded.
     * @return The base-64 encoded string, if the encoding was successful; Null, otherwise.
     */
    private static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + file, e);
        }
    }

    /**
     * Creates a file to be sent through a PUT request to GitHub's REST API.
     * @param filename The name of the ontology file to upload.
     * @return The body of the response sent by the server.
     * @throws IOException If an I/O error occurs when sending or receiving.
     * @throws InterruptedException If the operation is interrupted.
     */
    public static String createFile(String filename) throws IOException, InterruptedException {
        File f = new File(filename);
        var encodedContent = encodeFileToBase64(f);
        var createMap = Map.of("message", "New file added", "content", encodedContent);
        String filepath = "/contents/" + f.getName();
        var requestBody = objectMapper.writeValueAsString(createMap);
        return put(filepath, requestBody);
    }
}
