package org.example.Nyro;

import org.example.db.DatabaseHandler;

import java.io.*;

public class ArticleCategorizer {

    private static final String PYTHON_SCRIPT_PATH = "src/main/java/org/example/model/categorizer.py";

    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        int updatedArticles = dbHandler.categorizeCachedArticles();

        System.out.println("Total articles categorized: " + updatedArticles);
    }

    /**
     * Categorizes an article based on its description and caption using a Python script.
     *
     * @param description The description of the article.
     * @param content     The caption of the article.
     * @return The predicted category.
     */
    public String categorizeArticle(String description, String content) {
        try {
            // Call the Python script and return the output
            return callPythonScript(description, content);
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown"; // Return "Unknown" in case of an error
        }
    }

    /**
     * Calls the Python categorizer script with the given description and caption.
     *
     * @param description The description of the article.
     * @param caption     The caption of the article.
     * @return The category predicted by the Python script.
     * @throws IOException if an error occurs during script execution.
     */
    private String callPythonScript(String description, String caption) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "python3", PYTHON_SCRIPT_PATH, description, caption
        );

        // Debug: Print the command being executed
        System.out.println("Executing Python script:");
        System.out.println("Command: python3 " + PYTHON_SCRIPT_PATH + " \"" + description + "\" \"" + caption + "\"");

        Process process = pb.start();

        // Capture the script's output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;

            // Debug: Indicate start of reading output
            System.out.println("Reading script output...");
            while ((line = reader.readLine()) != null) {
                output.append(line);
                // Debug: Print each line of output
                //System.out.println("Python Output Line: " + line);
            }

            process.waitFor(); // Ensure the script has completed

            // Debug: Print the final output
            System.out.println("Final Python Output: " + output.toString().trim());

            return output.toString().trim(); // Return the script's output
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Python script interrupted.", e);
        } catch (Exception e) {
            // Debug: Catch any other unexpected issues
            System.err.println("Unexpected error during script execution: " + e.getMessage());
            throw e;
        }
    }
}
