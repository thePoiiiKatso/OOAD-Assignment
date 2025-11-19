package bankingsystem.controllers;

import bankingsystem.dao.DBUtil;
import bankingsystem.util.Navigator;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.FileWriter;
import java.sql.*;
import java.time.format.DateTimeFormatter;

/**
 * Controller for ViewTransactions.fxml
 * Displays transactions only for the logged-in customer.
 * Supports search, export, printing, and totals.
 */
public class ViewTransactionsController {

    // ---------------- FXML Controls ----------------
    @FXML private TableView<TransactionRow> tblTransactions;
    @FXML private TableColumn<TransactionRow, Number> colTransactionID;
    @FXML private TableColumn<TransactionRow, String> colType;
    @FXML private TableColumn<TransactionRow, Number> colAmount;
    @FXML private TableColumn<TransactionRow, String> colDate;
    @FXML private TableColumn<TransactionRow, String> colTime;
    @FXML private TableColumn<TransactionRow, String> colAccount;
    @FXML private TableColumn<TransactionRow, String> colDescription;

    @FXML private TextField txtSearch;
    @FXML private Label lblTotalTransactions;
    @FXML private Label lblTotalDeposits;
    @FXML private Label lblTotalWithdrawals;
    @FXML private Label lblFeedback;

    private final ObservableList<TransactionRow> data = FXCollections.observableArrayList();

    // The logged-in customer's ID
    private int customerId;

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    // ---------------- INITIALIZATION ----------------
    @FXML
    private void initialize() {
        setupTableColumns();
    }

    private void setupTableColumns() {
        colTransactionID.setCellValueFactory(d -> d.getValue().idProperty());
        colType.setCellValueFactory(d -> d.getValue().typeProperty());
        colAmount.setCellValueFactory(d -> d.getValue().amountProperty());
        colDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colTime.setCellValueFactory(d -> d.getValue().timeProperty());
        colAccount.setCellValueFactory(d -> d.getValue().accountProperty());
        colDescription.setCellValueFactory(d -> d.getValue().descriptionProperty());
        tblTransactions.setItems(data);
    }

    // ---------------- LOAD DATA ----------------
    public void loadCustomerTransactions() {
        data.clear();
        if (customerId == 0) {
            lblFeedback.setText("⚠ No customer session detected.");
            return;
        }

        String sql = """
            SELECT t.* FROM transactions t
            JOIN accounts a ON t.acc_no = a.acc_no
            WHERE a.customer_id = ?
            ORDER BY t.id DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) addRow(rs);
            updateSummary();
            lblFeedback.setText("✅ Transactions loaded for customer ID: " + customerId);

        } catch (Exception e) {
            lblFeedback.setText("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("tdate");
        String date = ts.toLocalDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = ts.toLocalDateTime().toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        data.add(new TransactionRow(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getDouble("amount"),
                date,
                time,
                String.valueOf(rs.getInt("acc_no")),
                rs.getString("description") == null ? "" : rs.getString("description")
        ));
    }

    // ---------------- SEARCH ----------------
    @FXML
    private void handleSearch() {
        String query = txtSearch.getText();
        if (query == null || query.isBlank()) {
            loadCustomerTransactions();
            return;
        }

        data.clear();
        String sql = """
            SELECT t.* FROM transactions t
            JOIN accounts a ON t.acc_no = a.acc_no
            WHERE a.customer_id = ?
            AND (
                CAST(t.id AS VARCHAR) LIKE ? OR
                LOWER(t.type) LIKE ? OR
                CAST(t.tdate AS VARCHAR) LIKE ?
            )
            ORDER BY t.id DESC
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            String like = "%" + query.toLowerCase() + "%";
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) addRow(rs);
            updateSummary();
            lblFeedback.setText(data.isEmpty()
                    ? "No results found."
                    : "Results for: " + query);

        } catch (Exception e) {
            lblFeedback.setText("Search error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowAll() {
        txtSearch.clear();
        loadCustomerTransactions();
    }

    // ---------------- EXPORT TO CSV ----------------
    @FXML
    private void handleExport() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Transactions");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            java.io.File file = chooser.showSaveDialog(tblTransactions.getScene().getWindow());
            if (file == null) return;

            try (FileWriter fw = new FileWriter(file)) {
                fw.write("ID,Type,Amount,Date,Time,Account,Description\n");
                for (TransactionRow t : data) {
                    fw.write(String.format("%d,%s,%.2f,%s,%s,%s,%s\n",
                            t.getId(), t.getType(), t.getAmount(),
                            t.getDate(), t.getTime(), t.getAccount(), t.getDescription()));
                }
            }
            lblFeedback.setText("✅ Exported successfully: " + file.getName());
        } catch (Exception e) {
            lblFeedback.setText("Export error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- PRINT ----------------
    @FXML
    private void handlePrint() {
        try {
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(tblTransactions.getScene().getWindow())) {
                boolean success = job.printPage(tblTransactions);
                if (success) {
                    job.endJob();
                    lblFeedback.setText("✅ Transactions printed successfully.");
                } else {
                    lblFeedback.setText("Print job failed.");
                }
            }
        } catch (Exception e) {
            lblFeedback.setText("Print error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- BACK ----------------
    @FXML
    private void handleBack() {
        Navigator.goToWithController("/Dashboard.fxml", 873, 700, controller -> {
            if (controller instanceof DashboardController dash) {
                dash.setCustomerId(customerId);
            }
        });
    }

    // ---------------- SUMMARY ----------------
    private void updateSummary() {
        double deposits = 0, withdrawals = 0;
        for (TransactionRow r : data) {
            if (r.getType().toLowerCase().contains("deposit")) deposits += r.getAmount();
            else if (r.getType().toLowerCase().contains("withdraw")) withdrawals += r.getAmount();
        }
        lblTotalTransactions.setText(String.valueOf(data.size()));
        lblTotalDeposits.setText("P " + String.format("%.2f", deposits));
        lblTotalWithdrawals.setText("P " + String.format("%.2f", withdrawals));
    }

    // ---------------- INNER MODEL CLASS ----------------
    public static class TransactionRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty type;
        private final SimpleDoubleProperty amount;
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty account;
        private final SimpleStringProperty description;

        public TransactionRow(int id, String type, double amount, String date, String time,
                              String account, String description) {
            this.id = new SimpleIntegerProperty(id);
            this.type = new SimpleStringProperty(type);
            this.amount = new SimpleDoubleProperty(amount);
            this.date = new SimpleStringProperty(date);
            this.time = new SimpleStringProperty(time);
            this.account = new SimpleStringProperty(account);
            this.description = new SimpleStringProperty(description);
        }

        public int getId() { return id.get(); }
        public String getType() { return type.get(); }
        public double getAmount() { return amount.get(); }
        public String getDate() { return date.get(); }
        public String getTime() { return time.get(); }
        public String getAccount() { return account.get(); }
        public String getDescription() { return description.get(); }

        public SimpleIntegerProperty idProperty() { return id; }
        public SimpleStringProperty typeProperty() { return type; }
        public SimpleDoubleProperty amountProperty() { return amount; }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty timeProperty() { return time; }
        public SimpleStringProperty accountProperty() { return account; }
        public SimpleStringProperty descriptionProperty() { return description; }
    }
}
