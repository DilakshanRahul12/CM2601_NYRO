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
    public User authenticateUser(String email, String password) {
        // Check credentials in the database
        String query = "SELECT * FROM \"user\" WHERE email = ? AND password = ?";
        try (Connection conn = connect();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Return a User object with the fetched data
                return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if authentication fails
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM \"user\" WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        String query = "INSERT INTO favourites (user_id, article_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        return executeUpdate(query, userId, articleId);
    }

    // Add to read history
    public boolean addToReadHistory(int userId, int articleId) {
        String query = "INSERT INTO read_history (user_id, article_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        return executeUpdate(query, userId, articleId);
    }

    // Add to dislikes
    public boolean addToDislikes(int userId, int articleId) {
        String query = "INSERT INTO dislikes (user_id, article_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        return executeUpdate(query, userId, articleId);
    }

    // Remove from favorites
    public boolean removeFromFavorites(int userId, int articleId) {
        String query = "DELETE FROM favourites WHERE user_id = ? AND article_id = ?";
        return executeUpdate(query, userId, articleId);
    }

    // Remove from dislikes
    public boolean removeFromDislikes(int userId, int articleId) {
        String query = "DELETE FROM dislikes WHERE user_id = ? AND article_id = ?";
        return executeUpdate(query, userId, articleId);
    }

    // General method to execute updates
    private boolean executeUpdate(String query, int userId, int articleId) {
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

    // Add this method to your DatabaseHandler class

    public UserPreference getUserPreference(int userId) {
        String favQuery = "SELECT article_id, added_at FROM favourites WHERE user_id = ?";
        String readQuery = "SELECT article_id, read_at FROM read_history WHERE user_id = ?";
        String dislikeQuery = "SELECT article_id, added_at FROM dislikes WHERE user_id = ?";

        UserPreference userPreference = new UserPreference(new User(userId, "", "")); // Assuming you create a User object with the userId only

        try (Connection conn = connect()) {
            // Fetching favourites
            try (PreparedStatement stmt = conn.prepareStatement(favQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int articleId = rs.getInt("article_id");
                    LocalDateTime timestamp = rs.getTimestamp("added_at").toLocalDateTime();
                    userPreference.addToFavourites(articleId, timestamp);
                }
            }

            // Fetching read history
            try (PreparedStatement stmt = conn.prepareStatement(readQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int articleId = rs.getInt("article_id");
                    LocalDateTime timestamp = rs.getTimestamp("read_at").toLocalDateTime();
                    userPreference.addToRead(articleId, timestamp);
                }
            }

            // Fetching dislikes
            try (PreparedStatement stmt = conn.prepareStatement(dislikeQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int articleId = rs.getInt("article_id");
                    LocalDateTime timestamp = rs.getTimestamp("added_at").toLocalDateTime();
                    userPreference.addToDislike(articleId, timestamp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userPreference;
    }

    public Article getArticleById(int articleId) {
        String query = "SELECT id, title, \"publishedAt\", description, content, source, \"URL\", \"imgURL\", category FROM article WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, articleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Article(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("publishedAt"),
                        rs.getString("description"),
                        rs.getString("content"),
                        rs.getString("source"),
                        rs.getString("URL"),
                        rs.getString("imgURL"),
                        rs.getString("category")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}

