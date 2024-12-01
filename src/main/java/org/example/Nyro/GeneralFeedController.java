package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.example.db.DatabaseHandler;

import java.io.IOException;
import java.util.List;

public class GeneralFeedController {

    @FXML
    private HBox hNewsHolder;

    @FXML
    private ScrollPane recentNews;

    @FXML
    private TilePane rNewsTilePane;  // TilePane for recent news

    public void initialize() {
        DatabaseHandler dbHandler = new DatabaseHandler();

        // Fetch 4 random articles for hot news
        List<Article> hotArticleList = dbHandler.getRandomArticles(4);

        // Fetch 10 random articles for recent news
        List<Article> recentArticleList = dbHandler.getRandomArticles(12);

        // Populate the views
        populateHotNews(hotArticleList);
        populateRecentNews(recentArticleList);
    }

    private void populateHotNews(List<Article> hotArticleList) {
        for (Article article : hotArticleList) {
            try {
                // Load the Article Card FXML for hot article
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsCard2.fxml"));
                Pane newsCard = loader.load();

                // Access the NewsCardController
                NewsCardController controller = loader.getController();

                // Set the data for the article card
                controller.setNewsData(article);

                // Add the article card to the HBox
                hNewsHolder.getChildren().add(newsCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateRecentNews(List<Article> recentArticleList) {
        for (Article article : recentArticleList) {
            try {
                // Load the Article Card FXML for recent article
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsCard2.fxml"));
                Pane newsCard = loader.load();

                // Access the NewsCardController
                NewsCardController controller = loader.getController();

                // Set the data for the article card
                controller.setNewsData(article);

                // Add the article card to the TilePane
                rNewsTilePane.getChildren().add(newsCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
