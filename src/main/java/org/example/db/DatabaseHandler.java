package org.example.db;

import org.example.Nyro.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 *   Implement a demo database too/ import
 *
 */

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Nyro";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sdr8393";

    // Cached articles
    private List<Article> cachedArticles = new ArrayList<>();

    /**
     * Initializes the DatabaseHandler by loading all articles into memory.
     */
    public DatabaseHandler() {
        this.cachedArticles = getAllArticles(); // Load articles at startup
    }

    /**
     * Returns all articles from memory.
     *
     * @return List of all articles.
     */
    public List<Article> getCachedArticles() {
        return cachedArticles;
    }

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


// User


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
        String query = "INSERT INTO \"user\" (\"email\", \"password\") VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null) return false;

            stmt.setString(1, email);
            stmt.setString(2, password);

            return stmt.executeUpdate() > 0;

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
        String query = "SELECT 1 FROM \"user\" WHERE email = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


// Article


    /**
     * Inserts a news article into the database.
     */
    public boolean insertArticle(String title, String description, String content, String source, Timestamp publishedAt, String url, String imgUrl) {
        String query = "INSERT INTO article (title, description, content, source, \"publishedAt\", \"URL\", \"imgURL\") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, content);
            stmt.setString(4, source);
            stmt.setTimestamp(5, publishedAt);
            stmt.setString(6, url);
            stmt.setString(7, imgUrl);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of random articles from the database.
     */
    public List<Article> getRandomArticles(int limit) {
        String query = "SELECT id, title, \"publishedAt\", description, source, \"URL\", \"imgURL\", category FROM article ORDER BY RANDOM() LIMIT ?";
        List<Article> articles = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getTimestamp("publishedAt"),
                            rs.getString("description"),
                            rs.getString("source"),
                            rs.getString("URL"),
                            rs.getString("imgURL"),
                            rs.getString("category")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    /**
     * Retrieves a list of random articles from the cached articles.
     *
     * @param limit The number of random articles to fetch.
     * @return List of random articles.
     */
    public List<Article> getRandomArticlesFromCache(int limit) {
        List<Article> shuffledArticles = new ArrayList<>(cachedArticles);
        Collections.shuffle(shuffledArticles); // Shuffle the list
        return shuffledArticles.stream().limit(limit).collect(Collectors.toList());
    }


    /**
     * Retrieves all articles from the database.
     */
    public List<Article> getAllArticles() {
            String query = "SELECT id, title, \"publishedAt\", description, content, source, \"URL\", \"imgURL\", category FROM article";
            List<Article> articles = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    articles.add(new Article(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getTimestamp("publishedAt"),
                            rs.getString("description"),
                            rs.getString("content"),
                            rs.getString("source"),
                            rs.getString("URL"),
                            rs.getString("imgURL"),
                            rs.getString("category")
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return articles;
        }


    /**
     * Categorizes uncategorized articles in the cached list.
     *
     * @return The number of articles updated.
     */
    public int categorizeCachedArticles() {
        int updatedCount = 0;

        for (Article article : cachedArticles) {
            if (article.getCategory() == null || article.getCategory().isBlank()) {
                String newCategory = new ArticleCategorizer().categorizeArticle(article.getDescription(), article.getContent());

                if (newCategory != null && !newCategory.isBlank()) {
                    boolean success = updateArticleCategory(article.getId(), newCategory);
                    if (success) {
                        article.setCategory(newCategory); // Update in memory
                        updatedCount++;
                    }
                }
            }
        }
        return updatedCount;
    }

    public boolean updateArticleCategory(int articleId, String category) {
        String query = "UPDATE article SET category = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setString(1, category);
            stmt.setInt(2, articleId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


// USER PREFERENCE


    public boolean addToFavorites(int userId, int articleId) {
        String query = "INSERT INTO favorites (user_id, article_id, added_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, articleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean addToDislikes(int userId, int articleId) {
        String query = "INSERT INTO dislikes (user_id, article_id, added_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, articleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToReadHistory(int userId, int articleId) {
        String query = "INSERT INTO read_history (user_id, article_id, read_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, articleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getUserFavorites(int userId) {
        String query = "SELECT article_id FROM favorites WHERE user_id = ?";
        List<Integer> favorites = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    favorites.add(rs.getInt("article_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    /**
     * Retrieves all articles disliked by the user.
     *
     * @param userId The ID of the user.
     * @return List of article IDs the user disliked.
     */
    public List<Integer> getUserDislikes(int userId) {
        String query = "SELECT article_id FROM dislikes WHERE user_id = ?";
        List<Integer> dislikedArticles = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dislikedArticles.add(rs.getInt("article_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dislikedArticles;
    }

    /**
     * Retrieves all articles read by the user.
     *
     * @param userId The ID of the user.
     * @return List of article IDs the user has read.
     */
    public List<Integer> getUserReadHistory(int userId) {
        String query = "SELECT article_id FROM read_history WHERE user_id = ?";
        List<Integer> readArticles = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    readArticles.add(rs.getInt("article_id"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readArticles;
    }

    /**
     * Loads user preferences (favorites, dislikes, and read history) for a given user.
     *
     * @param user The user object.
     * @return A UserPreference object containing the user's preferences.
     */
    public UserPreference loadUserPreferences(User user) {
        UserPreference userPreference = new UserPreference(user);

        // Load data from the database
        List<Integer> favorites = getUserFavorites(user.getId());
        List<Integer> dislikes = getUserDislikes(user.getId());
        List<Integer> readHistory = getUserReadHistory(user.getId());

        // Add data to the UserPreference object
        favorites.forEach(id -> userPreference.addToFavourites(id, LocalDateTime.now()));
        dislikes.forEach(id -> userPreference.addToDislike(id, LocalDateTime.now()));
        readHistory.forEach(id -> userPreference.addToRead(id, LocalDateTime.now()));

        return userPreference;
    }

//    public void saveUserPreferences(UserPreference userPreference) {
//        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
//        if (loggedInUser != null && loggedInUser.getId() == userPreference.getUser().getId()) {
//            // Save preferences to the database
//            saveFavorites(loggedInUser.getId(), userPreference.getFavourites());
//            saveDislikes(loggedInUser.getId(), userPreference.getDislike());
//            saveReadHistory(loggedInUser.getId(), userPreference.getRead());
//            System.out.println("User preferences saved for user ID: " + loggedInUser.getId());
//        } else {
//            System.out.println("No logged-in user or mismatched user ID. Preferences not saved.");
//        }
//    }
}