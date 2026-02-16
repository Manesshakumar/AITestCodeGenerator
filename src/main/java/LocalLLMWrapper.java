import java.io.*;

public class LocalLLMWrapper {

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

                User Story:
                """ + userStory;

        // Command to run GPT4All CLI
        //ProcessBuilder pb = new ProcessBuilder("gpt4all", "-m", "gpt4all-j", "-p", prompt);
        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Users\\SABHARISH\\gpt4all\\bin\\gpt4all.exe",
                "-m", "gpt4all-j",
                "-p", prompt
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();

        return output.toString().trim();
    }
}
