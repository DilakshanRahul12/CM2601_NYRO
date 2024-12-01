package org.example.Nyro;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.db.DatabaseHandler;

public class NewsApiFetcher {

    private static final String API_KEY = "0bc1c767d0074fd882b504932335c250"; // Replace with your Article API key
    private static final String BASE_URL = "https://newsapi.org/v2/top-headlines";
    private static final String COUNTRY = "us";
    private static final String[] CATEGORIES = {"business", "entertainment", "health", "science", "sports", "technology"};
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    public void fetchAndStoreNews() {
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String category : CATEGORIES) {
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

    public static void main(String[] args) {
        NewsApiFetcher fetcher = new NewsApiFetcher();
        fetcher.fetchAndStoreNews();
    }
}
