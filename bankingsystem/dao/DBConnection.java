package bankingsystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private static final String URL =
            "jdbc:h2:C:/Users/Admin/Desktop/BANKING SYSTEM JAVA/bankdb;AUTO_SERVER=TRUE";

    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");

            try (Connection c = getConnection();
                 Statement s = c.createStatement()) {

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

                s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        acc_no INT AUTO_INCREMENT PRIMARY KEY,
                        type VARCHAR(40),
                        balance DOUBLE,
                        address VARCHAR(100),
                        customer_id INT,
                        status VARCHAR(20) DEFAULT 'ACTIVE',
                        reason VARCHAR(255),
                        employer VARCHAR(255),
                        request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (customer_id) REFERENCES customers(id)
                    )
                """);

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

                System.out.println("✔ REAL H2 schema loaded");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection() { }
}
