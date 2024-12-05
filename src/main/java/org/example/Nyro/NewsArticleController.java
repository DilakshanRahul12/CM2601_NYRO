package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.example.db.DatabaseHandler;

public class NewsArticleController {
    @FXML
    private WebView webpage;
    private WebEngine webEngine;

    @FXML
    private Button favButton;

    @FXML
    private Button dislikeButton;

    private int currentUserId ; // Should be set when the user logs in

    private DatabaseHandler dbHandler; // Instance of your database handler

    @FXML
    public void initialize() {
        dbHandler = new DatabaseHandler();
        webEngine = webpage.getEngine();


    }
    public void loadURL(String url) {
        webEngine.load(url);
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId; // Set this from your login/session logic
    }

    private int getArticleIdFromURL(String url) {
        int articleId = dbHandler.getArticleIdByUrl(url);
        if (articleId == -1) {
            System.out.println("Article ID could not be found for the URL: " + url);
        }
        return articleId;
    }

    @FXML
    public void handleFavoriteClick() {
        String currentUrl = webEngine.getLocation();
        int articleId = getArticleIdFromURL(currentUrl);

        if (articleId != -1) {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user != null) {
                boolean success = dbHandler.addToFavorites(user.getId(), articleId);
                if (success) {
                    System.out.println("DEBUG: Article added to favorites successfully for User ID: " + user.getId());
                } else {
                    System.out.println("DEBUG: Failed to add article to favorites for User ID: " + user.getId());
                }
            } else {
                System.out.println("DEBUG: No logged-in user. Cannot add article to favorites.");
            }
        } else {
            System.out.println("DEBUG: Invalid article ID. Cannot add to favorites.");
        }
    }

    @FXML
    public void handleDislikeClick() {
        String currentUrl = webEngine.getLocation();
        int articleId = getArticleIdFromURL(currentUrl);

        if (articleId != -1) {
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user != null) {
                boolean success = dbHandler.addToDislikes(user.getId(), articleId);
                if (success) {
                    System.out.println("DEBUG: Article added to dislikes successfully for User ID: " + user.getId());
                } else {
                    System.out.println("DEBUG: Failed to add article to dislikes for User ID: " + user.getId());
                }
            } else {
                System.out.println("DEBUG: No logged-in user. Cannot add article to dislikes.");
            }
        } else {
            System.out.println("DEBUG: Invalid article ID. Cannot add to dislikes.");
        }
    }
}

