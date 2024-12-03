package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class NewsCardController {

    @FXML
    private ImageView newsImg;

    @FXML
    private Label newshead;

    @FXML
    private Label newsDate;

    @FXML
    private Label newsCategory;

    @FXML
    private Label newsDesc;

    @FXML
    private Hyperlink newsLink;

    @FXML
    private Button read;

    @FXML
    private ImageView fav;

    @FXML
    private ImageView dislike;
    private Article currentArticle; // Store the article data for later use

    public void setNewsData(Article article) {
        // Save the article data
        this.currentArticle = article;

        // Populate the UI elements with article details
        newshead.setText(article.getTitle());
        System.out.println("DEBUG: Article ID set to " + currentArticle.getId());
        newsDate.setText(String.valueOf(article.getPublishedAt()));
        newsCategory.setText(article.getCategory());
        newsDesc.setText(article.getDescription());
        newsLink.setText(article.getSource());

        newsLink.setOnAction(e -> redirectToWebView(article.getUrl()));


        // Load the provided image or a default one
        try {
            Image image = new Image(article.getImgURL(), true);
            newsImg.setImage(image);
        } catch (Exception e) {
            newsImg.setImage(new Image(getClass().getResource("/images/heart.png").toExternalForm()));
        }
    }

    // Accessor method to retrieve the current article
    public Article getCurrentArticle() {
        return this.currentArticle;
    }

    private void redirectToWebView(String url) {
        try {
            System.out.println(getClass().getResource("NewsArticle.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsArticle.fxml"));
            Parent root = loader.load();

            NewsArticleController controller = loader.getController();
            controller.loadURL(url);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nyro - WebView");
            stage.show();
        } catch (IOException e) {
            // Show an alert dialog to inform the user of the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load the webpage");
            alert.setContentText("Please check your internet connection or try again later.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReadMore() {
        if (currentArticle != null) {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user != null) {
                int userId = user.getId();
                int articleId = currentArticle.getId();

                // Update the database
                boolean success = new DatabaseHandler().addToReadHistory(userId, articleId);
                if (success) {
                    // Update the user's preferences
                    LocalDateTime timestamp = LocalDateTime.now();
                    UserPreference userPreference = new UserPreference(user);
                    userPreference.addToRead(articleId, timestamp);
                    System.out.println("Article marked as read: " + currentArticle.getTitle());
                }
            }
            redirectToWebView(currentArticle.getUrl());
        }
    }

    @FXML
    private void handleFavorite() {
        if (currentArticle != null) {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user != null) {
                int userId = user.getId();
                int articleId = currentArticle.getId();

                boolean success = new DatabaseHandler().addToFavorites(userId, articleId);
                if (success) {
                    LocalDateTime timestamp = LocalDateTime.now();
                    UserPreference userPreference = new UserPreference(user);
                    userPreference.addToFavourites(articleId, timestamp);
                    System.out.println("Article added to favorites: " + currentArticle.getTitle());
                }
            }
        }
    }

    @FXML
    private void handleDislike() {
        if (currentArticle != null) {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user != null) {
                int userId = user.getId();
                int articleId = currentArticle.getId();

                boolean success = new DatabaseHandler().addToDislikes(userId, articleId);
                if (success) {
                    LocalDateTime timestamp = LocalDateTime.now();
                    UserPreference userPreference = new UserPreference(user);
                    userPreference.addToDislike(articleId, timestamp);
                    System.out.println("Article added to dislikes: " + currentArticle.getTitle());
                }
            }
        }
    }


}
