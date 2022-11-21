package helper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class SubmitToGitHub {
    private static final String authorization = "Bearer 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"; // change this token for the new repo
    private static final String baseUrl = "https://api.github.com/repos/uhfonso/TestRepo"; // change this repo as well

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String put(String path, String body) throws IOException, InterruptedException {
        System.out.println("entrei no put");
        var request = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
                .setHeader("Authorization", authorization)
                .PUT(BodyPublishers.ofString(body))
                .build();

        var response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        System.out.println(response);
        return response.body();
    }
    private static String getResourceFile(String filename) throws IOException {
        System.out.println(filename);
        System.out.println("Entrei no get resource!");
        var fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        System.out.println(fileStream);
        return new String(Objects.requireNonNull(fileStream).readAllBytes(), StandardCharsets.UTF_8);
    }


    public static String createFile(String filename) throws IOException, InterruptedException {
        System.out.println("Entrei no create file");
        var fileToAdd = getResourceFile(filename);
        System.out.println(fileToAdd.toString());
        var encodedContent = java.util.Base64.getEncoder().encodeToString(fileToAdd.getBytes());

        var createMap = Map.of(
                "message", "New file added",
                "content", encodedContent);

        var requestBody = objectMapper.writeValueAsString(createMap);
        System.out.println(requestBody);
        return put("/contents/Pizza.owl", requestBody);
    }


}
