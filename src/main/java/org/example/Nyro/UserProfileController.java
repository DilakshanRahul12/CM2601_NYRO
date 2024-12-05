package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UserProfileController {

    @FXML
    private Label userEmail;

    @FXML
    private ListView<String> favoritesList;

    @FXML
    private ListView<String> dislikedList;

    @FXML
    private TextField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Label passwordChangeStatus;

    @FXML
    private Button logoutButton;
    @FXML
    private Button back;

    private final SessionManager sessionManager = SessionManager.getInstance();
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    public void initialize() {
        User loggedInUser = sessionManager.getLoggedInUser();

        if (loggedInUser != null) {
            userEmail.setText("Email: " + loggedInUser.getEmail());
            //userEmail.setText("Email: " + "loggedInUser.getEmail()");
            // Load user preferences
            loadFavorites(loggedInUser.getId());
            loadDislikedArticles(loggedInUser.getId());
        }

        changePasswordButton.setOnAction(event -> handleChangePassword());
        logoutButton.setOnAction(event -> handleLogout());
    }

    private void loadFavorites(int userId) {
        // Fetch favorite articles from the database
        List<String> favorites = dbHandler.getFavoriteArticlesByUserId(userId);
        favoritesList.getItems().clear();
        favoritesList.getItems().addAll(favorites);
    }

    private void loadDislikedArticles(int userId) {
        // Fetch disliked articles from the database
        List<String> disliked = dbHandler.getDislikedArticlesByUserId(userId);
        dislikedList.getItems().clear();
        dislikedList.getItems().addAll(disliked);
    }

    private void handleChangePassword() {
        String currentPass = currentPassword.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();

        User loggedInUser = sessionManager.getLoggedInUser();
        if (loggedInUser == null) {
            passwordChangeStatus.setText("Error: No user logged in.");
            return;
        }

        // Validate current password
        if (!dbHandler.validatePassword(loggedInUser.getId(), currentPass)) {
            passwordChangeStatus.setText("Error: Current password is incorrect.");
            return;
        }

        // Validate new password and confirmation
        if (!newPass.equals(confirmPass)) {
            passwordChangeStatus.setText("Error: New passwords do not match.");
            return;
        }

        // Update password in the database
        boolean success = dbHandler.updateUserPassword(loggedInUser.getId(), newPass);
        if (success) {
            passwordChangeStatus.setStyle("-fx-text-fill: green;");
            passwordChangeStatus.setText("Password changed successfully!");
        } else {
            passwordChangeStatus.setText("Error: Failed to change password.");
        }
    }

    private void handleLogout() {
        sessionManager.clearSession();
        System.out.println("User logged out successfully.");
        SessionManager.getInstance().clearSession();

        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneSwitcher.switchScene(stage, "/org/example/app/GeneralizedFeed.fxml");
    }
}
