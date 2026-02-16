import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerateTestFromStory {

    public static void main(String[] args) {

        String userStory = """
				        As a registered user,
				        I want to log in to the website with valid credentials
				        so that I can access my dashboard.

				        Acceptance Criteria:
				        - Navigate to https://opensource-demo.orangehrmlive.com/
				        - Verify the Page Title
				        - Enter valid username and password
				        - Verify successful login by checking dashboard visibility or Page Title
				""";

        try {
            System.out.println("Sending user story to AI...");
            String generatedCode = GeminiLLMWrapper.generateCodeFromStory(userStory);
            System.out.println("\n✅ AI Generated Test Code:\n");
            System.out.println(generatedCode);

            // Save to timestamped file inside 'generated-tests' folder
            String savedFile = HuggingFaceClientWrapper.saveCodeToFile(generatedCode, "generated-tests");

            System.out.println("\n✅ AI Generated Test Code saved to: " + savedFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
