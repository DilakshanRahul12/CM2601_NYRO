package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class LoginPageController {
    @FXML
    public Button signUpSwitch;
    @FXML
    private PasswordField signInPass;
    @FXML
    private TextField signInEmail;
    @FXML
    private Button signIn;

    // Database credentials
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Users";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sdr8393";

    @FXML
    public void initialize() {
        signIn.setOnAction(event -> handleSignIn());
    }

    private void handleSignIn() {
        String email = signInEmail.getText();
        String password = signInPass.getText();

        // Create User object
        User user = new User(email, password);

        if (!user.isValidEmail()) {
            showAlert("Sign-In Error", "Invalid email format.", Alert.AlertType.ERROR);
            return;
        }

        if (authenticateUser(user)) {
            showAlert("Sign-In Successful", "Welcome back!", Alert.AlertType.INFORMATION);
            // Switch to the next scene if needed, e.g., switchScene("HomePage.fxml");
        }
    }

    private boolean authenticateUser(User user) {
        String authQuery = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement authStmt = conn.prepareStatement(authQuery)) {

            // Step 1: Check email and password combination
            authStmt.setString(1, user.getEmail());
            authStmt.setString(2, user.getPassword());
            ResultSet authResult = authStmt.executeQuery();

            if (authResult.next()) {
                // Email and password match
                return true;
            } else {
                // Either email or password is incorrect
                showAlert("Sign-In Failed", "Email or password is incorrect.", Alert.AlertType.ERROR);
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void register() {
        switchScene("SignUp.fxml");
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
