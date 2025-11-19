package bankingsystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // ---------------------------------------------------------
    // INSERT CUSTOMER
    // ---------------------------------------------------------
    public int insert(String name, String email, String phone, String address,
                      String username, String password) {

        String sql = """
            INSERT INTO customers (name, email, phone, address, username, password)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBConnection.getConnection();   // DO NOT CLOSE
            ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setString(5, username);
            ps.setString(6, password);

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();

            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ Customer inserted with ID: " + id);
                return id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Customer insert failed: " + e.getMessage(), e);

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        return -1;
    }

    // ---------------------------------------------------------
    // SAFE UPSERT (NO DUPLICATES WILL BE CREATED)
    // ---------------------------------------------------------
    public void upsert(int id, String name, String email, String phone, String address,
                       String username, String password) {

        String updateSql = """
            UPDATE customers
            SET name = ?, email = ?, phone = ?, address = ?, username = ?, password = ?
            WHERE id = ?
        """;

        String insertSql = """
            INSERT INTO customers (id, name, email, phone, address, username, password)
            SELECT ?, ?, ?, ?, ?, ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM customers WHERE id = ?)
        """;

        Connection c = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;

        try {
            c = DBConnection.getConnection();

            // ---- Try updating first ----
            psUpdate = c.prepareStatement(updateSql);
            psUpdate.setString(1, name);
            psUpdate.setString(2, email);
            psUpdate.setString(3, phone);
            psUpdate.setString(4, address);
            psUpdate.setString(5, username);
            psUpdate.setString(6, password);
            psUpdate.setInt(7, id);

            int updated = psUpdate.executeUpdate();

            if (updated > 0) {
                System.out.println("✅ Updated customer ID: " + id);
                return;
            }

            // ---- If no row updated, insert ----
            psInsert = c.prepareStatement(insertSql);
            psInsert.setInt(1, id);
            psInsert.setString(2, name);
            psInsert.setString(3, email);
            psInsert.setString(4, phone);
            psInsert.setString(5, address);
            psInsert.setString(6, username);
            psInsert.setString(7, password);
            psInsert.setInt(8, id);

            int inserted = psInsert.executeUpdate();

            if (inserted > 0) {
                System.out.println("⭐ Inserted new customer ID (fallback): " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Safe upsert failed: " + e.getMessage(), e);

        } finally {
            try { if (psUpdate != null) psUpdate.close(); } catch (SQLException ignored) {}
            try { if (psInsert != null) psInsert.close(); } catch (SQLException ignored) {}
        }
    }

    // ---------------------------------------------------------
    // FIND ALL CUSTOMERS
    // ---------------------------------------------------------
    public List<CustomerRecord> findAll() {

        List<CustomerRecord> list = new ArrayList<>();

        String sql = "SELECT * FROM customers ORDER BY id";

        Connection c = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            c = DBConnection.getConnection();
            st = c.createStatement();
            rs = st.executeQuery(sql);

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

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (st != null) st.close(); } catch (SQLException ignored) {}
        }

        return list;
    }

    // ---------------------------------------------------------
    // FIND BY USERNAME + PASSWORD (LOGIN)
    // ---------------------------------------------------------
    public CustomerRecord findByUsernameAndPassword(String username, String password) {

        String sql = "SELECT * FROM customers WHERE username = ? AND password = ?";

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

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

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Login query failed: " + e.getMessage(), e);

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        return null;
    }

    // ---------------------------------------------------------
    // FIND BY ID
    // ---------------------------------------------------------
    public CustomerRecord findById(int id) {

        String sql = "SELECT * FROM customers WHERE id = ?";

        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);

            ps.setInt(1, id);

            rs = ps.executeQuery();

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

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        return null;
    }

    // ---------------------------------------------------------
    // COUNT ALL CUSTOMERS
    // ---------------------------------------------------------
    public int countAll() {

        String sql = "SELECT COUNT(*) FROM customers";

        Connection c = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            c = DBConnection.getConnection();
            st = c.createStatement();
            rs = st.executeQuery(sql);

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (st != null) st.close(); } catch (SQLException ignored) {}
        }

        return 0;
    }

    // ---------------------------------------------------------
    // DELETE CUSTOMER
    // ---------------------------------------------------------
    public boolean delete(int id) {

        String sql = "DELETE FROM customers WHERE id = ?";

        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = DBConnection.getConnection();
            ps = c.prepareStatement(sql);

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        return false;
    }

    // ---------------------------------------------------------
    // DTO CLASS
    // ---------------------------------------------------------
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
