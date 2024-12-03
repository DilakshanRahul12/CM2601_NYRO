package org.example.Nyro;

public class SessionManager {
    private static SessionManager instance;
    private User loggedInUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;

        // Debug Print Statement
        if (user != null) {
            System.out.println("DEBUG: Logged-in user set in SessionManager - " +
                    "User ID: " + user.getId() + ", Email: " + user.getEmail());
        } else {
            System.out.println("DEBUG: Attempted to set logged-in user in SessionManager, but the user is null.");
        }
    }

    public void clearSession() {
        this.loggedInUser = null;
    }
}
