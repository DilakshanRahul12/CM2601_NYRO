package org.example.Nyro;

import org.example.db.DatabaseHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;

public class ArticleCategorizer {

    private final DatabaseHandler dbHandler;

    public ArticleCategorizer(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    /**
     * Categorizes and updates articles without categories in the database.
     */
    public void categorizeAndUpdateArticles() {
        // Fetch all articles
        List<Article> articles = dbHandler.getAllArticles();

        for (Article article : articles) {
            if (article.getCategory() == null || article.getCategory().isBlank()) {
                // Categorize the article
                String category = categorizeArticle(article.getDescription(), article.getTitle());

                if (category != null) {
                    // Update the category in the Article object
                    article.setCategory(category);

                    // Update the category in the database
                    dbHandler.updateArticleCategory(article.getId(), category);
                    System.out.printf("Updated article ID %d with category '%s'%n", article.getId(), category);
                }
            }
        }
    }

    /**
     * Invokes the Python script to categorize the article based on description and title.
     *
     * @param description The description of the article.
     * @param title       The title of the article.
     * @return The identified category or null if the categorization failed.
     */
    private String categorizeArticle(String description, String title) {
        try {
            // Command to run the Python script
            String pythonCommand = "python3";
            String scriptPath = "src/main/java/org/example/model/Categorizer.py";

            // Construct the process builder
            ProcessBuilder pb = new ProcessBuilder(pythonCommand, scriptPath, description, title);
            pb.redirectErrorStream(true); // Merge error stream with output stream
            Process process = pb.start();

            // Read output from the script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Wait for the script to finish execution
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Return the category from the output
                return output.toString().trim();
            } else {
                System.err.printf("Python script exited with code %d. Output: %s%n", exitCode, output);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        ArticleCategorizer categorizer = new ArticleCategorizer(dbHandler);

        // Start the categorization process
        categorizer.categorizeAndUpdateArticles();
    }
}
