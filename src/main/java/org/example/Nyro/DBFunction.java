package org.example.Nyro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBFunction {

    private static final String URL = "jdbc:postgresql://localhost:5432/yourdatabase";
    private static final String USER = "postgre";
    private static final String PASSWORD = "sdr8393";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


}
