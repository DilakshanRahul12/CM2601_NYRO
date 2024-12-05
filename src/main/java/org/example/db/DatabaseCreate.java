package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseCreate {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/your_database_name";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "sdr8393";

    public static void main(String[] args) {
        createDatabase();
        createTables();
    }

    private static void createDatabase() {
        String postgresUrl = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection connection = DriverManager.getConnection(postgresUrl, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String createDbQuery = "SELECT 1 FROM pg_database WHERE datname = 'your_database_name'";
            ResultSet resultSet = statement.executeQuery(createDbQuery);

            if (!resultSet.next()) {
                statement.executeUpdate("CREATE DATABASE your_database_name");
                System.out.println("Database 'your_database_name' created successfully.");
            } else {
                System.out.println("Database 'your_database_name' already exists.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void createTables() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            // Check and create 'users' table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, email TEXT NOT NULL, password TEXT NOT NULL);");

            // Check and create 'articles' table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS articles (id SERIAL PRIMARY KEY, title TEXT NOT NULL, description TEXT, content TEXT, source TEXT, publishedAt TIMESTAMP, URL TEXT, imgURL TEXT, category TEXT);");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            /**
             * Please uncomment the following code to create the other tables after creating the 'users' and 'articles' tables.
             */
            // Create other tables with references to 'users' and 'articles'
            //statement.executeUpdate("CREATE TABLE IF NOT EXISTS admin (admin_id SERIAL PRIMARY KEY, email TEXT NOT NULL, password TEXT NOT NULL);");
            //statement.executeUpdate("CREATE TABLE IF NOT EXISTS read_history (id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), article_id INT REFERENCES articles(id), read_at TIME);");
            //statement.executeUpdate("CREATE TABLE IF NOT EXISTS favourites (id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), article_id INT REFERENCES articles(id), added_at TIME);");
            //statement.executeUpdate("CREATE TABLE IF NOT EXISTS dislikes (id SERIAL PRIMARY KEY, user_id INT REFERENCES users(id), article_id INT REFERENCES articles(id), added_at TIME);");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
