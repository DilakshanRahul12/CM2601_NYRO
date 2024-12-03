package org.example.Nyro;

import org.example.db.DatabaseHandler;

import java.time.LocalDateTime;
import java.util.*;

public class RecommendationEngine {
    private final DatabaseHandler databaseHandler;

    public RecommendationEngine(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    /**
     * Generates recommendations based on user preferences.
     *
     * @param userId The ID of the user for whom recommendations are to be generated.
     * @param limit  The maximum number of recommendations.
     * @return A list of recommended articles.
     */
    public List<Article> getRecommendations(int userId, int limit) {
        // Get user preferences and articles
        UserPreference userPreference = databaseHandler.getUserPreference(userId);
        List<Article> allArticles = databaseHandler.getCachedArticles();

        // Calculate category weights
        Map<String, Integer> categoryWeights = getCategoryWeights(userPreference);

        // Filter, sort, and limit articles manually
        List<Article> filteredArticles = new ArrayList<>();
        for (Article article : allArticles) {
            if (article.getCategory() != null) {
                filteredArticles.add(article);
            }
        }

        // Sort the articles based on category weights
        filteredArticles.sort((a1, a2) -> {
            int weight1 = categoryWeights.getOrDefault(a1.getCategory(), 0);
            int weight2 = categoryWeights.getOrDefault(a2.getCategory(), 0);
            return Integer.compare(weight2, weight1); // Descending order
        });

        // Limit the results
        List<Article> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, filteredArticles.size()); i++) {
            result.add(filteredArticles.get(i));
        }

        return result;
    }

    /**
     * Computes weights for categories based on user interaction.
     *
     * @param userPreference The user's preferences.
     * @return A map of category weights.
     */
    private Map<String, Integer> getCategoryWeights(UserPreference userPreference) {
        Map<String, Integer> weights = new HashMap<>();

        // Adjust weights based on user interaction
        adjustWeights(weights, userPreference.getFavourites(), 3);
        adjustWeights(weights, userPreference.getRead(), 2);
        adjustWeights(weights, userPreference.getDislike(), -1);

        return weights;
    }

    /**
     * Adjusts category weights based on a given interaction type.
     *
     * @param weights     The map of category weights.
     * @param interaction Map of article IDs to interaction counts.
     * @param weightValue The weight adjustment value.
     */
    private void adjustWeights(Map<String, Integer> weights, Map<Integer, LocalDateTime> interaction, int weightValue) {
        for (Integer articleId : interaction.keySet()) {
            Article article = findArticleById(articleId);
            if (article != null && article.getCategory() != null) {
                weights.put(article.getCategory(),
                        weights.getOrDefault(article.getCategory(), 0) + weightValue);
            }
        }
    }

    /**
     * Finds an article by its ID.
     *
     * @param articleId The ID of the article.
     * @return The article object, or null if not found.
     */
    private Article findArticleById(int articleId) {
        List<Article> cachedArticles = databaseHandler.getCachedArticles();
        for (Article article : cachedArticles) {
            if (article.getId() == articleId) {
                return article;
            }
        }
        return null;
    }
}
