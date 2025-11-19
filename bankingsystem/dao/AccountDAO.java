package bankingsystem.dao;

import bankingsystem.model.BankAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // -----------------------------------------------------
    // CREATE NEW ACCOUNT (AUTO-GENERATED acc_no)
    // -----------------------------------------------------
    public int create(BankAccount a) {

        String insertSQL = """
            INSERT INTO accounts(type, balance, address, customer_id, status,
                                 reason, employer, request_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getAccountType());
            ps.setDouble(2, a.getBalance());
            ps.setString(3, a.getAddress());
            ps.setInt(4, a.getCustomerID());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getReason());
            ps.setString(7, a.getEmployer());
            ps.setTimestamp(8, new Timestamp(a.getRequestDate().getTime()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int accNo = rs.getInt(1);
                    a.setAccountNumber(accNo);
                    return accNo;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // -----------------------------------------------------
    // SAVE (INSERT OR UPDATE)
    // -----------------------------------------------------
    public void save(BankAccount a) {

        if (a.getAccountNumber() == 0) {
            create(a);
            return;
        }

        String updateSQL = """
            UPDATE accounts
            SET type=?, balance=?, address=?, customer_id=?, status=?,
                reason=?, employer=?, request_date=?
            WHERE acc_no=?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(updateSQL)) {

            ps.setString(1, a.getAccountType());
            ps.setDouble(2, a.getBalance());
            ps.setString(3, a.getAddress());
            ps.setInt(4, a.getCustomerID());
            ps.setString(5, a.getStatus());
            ps.setString(6, a.getReason());
            ps.setString(7, a.getEmployer());
            ps.setTimestamp(8, new Timestamp(a.getRequestDate().getTime()));
            ps.setInt(9, a.getAccountNumber());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------
    // FIND BY ID
    // -----------------------------------------------------
    public BankAccount findById(int accNo) {

        String sql = "SELECT * FROM accounts WHERE acc_no = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, accNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

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
                        reqDate != null ? new java.util.Date(reqDate.getTime()) : new java.util.Date(),
                        status
                ) {
                    @Override public void deposit(double amount) { balance += amount; }

                    @Override public boolean withdraw(double amount) {
                        if (amount <= balance) { balance -= amount; return true; }
                        return false;
                    }

                    @Override public String getAccountType() { return type; }
                };

                account.setReason(reason);
                account.setEmployer(employer);

                return account;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // -----------------------------------------------------
    // NEW METHOD — FIND BY CUSTOMER ID
    // -----------------------------------------------------
    public List<BankAccount> findByCustomerId(int customerId) {

        List<BankAccount> list = new ArrayList<>();

        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY acc_no";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int accNo = rs.getInt("acc_no");
                String type = rs.getString("type");
                double balance = rs.getDouble("balance");
                String address = rs.getString("address");
                String status = rs.getString("status");
                String reason = rs.getString("reason");
                String employer = rs.getString("employer");
                Timestamp reqDate = rs.getTimestamp("request_date");

                BankAccount account = new BankAccount(
                        accNo,
                        balance,
                        address,
                        customerId,
                        reqDate != null ? new java.util.Date(reqDate.getTime()) : new java.util.Date(),
                        status
                ) {
                    @Override public void deposit(double amt) { balance += amt; }

                    @Override public boolean withdraw(double amt) {
                        if (amt <= balance) { balance -= amt; return true; }
                        return false;
                    }

                    @Override public String getAccountType() { return type; }
                };

                account.setReason(reason);
                account.setEmployer(employer);

                list.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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
                        reqDate != null ? new java.util.Date(reqDate.getTime()) : new java.util.Date(),
                        status
                ) {
                    @Override public void deposit(double amount) { balance += amount; }

                    @Override public boolean withdraw(double amount) {
                        if (amount <= balance) { balance -= amount; return true; }
                        return false;
                    }

                    @Override public String getAccountType() { return type; }
                };

                account.setReason(reason);
                account.setEmployer(employer);

                list.add(account);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // -----------------------------------------------------
    // FIND ALL ACCOUNTS WITH CUSTOMER DETAILS (DTO)
    // -----------------------------------------------------
    public List<AccountRecord> findAllDetailed() {

        List<AccountRecord> list = new ArrayList<>();

        String sql = """
            SELECT 
                a.acc_no,
                a.type,
                a.balance,
                a.status,
                a.reason,
                a.employer,
                a.request_date,
                c.id AS customer_id,
                c.name AS customer_name,
                c.address AS customer_address,
                c.username
            FROM accounts a
            JOIN customers c ON a.customer_id = c.id
            ORDER BY a.acc_no
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
                        rs.getString("customer_address"),
                        rs.getString("username")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // -----------------------------------------------------
    // COUNTS
    // -----------------------------------------------------
    public int countAll() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM accounts");
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countActive() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM accounts WHERE status='ACTIVE'");
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countCustomers() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(DISTINCT customer_id) FROM accounts");
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countPending() {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM accounts WHERE status='PENDING'");
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // -----------------------------------------------------
    // DELETE
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
    // DTO FOR TABLE VIEWS
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

        public AccountRecord(int accNo, String type, double balance, String status,
                             String reason, String employer, Timestamp requestDate,
                             int customerId, String customerName,
                             String address, String username) {

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
