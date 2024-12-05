package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

import java.io.IOException;
import java.net.URL;

public class SignUpController {

    @FXML
    private TextField signUpEmail;
    @FXML
    private PasswordField signUpPass;
    @FXML
    private PasswordField signUpPass1;
    @FXML
    private Button signUp;
    @FXML
    private Button feed;

    private DatabaseHandler dbHandler;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();
        signUp.setOnAction(event -> handleSignUp());
    }

    private void handleSignUp() {
        String email = signUpEmail.getText();
        String password = signUpPass.getText();
        String confirmPassword = signUpPass1.getText();

        if (!dbHandler.isValidEmail(email)) {
            showAlert("Sign-Up Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        if (!dbHandler.doPasswordsMatch(password, confirmPassword)) {
            showAlert("Sign-Up Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        if (dbHandler.insertUser(email, password)) {
            // Fetch the newly created user (e.g., by email)
            User newUser = dbHandler.getUserByEmail(email);
            if (newUser != null) {
                // Set the session for the new user
                SessionManager.getInstance().setLoggedInUser(newUser);

                // Redirect to the Personalized Feed
                Stage currentStage = (Stage) signUpEmail.getScene().getWindow();

                NewsApiFetcher newsApiFetcher = new NewsApiFetcher();
                newsApiFetcher.fetchAndStoreNews();
                int catCount = dbHandler.categorizeCachedArticles();
                System.out.println(catCount);

                redirectToPersonalizedFeed(newUser, currentStage);
            } else {
                showAlert("Sign-Up Error", "Unable to fetch user data. Please try logging in.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Sign-Up Failed", "Unable to save user. Please try again.", Alert.AlertType.ERROR);
            clearFields();
        }
    }



    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        signUpEmail.clear();
        signUpPass.clear();
        signUpPass1.clear();
    }
    @FXML
    public void login() {
        SceneSwitcher.switchScene((Stage) signUp.getScene().getWindow(), "/org/example/Nyro/Login.fxml");
    }

    @FXML
    private void goToFeed() {
        // Get the current stage (window) and use popScene to switch to GeneralizedFeed.fxml
        Stage stage = (Stage) feed.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/app/GeneralizedFeed.fxml");
    }

    @FXML
    private void goToPersonalizedFeed() {
        // Get the current stage (window) and use popScene to switch to GeneralizedFeed.fxml
        Stage stage = (Stage) feed.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/PersonalizedFeed.fxml");
    }

    private void redirectToPersonalizedFeed(User user, Stage stage) {
        try {
            URL resource = getClass().getResource("/org/example/Nyro/PersonalizedFeed.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Pass the logged-in user to the controller
            PersonalizedFeedController controller = loader.getController();
            controller.setLoggedInUser(user);

            // Update the stage with the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Personalized Feed.", Alert.AlertType.ERROR);
        }
    }

}
