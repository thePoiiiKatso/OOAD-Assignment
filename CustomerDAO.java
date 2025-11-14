package bankingsystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all CRUD operations for customers.
 */
public class CustomerDAO {

    /**
     * Inserts a new customer and returns the generated ID.
     */
    public int insert(String name, String email, String phone, String address,
                      String username, String password) {
        String sql = """
            INSERT INTO customers (name, email, phone, address, username, password)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, username);
            ps.setString(6, password);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✅ Customer inserted with ID: " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Customer insert failed: " + e.getMessage(), e);
        }
        return -1;
    }

    /**
     * Updates an existing customer or inserts if not found.
     */
    public void upsert(int id, String name, String email, String phone, String address,
                       String username, String password) {
        String sql = """
            MERGE INTO customers KEY(id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, username);
            ps.setString(7, password);
            ps.executeUpdate();

            System.out.println("✅ Customer saved/updated: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Customer upsert failed: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve all customers.
     */
    public List<CustomerRecord> findAll() {
        List<CustomerRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new CustomerRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Find a customer by username and password (for login).
     */
    public CustomerRecord findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM customers WHERE username = ? AND password = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerRecord(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Login query failed: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Find a customer by their ID (used by DashboardController).
     */
    public CustomerRecord findById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerRecord(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Count all customers (for dashboard stats).
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Delete a customer by ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // DTO: Simple data transfer object used across the app.
    // -------------------------------------------------------------------------
    public static class CustomerRecord {
        private final int id;
        private final String name;
        private final String email;
        private final String phone;
        private final String address;
        private final String username;
        private final String password;

        public CustomerRecord(int id, String name, String email, String phone,
                              String address, String username, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.username = username;
            this.password = password;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }
}
