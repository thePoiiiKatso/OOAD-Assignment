package bankingsystem.controllers;

import bankingsystem.model.DatabaseConnection;
import bankingsystem.model.TransactionRecord;
import bankingsystem.model.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * ✅ ViewTransactionsController
 * Displays all transactions for the logged-in customer from the
 * bank_transactions table. Allows search/filtering and navigation back to dashboard.
 */
public class ViewTransactionsController {

    @FXML private TableView<TransactionRecord> tblTransactions;
    @FXML private TableColumn<TransactionRecord, String> colAccountType;
    @FXML private TableColumn<TransactionRecord, String> colTransactionID;
    @FXML private TableColumn<TransactionRecord, String> colTransaction;
    @FXML private TableColumn<TransactionRecord, String> colDate;
    @FXML private TableColumn<TransactionRecord, String> colTime;
    @FXML private TextField txtSearch;

    private final ObservableList<TransactionRecord> allTransactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTransactionID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        colTransaction.setCellValueFactory(new PropertyValueFactory<>("transaction"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAccountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        loadTransactionsFromDatabase();
    }

    /** ✅ Load transactions belonging to the current user */
    private void loadTransactionsFromDatabase() {
        allTransactions.clear();

        String sql = """
            SELECT 
                t.id AS transactionID,
                t.type AS transaction,
                t.reference,
                t.amount,
                t.date,
                a.accountType
            FROM bank_transactions t
            JOIN customers c ON t.customer_id = c.id
            LEFT JOIN accounts a ON a.customer_id = c.id
            WHERE c.name = ?
            ORDER BY t.date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Session.currentUserName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String fullDate = rs.getString("date");
                String datePart = fullDate;
                String timePart = "";

                if (fullDate != null && fullDate.contains(" ")) {
                    String[] parts = fullDate.split(" ");
                    datePart = parts[0];
                    timePart = parts[1];
                }

                String transactionText = rs.getString("transaction") +
                        " (P" + String.format("%.2f", rs.getDouble("amount")) + ")" +
                        (rs.getString("reference") != null ? " - " + rs.getString("reference") : "");

                allTransactions.add(new TransactionRecord(
                        String.valueOf(rs.getInt("transactionID")),
                        transactionText,
                        datePart,
                        timePart,
                        rs.getString("accountType") != null ? rs.getString("accountType") : "—",
                        rs.getString("reference")
                ));
            }

            tblTransactions.setItems(allTransactions);

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Failed to load transactions: " + e.getMessage());
        }
    }

    /** 🔍 Filter transactions by search term */
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            tblTransactions.setItems(allTransactions);
            return;
        }

        ObservableList<TransactionRecord> filtered = FXCollections.observableArrayList(
                allTransactions.stream()
                        .filter(t ->
                                t.getTransactionID().toLowerCase().contains(query) ||
                                t.getTransaction().toLowerCase().contains(query) ||
                                t.getDate().toLowerCase().contains(query) ||
                                t.getAccountType().toLowerCase().contains(query))
                        .collect(Collectors.toList())
        );

        tblTransactions.setItems(filtered);
    }

    /** 🔙 Navigate back to dashboard */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard – Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Unable to return to dashboard.");
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
