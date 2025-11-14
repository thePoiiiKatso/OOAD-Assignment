package bankingsystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling transaction records.
 * Connected to embedded H2 database via DBConnection.
 * Supports recording, retrieving, and logging transactions with descriptions.
 */
public class TransactionDAO {

    /**
     * Records a new transaction without a description.
     * Automatically delegates to the overloaded version with an empty string.
     */
    public void record(int accNo, String type, double amount) {
        record(accNo, type, amount, "");
    }

    /**
     * Records a new transaction with an optional description.
     *
     * @param accNo       Account number associated with the transaction
     * @param type        Transaction type (e.g., Deposit, Withdrawal, Transfer)
     * @param amount      Transaction amount
     * @param description Optional transaction details
     */
    public void record(int accNo, String type, double amount, String description) {
        String sql = "INSERT INTO transactions(acc_no, type, amount, description) VALUES(?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, description);
            ps.executeUpdate();

            System.out.println("✅ Transaction recorded: " + type + " | Account #" + accNo + " | Amount: P" + amount);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Transaction record failed: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all transactions for a specific account number.
     *
     * @param accNo The account number to search transactions for
     * @return A list of formatted transaction strings for display
     */
    public List<String> findByAccount(int accNo) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE acc_no = ? ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Timestamp t = rs.getTimestamp("tdate");
                String entry = String.format(
                        "%-10s | P%-10.2f | %s | %s",
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        t.toLocalDateTime(),
                        rs.getString("description") == null ? "" : rs.getString("description")
                );
                list.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Initializes the transactions table if it doesn't already exist.
     * This ensures the schema always matches what your app expects.
     */
    public static void initializeTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id IDENTITY PRIMARY KEY,
                    acc_no INT NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    amount DOUBLE NOT NULL,
                    description VARCHAR(255),
                    tdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;
        try (Connection c = DBConnection.getConnection();
             Statement stmt = c.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Transactions table verified/created.");
        } catch (SQLException e) {
            System.err.println("❌ Error initializing transactions table: " + e.getMessage());
        }
    }
}
