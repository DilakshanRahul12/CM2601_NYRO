package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

import java.io.IOException;
import java.util.List;

public class FeedController {

    @FXML
    private HBox hNewsHolder;

    @FXML
    private ScrollPane recentNews;

    @FXML
    private TilePane rNewsTilePane;  // TilePane for recent news

    @FXML
    private Button login;

    @FXML
    private Button signUp;

    public void initialize() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        NewsApiFetcher newsApiFetcher = new NewsApiFetcher();

        // Fetch and store new articles from the News API
        //newsApiFetcher.fetchAndStoreNews();

        // Fetch 4 random articles for hot news
        List<Article> hotArticleList = dbHandler.getRandomArticles(4);

        // Fetch 12 random articles for recent news
        List<Article> recentArticleList = dbHandler.getRandomArticles(12);

        // Populate the views
        populateHotNews(hotArticleList);
        populateRecentNews(recentArticleList);
    }

    @FXML
    private void goToLogin() {
        // Get the current stage (window) and use popScene to switch to Login.fxml
        Stage stage = (Stage) login.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/Login.fxml");
    }

    @FXML
    private void goToSignup() {
        // Get the current stage (window) and use popScene to switch to SignUp.fxml
        Stage stage = (Stage) signUp.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/SignUp.fxml");
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
