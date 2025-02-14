package mff.cuni.cz.bortosa.flashy.DatabaseOperations;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class for handling the connection with the local database.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:database/flashcards.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

//public class DatabaseManager {
//    private String dbURL;
//    private String dbUser;
//    private String dbPassword;
//
//    public DatabaseManager() {
//        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
//            Properties properties = new Properties();
//            if (input == null) {
//                throw new IOException("config.properties file not found");
//            }
//            properties.load(input);
//
//            dbURL = properties.getProperty("db.url");
//            dbUser = properties.getProperty("db.user");
//            dbPassword = properties.getProperty("db.password");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(dbURL, dbUser, dbPassword);
//    }
//}
