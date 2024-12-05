package org.example.Nyro;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.db.DatabaseHandler;

public class NewsApiFetcher {

    private static final String API_KEY = "0bc1c767d0074fd882b504932335c250"; // Replace with your Article API key
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines";
    private static final String COUNTRY = "us";
    private static final String[] CATEGORIES = {"business", "entertainment", "health", "science", "sports", "technology"};
    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void fetchAndStoreNews() {
        ExecutorService executorService = Executors.newFixedThreadPool(CATEGORIES.length);
        CompletableFuture<?>[] tasks = new CompletableFuture[CATEGORIES.length];

        for (int i = 0; i < CATEGORIES.length; i++) {
            String category = CATEGORIES[i];
            tasks[i] = CompletableFuture.runAsync(() -> fetchAndProcessCategory(category), executorService);
        }

        CompletableFuture.allOf(tasks).join();
        executorService.shutdown();
    }

    private void fetchAndProcessCategory(String category) {
        String apiUrl = BASE_URL + "?country=" + COUNTRY + "&category=" + category + "&pageSize=5&apiKey=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        System.out.println("Fetching news for category: " + category);
        System.out.println("---------------------------------------------------");

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                JsonNode articles = rootNode.path("articles");
                if (articles.isArray()) {
                    for (JsonNode article : articles) {
                        String title = article.path("title").asText(null);

                        // Skip articles titled "Removed"
                        if ("[Removed]".equalsIgnoreCase(title)) {
                            System.out.println("Skipping article titled Removed");
                            continue;
                        }

                        // Check if the article title already exists in the database
                        if (dbHandler.isTitleExists(title)) {
                            System.out.println("Skipping duplicate article: " + title);
                            continue;
                        }

                        String description = article.path("description").asText(null);
                        String content = article.path("content").asText(null);
                        String source = article.path("source").path("name").asText(null);
                        String publishedAt = article.path("publishedAt").asText(null);
                        String url = article.path("url").asText(null);
                        String imgUrl = article.path("urlToImage").asText(null);

                        // Convert publishedAt to Timestamp
                        Timestamp publishedTimestamp = null;
                        if (publishedAt != null) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                                java.util.Date parsedDate = dateFormat.parse(publishedAt);
                                publishedTimestamp = new Timestamp(parsedDate.getTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue; // Skip this article if date parsing fails
                            }
                        }

                        if (title != null && description != null && content != null && source != null && publishedTimestamp != null && url != null) {
                            // Insert the article into the database using DatabaseHandler
                            if (dbHandler.insertArticle(title, description, content, source, publishedTimestamp, url, imgUrl)) {
                                System.out.println("Inserted article: " + title);
                            } else {
                                System.out.println("Failed to insert article: " + title);
                            }
                        }
                    }
                }
            } else {
                System.out.println("Failed to fetch news articles for category: " + category);
                System.out.println("HTTP Response Code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
