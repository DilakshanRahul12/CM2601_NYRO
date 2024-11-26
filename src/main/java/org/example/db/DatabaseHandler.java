package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Nyro";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sdr8393";

    /**
     * Establishes a connection to the database.
     *
     * @return Connection object if successful, null otherwise.
     */
    public Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validates email format.
     *
     * @param email The email address to validate.
     * @return true if the email is valid, false otherwise.
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Checks if the passwords match.
     *
     * @param password        The primary password.
     * @param confirmPassword The confirmation password.
     * @return true if the passwords match, false otherwise.
     */
    public boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * Inserts a new user into the database.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertUser(String email, String password) {
        String insertQuery = "INSERT INTO \"user\" (\"email\", \"password\") VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            if (conn == null) {
                return false;
            }

            stmt.setString(1, email);
            stmt.setString(2, password);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates a user by email and password.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     * @return true if authentication succeeds, false otherwise.
     */
    public boolean authenticateUser(String email, String password) {
        String authQuery = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(authQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet resultSet = stmt.executeQuery();

            return resultSet.next(); // Return true if a matching user is found

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
