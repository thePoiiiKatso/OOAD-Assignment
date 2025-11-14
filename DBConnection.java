package bankingsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centralized H2 database connection and schema initialization.
 * Automatically creates tables on first run if they don't exist.
 */
public class DBConnection {

    // ✅ Persistent H2 database file (creates ./bankdb.mv.db in your project folder)
    private static final String URL = "jdbc:h2:file:./bankdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection conn;

    static {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            // --- Customers table ---
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS customers (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(100),
                    phone VARCHAR(50),
                    address VARCHAR(255),
                    username VARCHAR(50),
                    password VARCHAR(255)
                )
            """);

            // --- Accounts table ---
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS accounts (
                    acc_no INT AUTO_INCREMENT PRIMARY KEY,
                    type VARCHAR(40),
                    balance DOUBLE,
                    address VARCHAR(100),  -- ✅ was 'branch', now consistent
                    customer_id INT,
                    status VARCHAR(20) DEFAULT 'Pending',
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                )
            """);

            // --- Transactions table ---
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id IDENTITY PRIMARY KEY,
                    acc_no INT,
                    type VARCHAR(30),
                    amount DOUBLE,
                    description VARCHAR(255),
                    tdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (acc_no) REFERENCES accounts(acc_no)
                )
            """);

            System.out.println("✅ H2 database initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }
    }

    private DBConnection() {}

    /** Returns a shared connection to the H2 database (singleton pattern). */
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to H2 database.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 Driver not found. Ensure H2 JAR is in your classpath.", e);
        }
        return conn;
    }

    /** Closes the shared H2 connection safely. */
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
