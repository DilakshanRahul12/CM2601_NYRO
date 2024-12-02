package org.example.Nyro;

import java.util.regex.Pattern;

public class User {
    private int id;
    private String email;
    private String password;

    // Regex pattern for email validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    // Constructor
    public User(int id, String email, String password) {
        this.email = email;
        this.password = password;
        this.id = id; // Initialize preferences
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter methods
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

    // Method to validate the email format
    public boolean isValidEmail() {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(this.email).matches();
    }

    // Method to check if passwords match (can be extended to check password requirements)
    public boolean checkPasswordsMatch(String confirmPassword) {
        return this.password.equals(confirmPassword);
    }


}
