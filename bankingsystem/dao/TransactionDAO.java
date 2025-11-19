package bankingsystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void record(int accNo, String type, double amount) {
        record(accNo, type, amount, "");
    }

    public void record(int accNo, String type, double amount, String description) {

        String sql = "INSERT INTO transactions(acc_no, type, amount, description) VALUES(?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, description);

            ps.executeUpdate();

            System.out.println("Transaction OK: " + type + " for acc " + accNo);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Transaction record failed: " + e.getMessage());
        }
    }

    public List<String> findByAccount(int accNo) {

        List<String> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE acc_no = ? ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp t = rs.getTimestamp("tdate");

                    String entry = String.format(
                            "%-10s | P%-10.2f | %s | %s",
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            t != null ? t.toLocalDateTime() : "",
                            rs.getString("description") == null ? "" : rs.getString("description")
                    );

                    list.add(entry);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
