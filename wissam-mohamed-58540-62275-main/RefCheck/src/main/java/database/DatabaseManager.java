package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlite:external-data/database.sqlite";
            return DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new DatabaseException("Connexion ", ex);
        }
    }
}
