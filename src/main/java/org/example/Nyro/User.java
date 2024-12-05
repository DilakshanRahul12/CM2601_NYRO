package org.example.Nyro;

import org.example.db.DatabaseHandler;

import java.util.regex.Pattern;

public class User {
    private int id;
    private String email;
    private String password;

    // Regex pattern for email validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    //Constructor
    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    //Overloaded constructor for creating a User object without ID (during sign-up)
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Utility Methods

    // Static utility method for email validation
    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(email).matches();
    }
    // Checks if two passwords match
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    // Factory method for user authentication
    public static User authenticate(String email, String password, DatabaseHandler dbHandler) {
        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        return dbHandler.authenticateUser(email, password);
    }

    // Factory method for user creation
    public static boolean register(String email, String password, String confirmPassword, DatabaseHandler dbHandler) {
        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!passwordsMatch(password, confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        return dbHandler.insertUser(email, password);
    }
}
