package org.example.db;

import org.example.Nyro.Article;
import org.example.Nyro.ArticleCategorizer;
import java.sql.*;
import java.util.ArrayList;
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

    /**
     * Inserts an article into the database.
     *
     * @return true if the insertion was successful, false otherwise.
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
        String query = "SELECT id, title, \"publishedAt\", description, source, \"URL\", \"imgURL\" FROM article ORDER BY RANDOM() LIMIT ?";
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
                            rs.getString("imgURL")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
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
     * Updates the keywords of an article.
     */
    public boolean updateArticleKeywords(int articleId, List<String> keywords) {
        String query = "UPDATE article SET keywords = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            List<String> cleanKeywords = keywords.stream()
                    .map(keyword -> keyword.replaceAll("[^\\x20-\\x7E]", ""))
                    .filter(keyword -> !keyword.isBlank())
                    .collect(Collectors.toList());

            if (cleanKeywords.isEmpty()) return false;

            Array sqlArray = conn.createArrayOf("text", cleanKeywords.toArray(new String[0]));

            stmt.setArray(1, sqlArray);
            stmt.setInt(2, articleId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Categorizes uncategorized articles in the database.
     *
     * @return The number of articles updated.
     */
    public int categorizeAllArticles() {
        String updateQuery = "UPDATE article SET category = ? WHERE id = ?";
        int updatedCount = 0;

        // Fetch all articles
        List<Article> articles = getAllArticles();

        // Filter articles without a category
        List<Article> uncategorizedArticles = articles.stream()
                .filter(article -> article.getCategory() == null || article.getCategory().isBlank())
                .toList();

        try (Connection conn = connect()) {
            if (conn == null) {
                System.out.println("Database connection failed.");
                return 0;
            }

            for (Article article : uncategorizedArticles) {
                // Categorize the article
                String newCategory = new ArticleCategorizer().categorizeArticle(article.getDescription(), article.getContent());

                if (newCategory != null && !newCategory.isBlank()) {
                    // Update the category in the database
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newCategory);
                        updateStmt.setInt(2, article.getId());
                        if (updateStmt.executeUpdate() > 0) {
                            updatedCount++;
                            System.out.println("Article ID: " + article.getId() + " categorized as: " + newCategory);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating article categories:");
            e.printStackTrace();
        }

        return updatedCount;
    }

}