package bankingsystem.controllers;

import bankingsystem.model.DatabaseConnection;
import bankingsystem.model.Session;
import bankingsystem.model.TransactionService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles account-to-account transfers within a single user's profile.
 * Automatically logs all transfers to the transactions table via TransactionService.
 */
public class ViewAccountsController {

    @FXML private ComboBox<String> comboAccountFrom;
    @FXML private ComboBox<String> comboAccountTo;
    @FXML private TextField txtTransferAmount;
    @FXML private Label lblBalance;
    @FXML private Button btnTransfer;

    private final Map<String, Integer> accountIds = new HashMap<>();
    private final Map<String, Double> accountBalances = new HashMap<>();

    @FXML
    public void initialize() {
        loadUserAccounts();
        comboAccountFrom.setOnAction(e -> showBalance(comboAccountFrom.getValue()));
    }

    /** ✅ Load all accounts belonging to the logged-in user */
    private void loadUserAccounts() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT account_id, accountType, balance
                FROM accounts
                WHERE customer_id = (SELECT id FROM customers WHERE name = ?)
            """);
            ps.setString(1, Session.currentUserName);
            ResultSet rs = ps.executeQuery();

            comboAccountFrom.getItems().clear();
            comboAccountTo.getItems().clear();
            accountIds.clear();
            accountBalances.clear();

            while (rs.next()) {
                String accountType = rs.getString("accountType");
                int accountId = rs.getInt("account_id");
                double balance = rs.getDouble("balance");

                comboAccountFrom.getItems().add(accountType);
                comboAccountTo.getItems().add(accountType);

                accountIds.put(accountType, accountId);
                accountBalances.put(accountType, balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load accounts: " + e.getMessage());
        }
    }

    /** ✅ Display balance of selected source account */
    private void showBalance(String accountName) {
        Double balance = accountBalances.get(accountName);
        if (balance != null) {
            lblBalance.setText("Balance: P" + String.format("%.2f", balance));
        } else {
            lblBalance.setText("Balance: —");
        }
    }

    /** ✅ Handle account-to-account transfers */
    @FXML
    private void handleTransfer(ActionEvent event) {
        String from = comboAccountFrom.getValue();
        String to = comboAccountTo.getValue();
        String amountStr = txtTransferAmount.getText();

        if (from == null || to == null || amountStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Select both accounts and enter amount.");
            return;
        }

        if (from.equals(to)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Transfer", "You cannot transfer between the same account.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            double amount = Double.parseDouble(amountStr);
            double fromBalance = accountBalances.get(from);
            double toBalance = accountBalances.get(to);

            if (fromBalance < amount) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "Not enough balance in " + from + ".");
                return;
            }

            double newFromBalance = fromBalance - amount;
            double newToBalance = toBalance + amount;

            // ✅ Update both accounts
            PreparedStatement update = conn.prepareStatement("UPDATE accounts SET balance = ? WHERE account_id = ?");
            update.setDouble(1, newFromBalance);
            update.setInt(2, accountIds.get(from));
            update.executeUpdate();

            update.setDouble(1, newToBalance);
            update.setInt(2, accountIds.get(to));
            update.executeUpdate();

            // ✅ Log the transfer via TransactionService
            TransactionService.recordTransaction(
                    Session.currentUserName,
                    "Account Transfer",
                    from + " → " + to,
                    amount,
                    accountIds.get(from),
                    accountIds.get(to)
            );

            Session.currentBalance = newFromBalance;
            accountBalances.put(from, newFromBalance);
            accountBalances.put(to, newToBalance);

            showAlert(Alert.AlertType.INFORMATION, "✅ Transfer Successful",
                    "P" + amount + " transferred from " + from + " to " + to + ".");

            // ✅ Refresh UI balances
            loadUserAccounts();
            lblBalance.setText("Balance: P" + String.format("%.2f", newFromBalance));

            // Optional: go to transactions view
            goToTransactions(event);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Enter a valid numeric amount.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        goToDashboard(event);
    }

    // ==================== Navigation Helpers ====================
    private void goToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Dashboard2.fxml");
        }
    }

    private void goToTransactions(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/ViewTransactions.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction History - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load ViewTransactions.fxml");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
