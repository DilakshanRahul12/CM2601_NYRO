package org.example.service;

import org.example.db.DatabaseHandler;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.util.Map;

public class ArticleCategorizer {

    // Method to call the Flask API
    private String callPythonScript(String description, String caption) {
        try {
            // Convert input to JSON format
            Gson gson = new Gson();
            String jsonInput = gson.toJson(Map.of("description", description, "caption", caption));

            // Create HTTP client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/categorize"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            // Send the request and handle the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, String> result = gson.fromJson(response.body(), Map.class);

            // Return the category
            return result.get("category");
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

//    public static void main(String[] args) {      // Test
//        DatabaseHandler dbHandler = new DatabaseHandler();
//        int updatedArticles = dbHandler.categorizeCachedArticles();
//
//        System.out.println("Total articles categorized: " + updatedArticles);
//    }

    /**
     * Categorizes an article based on its description and caption using a Python script.
     *
     * @param description The description of the article.
     * @param content     The caption of the article.
     * @return The predicted category.
     */
    // Public method to categorize an article
    public String categorizeArticle(String description, String content) {
        try {
            // Call the Python script and return the output
            return callPythonScript(description, content);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown"; // Return "Unknown" in case of an error
        }
    }

}
