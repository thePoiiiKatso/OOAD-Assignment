package bankingsystem.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //  Absolute path (you can shorten to relative later)
    private static final String URL =
            "jdbc:sqlite:C:/Users/Admin/Desktop/OOAD2025/bankingsystem/bank_system.db";

    // Returns a valid connection or null
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the SQLite driver class explicitly
            Class.forName("org.sqlite.JDBC");

            // Connect to the database
            conn = DriverManager.getConnection(URL);
            System.out.println("✅ Connected to SQLite successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ SQLite JDBC driver not found. Make sure sqlite-jdbc-3.50.3.0.jar is added to jGRASP CLASSPATH.");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }

        return conn;
    }
}
