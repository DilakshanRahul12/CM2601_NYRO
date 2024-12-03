package org.example.Nyro;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class PersonalizedFeedController {

    @FXML
    private HBox cardHolder; // HBox for Hot Articles
    @FXML
    private TilePane rNewsTilePane;

    // The RecommendationEngine instance
    private RecommendationEngine recommendationEngine;

    @FXML
    private Button login;
    @FXML
    private Button signUp;
    @FXML
    private Button signOut;

    private User loggedInUser;
    private UserPreference userPreference;

    public void initialize() {
        // Initialize the DatabaseHandler and RecommendationEngine
        DatabaseHandler dbHandler = new DatabaseHandler();
        this.recommendationEngine = new RecommendationEngine(dbHandler);

        // Example user ID (replace with actual user ID in your application)
        int currentUserId = getCurrentUserId();

        // Fetch hot articles and personalized articles
        List<Article> hotArticleList = dbHandler.getRandomArticles(5); // Hot articles

        // Populate the views
        populateHotArticles(hotArticleList);
        populateForYouArticles(currentUserId);
    }

    @FXML
    private void goToProfile() {
        // Switch to Login screen
        Stage stage = (Stage) login.getScene().getWindow();
        SceneSwitcher.popScene(stage, "org/example/Nyro/Login.fxml");
    }

    @FXML
    private void goToHistory() {
        // Switch to Sign Up screen
        Stage stage = (Stage) signUp.getScene().getWindow();
        SceneSwitcher.popScene(stage, "org/example/Nyro/SignUp.fxml");
    }

    private void populateHotArticles(List<Article> hotArticleList) {
        for (Article article : hotArticleList) {
            try {
                // Load the Article Card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsCard2.fxml"));
                Pane newsCard = loader.load();

                // Access the NewsCardController
                NewsCardController controller = loader.getController();

                // Set the article data
                controller.setNewsData(article);

                // Add the article card to the HBox
                cardHolder.getChildren().add(newsCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Populates the "For You" section with recommended articles.
     *
     * @param userId The ID of the user for whom the recommendations are being generated.
     */
    public void populateForYouArticles(int userId) {
        // Generate recommendations using the RecommendationEngine
        List<Article> recommendedArticles = recommendationEngine.getRecommendations(userId, 12);

        // Iterate through the recommended articles and add them to the HBox
        for (Article article : recommendedArticles) {
            try {
                // Load the Article Card FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsCard2.fxml"));
                Pane newsCard = loader.load();

                // Access the NewsCardController
                NewsCardController controller = loader.getController();

                // Set the article data
                controller.setNewsData(article);

                // Add the article card to the HBox
                rNewsTilePane.getChildren().add(newsCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogout() {
        // Clear the logged-in user's session
        SessionManager.getInstance().clearSession();

        // Redirect to the Login screen
        Stage stage = (Stage) signOut.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/app/GeneralizedFeed.fxml");
    }

    /**
     * Fetches the current user's ID using the SessionManager.
     *
     * @return The current user's ID, or -1 if no user is logged in.
     */
    private int getCurrentUserId() {
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            return loggedInUser.getId();
        }
        System.out.println("DEBUG: No user is currently logged in.");
        return -1; // Return -1 or handle accordingly when no user is logged in
    }


    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        this.userPreference = new UserPreference(user);

        // Load user's preferences from the database if needed
        //loadUserPreferences();
    }


}
