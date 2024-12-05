package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;

import org.example.model.Admin;
import org.example.utility.SceneSwitcher;

import java.io.IOException;
import java.net.URL;

public class LoginPageController {

    public Button signUpSwicth;
    @FXML
    private TextField signInEmail;
    @FXML
    private PasswordField signInPass;
    @FXML
    private Button signIn;
    @FXML
    private Button feed;

    @FXML
    private Button admin;

    private DatabaseHandler dbHandler;


    @FXML
    public void initialize() {
        dbHandler = new DatabaseHandler();
        signIn.setOnAction(event -> handleSignIn());
    }

    private void handleSignIn() {
        String email = signInEmail.getText();
        String password = signInPass.getText();

        try {
            User authenticatedUser = User.authenticate(email, password, dbHandler);

            // Set session and redirect
            SessionManager.getInstance().setLoggedInUser(authenticatedUser);
            Stage currentStage = (Stage) signInEmail.getScene().getWindow();
            redirectToPersonalizedFeed(authenticatedUser, currentStage);
        } catch (IllegalArgumentException e) {
            showAlert("Sign-In Error", e.getMessage(), Alert.AlertType.ERROR);
        } catch (NullPointerException e) {
            showAlert("Sign-In Failed", "Email or password is incorrect.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void handleAdminLogin() {
        String email = signInEmail.getText();
        String password = signInPass.getText();

        // Validate admin credentials
        Admin authenticatedAdmin = dbHandler.authenticateAdmin(email, password);

        Stage currentStage = (Stage) admin.getScene().getWindow();
        if (authenticatedAdmin != null) { // Redirect to Admin Panel
            redirectToAdmin(authenticatedAdmin,currentStage);
        } else {
            showAlert("Admin Login Failed", "Email or password is incorrect.", Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void register() {
        SceneSwitcher.switchScene((Stage) signUpSwicth.getScene().getWindow(), "/org/example/Nyro/SignUp.fxml");
    }

    @FXML
    private void goToFeed() {
        // Get the current stage (window) and use popScene to switch to GeneralizedFeed.fxml
        Stage stage = (Stage) feed.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/app/GeneralizedFeed.fxml");
    }
    @FXML
    private void goToPersonalizedFeed() {
        Stage stage = (Stage) feed.getScene().getWindow();

        try {
            // Use the class's resource loader to locate the FXML file
            URL resource = getClass().getResource("/org/example/Nyro/PersonalizedFeed.fxml");
            if (resource == null) {
                throw new IOException("FXML file not found at specified path.");
            }

            // Create the FXMLLoader with the located resource
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Get the controller instance and pass the logged-in user
            PersonalizedFeedController controller = loader.getController();
            controller.setLoggedInUser(SessionManager.getInstance().getLoggedInUser());

            // Set the new scene to the stage and show it
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading PersonalizedFeed.fxml");
        }
    }

    private void redirectToPersonalizedFeed(User user, Stage stage) {
        try {
            URL resource = getClass().getResource("/org/example/Nyro/PersonalizedFeed.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            PersonalizedFeedController controller = loader.getController();
            controller.setLoggedInUser(user);

            // Switch to the Personalized Feed scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Personalized Feed.", Alert.AlertType.ERROR);
        }
    }

     private void redirectToAdmin(Admin admin,Stage stage) {
        try {
            URL resource = getClass().getResource("/org/example/Nyro/AdminManageUser.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AdminManageUsersController controller = loader.getController();
            controller.setAdmin(admin);

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Admin Panel.", Alert.AlertType.ERROR);
        }
    }


}
