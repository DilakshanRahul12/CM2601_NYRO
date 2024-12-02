package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;

import org.example.utility.SceneSwitcher;

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

    private DatabaseHandler dbHandler;


    @FXML
    public void initialize() {
        dbHandler = new DatabaseHandler();
        signIn.setOnAction(event -> handleSignIn());
    }

    private void handleSignIn() {
        String email = signInEmail.getText();
        String password = signInPass.getText();

        if (!dbHandler.isValidEmail(email)) {
            showAlert("Sign-In Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        if (dbHandler.authenticateUser(email, password)) {
            showAlert("Sign-In Successful", "Welcome back!", Alert.AlertType.INFORMATION);
            goToPersonalizedFeed();
        } else {
            showAlert("Sign-In Failed", "Email or password is incorrect.", Alert.AlertType.ERROR);
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
        // Get the current stage (window) and use popScene to switch to GeneralizedFeed.fxml
        Stage stage = (Stage) feed.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/PersonalizedFeed.fxml");
    }

}
