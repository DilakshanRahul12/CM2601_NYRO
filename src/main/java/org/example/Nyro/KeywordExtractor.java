package org.example.Nyro;

import org.example.db.DatabaseHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordExtractor {

    public static void main(String[] args) {
        KeywordExtractor extractor = new KeywordExtractor();
        extractor.run();
    }

    public void run() {
        try {
            // Check and update articles with missing keywords
            checkAndUpdateKeywords();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void checkAndUpdateKeywords() {
//        DatabaseHandler dbHandler = new DatabaseHandler();
//        List<Article> articles = dbHandler.getAllArticles();
//
//        System.out.println("Checking articles for missing keywords...");
//        for (int i = 0; i < articles.size(); i++) {
//            Article article = articles.get(i);
//
//            // Assuming Article has a getKeywords() method that returns null if keywords are missing
//            if (article.getKeywords() == null || article.getKeywords().isEmpty()) {
//                System.out.println("No keywords found for article: " + article.getTitle());
//
//                // Extract keywords for the article
//                List<String> documents = fetchArticlesFromDatabase();
//                String input = preparePythonInput(documents, i); // Use current index as target
//                try {
//                    String rawOutput = callPythonScript("src/main/java/org/example/model/tfidf_processor.py", input);
//                    List<String> keywords = parsePythonOutput(rawOutput);
//
//                    // Update the database with the extracted keywords
//                    boolean updated = dbHandler.updateArticleKeywords(article.getId(), keywords);
//
//                    if (updated) {
//                        System.out.println("Updated keywords for article: " + article.getTitle());
//                    } else {
//                        System.out.println("Failed to update keywords for article: " + article.getTitle());
//                    }
//                } catch (IOException e) {
//                    System.out.println("Error processing article: " + article.getTitle());
//                    e.printStackTrace();
//                }
//            } else {
//                System.out.println("Keywords already exist for article: " + article.getTitle());
//            }
//        }
//    }

    public void checkAndUpdateKeywords() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        List<Article> articles = dbHandler.getAllArticles();

        System.out.println(articles);

        System.out.println("Checking articles for missing keywords...");
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println("Processing article index: " + i + ", ID: " + article.getId() + ", Title: " + article.getTitle());

            if (article.getKeywords() == null || article.getKeywords().isEmpty()) {
                System.out.println("No keywords found for article: " + article.getTitle() );

                String input = preparePythonInput(Collections.singletonList(article.getDescription()), 0); // Pass current article
                try {
                    String rawOutput = callPythonScript("src/main/java/org/example/model/tfidf_processor.py", input);
                    List<String> keywords = parsePythonOutput(rawOutput);

                    boolean updated = dbHandler.updateArticleKeywords(article.getId(), keywords);

                    if (updated) {
                        System.out.println("Updated keywords for article: " + article.getTitle() + "\n");
                    } else {
                        System.out.println("Failed to update keywords for article: " + article.getTitle());
                    }
                } catch (IOException e) {
                    System.out.println("Error processing article at index " + i + ", Title: " + article.getTitle());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Keywords already exist for article: " + article.getTitle());
            }
        }
    }



    private List<String> fetchArticlesFromDatabase() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        List<Article> articles = dbHandler.getAllArticles();
        System.out.println("Fetched " + articles.size() + " articles from the database.");
        return articles.stream()
                .map(Article::getDescription)
                .collect(Collectors.toList());
    }

    private String preparePythonInput(List<String> documents, int targetIndex) {
        StringBuilder input = new StringBuilder();
        for (String doc : documents) {
            input.append(doc).append(","); // Assuming comma separation for documents
        }
        input.append("\n").append(targetIndex); // Append the target document index
        System.out.println("Prepared input for Python script: " + input);
        return input.toString();
    }

    private String callPythonScript(String scriptPath, String input) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);
        Process process = pb.start();

        // Send input to Python script
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
            writer.write(input);
            writer.flush();
            System.out.println("Sent input to Python script.");
        }

        // Read output from Python script
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        System.out.println("Raw Python Output: " + output);
        return output.toString();
    }

    private List<String> parsePythonOutput(String rawOutput) {
        List<String> keywords = new ArrayList<>();
        String[] lines = rawOutput.split("\n");

        for (String line : lines) {
            if (line.trim().length() > 0) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String keyword = parts[0].trim();
                    keywords.add(keyword);
                }
            }
        }
        return keywords;
    }
}
