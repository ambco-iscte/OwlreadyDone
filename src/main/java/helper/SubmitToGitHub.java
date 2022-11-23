package helper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

import static java.util.Base64.getEncoder;
import static org.apache.commons.codec.binary.Base64.*;

public class SubmitToGitHub {
    private static final String authorization = "Bearer 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"; // change this token for the new repo
    private static final String baseUrl = "https://api.github.com/repos/uhfonso/TestRepo"; // change this repo as well

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String put(String path, String body) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder().uri(URI.create(baseUrl + path))
                .setHeader("Authorization", authorization)
                .PUT(BodyPublishers.ofString(body))
                .build();

        var response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        return response.body();
    }

    /**
    private static String getResourceFile(String filename) throws IOException {
        var fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        return new String(Objects.requireNonNull(fileStream).readAllBytes(), StandardCharsets.UTF_8);
    } **/

    private static String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public static String createFile(String filename) throws IOException, InterruptedException {
        File f = new File(filename);
        var encodedContent = encodeFileToBase64(f);
        var createMap = Map.of(
                "message", "New file added",
                "content", encodedContent);

        var requestBody = objectMapper.writeValueAsString(createMap);
        return put("/contents/example.owl", requestBody);
    }


}
