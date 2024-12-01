package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

public class SignUpController {

    @FXML
    private TextField signUpEmail;
    @FXML
    private PasswordField signUpPass;
    @FXML
    private PasswordField signUpPass1;
    @FXML
    private Button signUp;

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
        signUpEmail.clear();
        signUpPass.clear();
        signUpPass1.clear();
    }
    @FXML
    public void login() {
        SceneSwitcher.switchScene((Stage) signUp.getScene().getWindow(), "/org/example/Nyro/Login.fxml");
    }
}
