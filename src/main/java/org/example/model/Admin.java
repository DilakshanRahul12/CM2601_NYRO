package org.example.model;

public class Admin {
    private int adminId;
    private String password;
    private String email;

    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
