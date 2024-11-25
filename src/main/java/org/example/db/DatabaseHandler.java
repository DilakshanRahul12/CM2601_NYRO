package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}

