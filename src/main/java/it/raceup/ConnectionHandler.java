package it.raceup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {
    public static Connection getConnection() {
        final String DATABASE = "telemetry_data";
        final String USER = "raceup";
        final String PASSWORD = "raceup";
        final String URL = "jdbc:mysql://db:3306/" + DATABASE + "?serverTimezone=Europe/Rome";   // To use it in localhost change "db" with "localhost"
        Connection connection = null;
        // Load the JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't load database driver");
        }
        try {
            connection = DriverManager.getConnection
                    (URL, USER, PASSWORD);
            System.out.println("Database connected");
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get init.sql connection");
        }
        return connection;
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}