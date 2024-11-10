package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class LoginPageController {
    @FXML
    public Button signUpSwicth;
    @FXML
    private PasswordField signInPass;
    @FXML
    private TextField signInEmail;
    @FXML
    private Button signIn;



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

        if (authenticateUser(email, password)) {
            showAlert("Sign-In Successful", "Welcome back!", Alert.AlertType.INFORMATION);
        }
    }

    private boolean authenticateUser(String email, String password) {
        // Step 1: Check if the email exists
        String emailQuery = "SELECT * FROM \"user\" WHERE email = ?";
        String authQuery = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement emailStmt = conn.prepareStatement(emailQuery);
             PreparedStatement authStmt = conn.prepareStatement(authQuery)) {

            // Check if email exists
            emailStmt.setString(1, email);
            ResultSet emailResult = emailStmt.executeQuery();

            if (!emailResult.next()) {
                // Email does not exist
                showAlert("Sign-In Failed", "Email not found.", Alert.AlertType.ERROR);
                return false;
            }

            // Step 2: Check email and password combination
            authStmt.setString(1, email);
            authStmt.setString(2, password);
            ResultSet authResult = authStmt.executeQuery();

            if (authResult.next()) {
                // Email and password match
                return true;
            } else {
                // Password is incorrect
                showAlert("Sign-In Failed", "Incorrect password.", Alert.AlertType.ERROR);
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
}




