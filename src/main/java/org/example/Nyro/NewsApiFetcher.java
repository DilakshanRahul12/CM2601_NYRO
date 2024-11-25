package org.example.Nyro;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NewsApiFetcher {
    public static void main(String[] args) {
            String apiKey = "0bc1c767d0074fd882b504932335c250"; // Replace with your News API key
            String apiUrl = "https://newsapi.org/v2/top-headlines?country=us&apiKey=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    ObjectMapper objectMapper = new ObjectMapper();
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

                            if (title != null && description != null && content != null && source != null && publishedAt != null && url != null) {
                                System.out.println("Title: " + title);
                                System.out.println("Description: " + description);
                                System.out.println("Content: " + content);
                                System.out.println("Source: " + source);
                                System.out.println("Published At: " + publishedAt);
                                System.out.println("URL: " + url);
                                System.out.println("imgURL: " + imgUrl);
                                System.out.println("---------------------------------------------------");
                            }
                        }
                    }
                } else {
                    System.out.println("Failed to fetch news articles. HTTP Response Code: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
