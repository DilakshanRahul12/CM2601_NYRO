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
import java.util.List;

public class PersonalizedFeedController {

    @FXML
    private HBox cardHolder; // HBox for Hot Articles

    @FXML
    private GridPane gridPane; // GridPane for "For You" articles

    @FXML
    private Button login;

    @FXML
    private Button signUp;

    public void initialize() {
        DatabaseHandler dbHandler = new DatabaseHandler();

        // Fetch articles based on user preferences
        List<Article> hotArticleList = dbHandler.getRandomArticles(5); // Hot articles
        //List<Article> personalizedArticles = dbHandler.getPersonalizedArticles(9); // For You articles

        // Populate the views
        populateHotArticles(hotArticleList);
        //populateForYouArticles(personalizedArticles);
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

    private void populateForYouArticles(List<Article> personalizedArticles) {
        int column = 0;
        int row = 0;

        for (Article article : personalizedArticles) {
            try {
                // Load the Article Card FXML;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsCard2.fxml"));
                Pane newsCard = loader.load();

                // Access the NewsCardController
                NewsCardController controller = loader.getController();

                // Set the article data
                controller.setNewsData(article);

                // Add the article card to the GridPane
                gridPane.add(newsCard, column, row);

                column++;
                if (column == 3) { // Adjust number of columns as per the layout
                    column = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
