import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiLLMWrapper {
    public static String generateCodeFromStory(String userStory) throws Exception {

        String apiKey = "YOUR OWN API KEY ";
        //https://aistudio.google.com/api-keys
        String model = "gemini-2.5-flash";


        // Combine system prompt + user story
        String prompt = """
                You are an expert Test Automation Engineer. Generate a clean, maintainable Selenium + TestNG test class in Java
                that includes WebDriver Manager for Chrome Browser Execution.
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

        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONObject partWrapper = new JSONObject();
        partWrapper.put("parts", new JSONArray().put(textPart));

        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", new JSONArray().put(partWrapper));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://generativelanguage.googleapis.com/v1/models/"
                                + model
                                + ":generateContent?key="
                                + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();


        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Res:\n" + response.body());

        JSONObject jsonResponse = new JSONObject(response.body());

        return jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");
    }
}
