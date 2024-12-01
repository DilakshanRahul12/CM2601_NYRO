    package org.example.db;
    
    import org.example.Nyro.Article;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

    /**
     *                           Implement a demo database too
     *                           Create a env
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

        public boolean insertArticle(String title, String description, String content, String source, Timestamp publishedAt, String URL, String imgURL) {
            String insertQuery = "INSERT INTO article (title, description, content, source, \"publishedAt\", \"URL\", \"imgURL\") VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

                stmt.setString(1, title);
                stmt.setString(2, description);
                stmt.setString(3, content); // Assuming no content is provided
                stmt.setString(4, source);
                stmt.setTimestamp(5, publishedAt); // Pass Timestamp
                stmt.setString(6, URL);
                stmt.setString(7, imgURL);

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        public List<Article> getRandomArticles(int limit) {
            String query = "SELECT id ,title, \"publishedAt\", description, source, \"URL\", \"imgURL\" FROM article ORDER BY RANDOM() LIMIT ?";
            List<Article> articleList = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, limit);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    Timestamp publishedAt = rs.getTimestamp("publishedAt");
                    String description = rs.getString("description");
                    String source = rs.getString("source");
                    String url = rs.getString("URL");
                    String imgURL = rs.getString("imgURL");

                    Article article = new Article(id, title, publishedAt, description, source, url, imgURL);
                    articleList.add(article);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return articleList;
        }

            public List<Article> getAllArticles() {
            String query = "SELECT id ,title, \"publishedAt\", description, source, \"URL\", \"imgURL\" FROM article";
            List<Article> articleList = new ArrayList<>();

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    Timestamp publishedAt = rs.getTimestamp("publishedAt");
                    String description = rs.getString("description");
                    String source = rs.getString("source");
                    String url = rs.getString("URL");
                    String imgURL = rs.getString("imgURL");

                    Article article = new Article(id, title, publishedAt, description, source, url, imgURL);
                    articleList.add(article);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return articleList;
        }

        /**
         * Updates the keywords column of an article based on its ID.
         *
         * @param articleId The article ID to update.
         * @param keywords  The list of keywords to set for the article.
         * @return true if the update was successful, false otherwise.
         */

        public boolean updateArticleKeywords(int articleId, List<String> keywords) {
            String updateQuery = "UPDATE article SET keywords = ? WHERE id = ?";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                // Clean up the keywords list
                List<String> cleanKeywords = keywords.stream()
                        .map(keyword -> keyword.replaceAll("[^\\x20-\\x7E]", "")) // Remove non-ASCII characters
                        .filter(keyword -> !keyword.isBlank()) // Remove blank or empty strings
                        .collect(Collectors.toList());

                if (cleanKeywords.isEmpty()) {
                    System.out.println("Cleaned keywords list is empty, skipping update.");
                    return false;
                }

                // Convert to SQL array
                String[] keywordsArray = cleanKeywords.toArray(new String[0]);
                Array sqlArray = conn.createArrayOf("text", keywordsArray);

                // Construct the debug query string
                String debugQuery = String.format(
                        "UPDATE article SET keywords = '%s' WHERE id = %d;",
                        "{" + String.join(", ", cleanKeywords.stream()
                                .map(keyword -> "\"" + keyword + "\"") // Add quotes around each keyword
                                .collect(Collectors.toList())) + "}",
                        articleId
                );

                // Debugging output
                    System.out.println("Debug Query: " + debugQuery);

                // Set parameters and execute update
                stmt.setArray(1, sqlArray);
                stmt.setInt(2, articleId);

                int rowsUpdated = stmt.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated);
                return rowsUpdated > 0;

            } catch (SQLException e) {
                System.out.println("Error executing update query:");
                e.printStackTrace();
                return false;
            }
        }

        public boolean updateArticleCategory(int articleId, String category) {
            String updateQuery = "UPDATE article SET category = ? WHERE id = ?";

            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                stmt.setString(1, category);
                stmt.setInt(2, articleId);

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }




    }


