package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.db.DatabaseHandler;

import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField signInEmail;
    @FXML
    private PasswordField signInPass;
    @FXML
    private PasswordField signInPass1;
    @FXML
    private Button signIn;

    private DatabaseHandler dbHandler;

    @FXML
    private void initialize() {
        dbHandler = new DatabaseHandler();
        signIn.setOnAction(event -> handleSignUp());
    }

    private void handleSignUp() {
        String email = signInEmail.getText();
        String password = signInPass.getText();
        String confirmPassword = signInPass1.getText();

        User user = new User(email, password);

        if (!user.isValidEmail()) {
            showAlert("Sign-Up Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        if (!user.checkPasswordsMatch(confirmPassword)) {
            showAlert("Sign-Up Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        if (dbHandler.insertUser(user.getEmail(), user.getPassword())) {
            showAlert("Sign-Up Successful", "Account created successfully!", Alert.AlertType.INFORMATION);
            clearFields();
        } else {
            showAlert("Sign-Up Failed", "Unable to save user. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        signInEmail.clear();
        signInPass.clear();
        signInPass1.clear();
    }

    @FXML
    private void login() {
        switchScene("LoginPage.fxml");
    }

    public void switchScene(String fxmlFilePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();
            Stage stage = (Stage) signIn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
