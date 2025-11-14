package bankingsystem.dao;

import bankingsystem.model.BankAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // -----------------------------------------------------
    // SAVE / UPDATE ACCOUNT
    // -----------------------------------------------------
    public void save(BankAccount a) {
        String sql = """
                MERGE INTO accounts(
                    acc_no, type, balance, address, customer_id, status,
                    reason, employer, request_date
                )
                KEY(acc_no) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, a.getAccountNumber());
            ps.setString(2, a.getAccountType());
            ps.setDouble(3, a.getBalance());
            ps.setString(4, a.getAddress());
            ps.setInt(5, a.getCustomerID());
            ps.setString(6, a.getStatus());

            ps.setString(7, a.getReason());
            ps.setString(8, a.getEmployer());
            ps.setTimestamp(9, a.getRequestDate() != null
                    ? new Timestamp(a.getRequestDate().getTime())
                    : new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------
    // GET ALL ACCOUNTS
    // -----------------------------------------------------
    public List<BankAccount> findAll() {
        List<BankAccount> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY acc_no";

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                int accNo = rs.getInt("acc_no");
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");
                String address = rs.getString("address");
                int customerId = rs.getInt("customer_id");
                String status = rs.getString("status");
                String reason = rs.getString("reason");
                String employer = rs.getString("employer");
                Timestamp reqDate = rs.getTimestamp("request_date");

                BankAccount account = new BankAccount(
                        accNo,
                        balance,
                        address,
                        customerId,
                        reqDate != null
                                ? new java.util.Date(reqDate.getTime())
                                : new java.util.Date(),
                        status
                ) {
                    @Override
                    public void deposit(double amount) {
                        balance += amount;
                    }

                    @Override
                    public boolean withdraw(double amount) {
                        if (amount <= balance) {
                            balance -= amount;
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public String getAccountType() {
                        return type;
                    }
                };

                account.setReason(reason);
                account.setEmployer(employer);
                account.setRequestDate(
                        reqDate != null
                                ? new java.util.Date(reqDate.getTime())
                                : new java.util.Date()
                );

                list.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // -----------------------------------------------------
    // COUNT TOTAL ACCOUNTS
    // -----------------------------------------------------
    public int countAll() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM accounts")) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -----------------------------------------------------
    // COUNT ACTIVE ACCOUNTS
    // -----------------------------------------------------
    public int countActive() {
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM accounts WHERE status='ACTIVE'")) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -----------------------------------------------------
    // COUNT UNIQUE CUSTOMERS
    // -----------------------------------------------------
    public int countCustomers() {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM accounts";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -----------------------------------------------------
    // COUNT PENDING ACCOUNTS
    // -----------------------------------------------------
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status='PENDING'";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // -----------------------------------------------------
    // FULL ACCOUNT + CUSTOMER JOIN
    // -----------------------------------------------------
    public List<AccountRecord> findAllDetailed() {

        List<AccountRecord> list = new ArrayList<>();

        String sql = """
                SELECT 
                    a.acc_no, a.type, a.balance, a.status,
                    a.reason, a.employer, a.request_date,
                    c.id AS customer_id, c.name AS customer_name,
                    c.address AS address, c.username AS username
                FROM accounts a
                JOIN customers c ON a.customer_id = c.id
                ORDER BY a.acc_no
                """;

        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new AccountRecord(
                        rs.getInt("acc_no"),
                        rs.getString("type"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getString("reason"),
                        rs.getString("employer"),
                        rs.getTimestamp("request_date"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("address"),
                        rs.getString("username")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // -----------------------------------------------------
    // DELETE ACCOUNT
    // -----------------------------------------------------
    public void delete(int accNo) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM accounts WHERE acc_no=?")) {

            ps.setInt(1, accNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------
    // DTO FOR TABLE ROWS
    // -----------------------------------------------------
    public static class AccountRecord {

        private final int accNo;
        private final String type;
        private final double balance;
        private final String status;

        private final String reason;
        private final String employer;
        private final Timestamp requestDate;

        private final int customerId;
        private final String customerName;
        private final String address;
        private final String username;

        public AccountRecord(
                int accNo, String type, double balance, String status,
                String reason, String employer, Timestamp requestDate,
                int customerId, String customerName,
                String address, String username
        ) {
            this.accNo = accNo;
            this.type = type;
            this.balance = balance;
            this.status = status;
            this.reason = reason;
            this.employer = employer;
            this.requestDate = requestDate;
            this.customerId = customerId;
            this.customerName = customerName;
            this.address = address;
            this.username = username;
        }

        public int getAccNo() { return accNo; }
        public String getType() { return type; }
        public double getBalance() { return balance; }
        public String getStatus() { return status; }

        public String getReason() { return reason; }
        public String getEmployer() { return employer; }
        public Timestamp getRequestDate() { return requestDate; }

        public int getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public String getAddress() { return address; }
        public String getUsername() { return username; }
    }
}
