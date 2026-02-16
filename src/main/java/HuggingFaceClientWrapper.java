import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import org.json.JSONObject;

public class HuggingFaceClientWrapper {

    private static final Logger LOGGER = Logger.getLogger(HuggingFaceClientWrapper.class.getName());

    // Router endpoint for model
    private static final String API_URL =
            "https://router.huggingface.co/models/google/gemma-2b-it";

    // Your Hugging Face token
    private static final String API_TOKEN = "YOUR API KEY";

    /*
    Instructions for obtaining Hugging Face API token: Go to Website then Create Token with "Read" permissions for Inference API, and copy the token value.
https://huggingface.co/settings/tokens
But note that the free tier has limited usage (e.g., 30k tokens/month) and may not be sufficient for extensive test generation.
     */

    public static String generateCodeFromStory(String userStory) throws IOException, InterruptedException {

        // Combine system prompt + user story
        String prompt = """
                You are an expert Test Automation Engineer. Generate a clean, maintainable Selenium + TestNG test class in Java
                that includes full Extent Reports integration. 
                Requirements:
                - Return ONLY valid Java source code (no explanations, no markdown).
                - Include package, all necessary imports, and a single public class.
                - Use ChromeDriver in @BeforeClass and quit driver in @AfterClass.
                - On exceptions, capture a screenshot, save it under a reports/screenshots folder, and attach it to the report using MediaEntityBuilder.
                - Implement the test steps derived from the provided input.
                - Use clear, descriptive class and method names. Keep one @Test method per logical test-case.
                - Include appropriate TestNG assertions (Assert.assertTrue / Assert.assertFalse / Assert.assertEquals).
                - Do not include any external library setup instructions or extra commentary — only the Java test class source.
                """ + "\nUser Story:\n" + userStory;

        // Router expects {"inputs": "..."} only (parameters optional)
        String json = new JSONObject().put("inputs", prompt).toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Debug output
        System.out.println("Router Response: " + response.body());

        // Parse JSON returned by Router
        JSONObject obj = new JSONObject(response.body());
        if (obj.has("error")) {
            throw new IOException("Hugging Face API error: " + obj.getString("error"));
        }

        // Router returns "generated_text" inside "generated_text" field
        String code = obj.getString("generated_text");
        return code.trim();
    }

    public static String saveCodeToFile(String code, String folderPath) throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = folderPath + "/AI_GeneratedTest_" + ts + ".java";

        Path path = Path.of(fileName).toAbsolutePath();
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, code, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        LOGGER.info("Saved generated code to: " + path);
        return fileName;
    }
}
