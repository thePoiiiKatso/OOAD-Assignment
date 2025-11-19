package bankingsystem.dao;

import java.sql.*;

public class DBUtil {

    private static final String URL = "jdbc:h2:./bankdb"; 
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS customers (
                    id IDENTITY PRIMARY KEY,
                    name VARCHAR(100),
                    email VARCHAR(100),
                    phone VARCHAR(50),
                    address VARCHAR(255),
                    username VARCHAR(100) UNIQUE,
                    password VARCHAR(100)
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS accounts (
                    acc_no IDENTITY PRIMARY KEY,
                    type VARCHAR(40),
                    balance DOUBLE,
                    address VARCHAR(255),
                    customer_id INT,
                    status VARCHAR(20),
                    reason VARCHAR(255),
                    employer VARCHAR(255),
                    request_date TIMESTAMP
                )
            """);

            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id IDENTITY PRIMARY KEY,
                    acc_no INT,
                    type VARCHAR(30),
                    amount DOUBLE,
                    description VARCHAR(255),
                    tdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
