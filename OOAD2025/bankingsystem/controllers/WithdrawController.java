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

/**
 * Handles withdrawals for Lekgwere Banking System.
 * ❌ Savings Accounts: Withdrawals not allowed (button disabled).
 * ✅ Cheque & Investors Accounts: Withdrawals allowed.
 */
public class WithdrawController {

    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtWithdrawAmount;
    @FXML private TextField txtAvailableBalance;
    @FXML private PasswordField txtPin;
    @FXML private Button btnConfirm;

    @FXML
    private void initialize() {
        cmbAccountType.getItems().addAll("Savings Account", "Cheque Account", "Investors Account");

        // Auto-load balance when account is selected
        cmbAccountType.setOnAction(e -> {
            loadAccountBalance();
            handleAccountTypeSelection();
        });

        // Disable Confirm by default until an account is selected
        btnConfirm.setDisable(true);
    }

    /** ✅ Load account balance for selected account */
    private void loadAccountBalance() {
        String accountType = cmbAccountType.getValue();
        if (accountType == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT a.balance
                FROM accounts a
                JOIN customers c ON a.customer_id = c.id
                WHERE c.name = ? AND a.accountType = ?
            """);
            ps.setString(1, Session.currentUserName);
            ps.setString(2, accountType);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                txtAvailableBalance.setText("P" + String.format("%.2f", balance));
            } else {
                txtAvailableBalance.setText("—");
            }

        } catch (Exception e) {
            e.printStackTrace();
            txtAvailableBalance.setText("Error");
        }
    }

    /** ✅ Handle account type selection (disable withdrawals for Savings) */
    private void handleAccountTypeSelection() {
        String accountType = cmbAccountType.getValue();
        if (accountType == null) return;

        if (accountType.equalsIgnoreCase("Savings Account")) {
            btnConfirm.setDisable(true);
            txtWithdrawAmount.setEditable(false);

            showAlert(Alert.AlertType.INFORMATION,
                    "Savings Account Notice",
                    "💡 Withdrawals are not permitted from a Savings Account.\n" +
                    "Please use your Cheque or Investors Account for withdrawals.");
        } else {
            btnConfirm.setDisable(false);
            txtWithdrawAmount.setEditable(true);
        }
    }

    /** ✅ Handles withdrawal logic */
    @FXML
    private void handleWithdraw(ActionEvent event) {
        String accountType = cmbAccountType.getValue();
        String amountStr = txtWithdrawAmount.getText();
        String pin = txtPin.getText();

        // --- Validation ---
        if (accountType == null || amountStr.isEmpty() || pin.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info",
                    "⚠️ Please select an account type and enter both amount and PIN.");
            return;
        }

        // --- Restrict withdrawals for Savings Accounts ---
        if (accountType.equalsIgnoreCase("Savings Account")) {
            showAlert(Alert.AlertType.ERROR, "Withdrawal Not Allowed",
                    "❌ Withdrawals are not permitted from a Savings Account.\n" +
                    "Please use your Cheque or Investors Account instead.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            double amount = Double.parseDouble(amountStr);

            PreparedStatement ps = conn.prepareStatement("""
                SELECT a.account_id, a.balance, c.pin
                FROM accounts a
                JOIN customers c ON a.customer_id = c.id
                WHERE c.name = ? AND a.accountType = ?
            """);
            ps.setString(1, Session.currentUserName);
            ps.setString(2, accountType);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                String correctPin = rs.getString("pin");
                int accountId = rs.getInt("account_id");

                if (!pin.equals(correctPin)) {
                    showAlert(Alert.AlertType.ERROR, "Invalid PIN", "❌ Incorrect PIN entered.");
                    return;
                }

                if (balance < amount) {
                    showAlert(Alert.AlertType.ERROR, "Insufficient Funds",
                            "💰 Your balance is too low to complete this withdrawal.");
                    return;
                }

                double newBalance = balance - amount;

                // --- Update DB ---
                PreparedStatement update = conn.prepareStatement("""
                    UPDATE accounts SET balance = ? WHERE account_id = ?
                """);
                update.setDouble(1, newBalance);
                update.setInt(2, accountId);
                update.executeUpdate();

                // --- Record transaction ---
                TransactionService.recordTransaction(
                        Session.currentUserName,
                        "Withdrawal",
                        "Cash Withdrawal from " + accountType,
                        amount,
                        accountId,
                        null
                );

                Session.currentBalance = newBalance;
                txtAvailableBalance.setText("P" + String.format("%.2f", newBalance));

                showAlert(Alert.AlertType.INFORMATION, "✅ Success",
                        "You withdrew P" + String.format("%.2f", amount) +
                        " from your " + accountType +
                        ".\nNew Balance: P" + String.format("%.2f", newBalance));

            } else {
                showAlert(Alert.AlertType.ERROR, "Account Error",
                        "⚠️ Could not find your account details.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount",
                    "Please enter a valid numeric amount (e.g., 200.00).");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    /** ✅ Return to Dashboard */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load Dashboard.");
        }
    }

    /** ✅ Helper alert dialog */
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
