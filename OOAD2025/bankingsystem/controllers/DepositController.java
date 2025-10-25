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
 * Handles deposit operations for Lekgwere Banking System.
 * Verifies PIN, updates balance, records transaction, and updates dashboard session.
 */
public class DepositController {

    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtDepositAmount;
    @FXML private TextField txtAvailableBalance;
    @FXML private PasswordField txtPin;
    @FXML private Button btnConfirm;

    @FXML
    private void initialize() {
        cmbAccountType.getItems().addAll("Savings Account", "Cheque Account", "Investors Account");
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        String accountType = cmbAccountType.getValue();
        String amountStr = txtDepositAmount.getText();
        String pin = txtPin.getText();

        if (accountType == null || amountStr.isEmpty() || pin.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Amount must be greater than zero.");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to the database.");
                return;
            }

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
                    showAlert(Alert.AlertType.ERROR, "Invalid PIN", "Incorrect PIN entered.");
                    conn.close();
                    return;
                }

                double newBalance = balance + amount;

                // ✅ Update account balance
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE accounts SET balance = ? WHERE account_id = ?");
                update.setDouble(1, newBalance);
                update.setInt(2, accountId);
                update.executeUpdate();

                // ✅ Record transaction via TransactionService
                TransactionService.recordTransaction(
                        Session.currentUserName,
                        "Deposit",
                        "Cash Deposit into " + accountType,
                        amount,
                        null, accountId
                );

                conn.close();

                // ✅ Update session balance
                Session.currentBalance = newBalance;
                txtAvailableBalance.setText("P" + String.format("%.2f", newBalance));

                showAlert(Alert.AlertType.INFORMATION, "✅ Deposit Successful",
                        "You deposited P" + amount + " into your " + accountType +
                        ".\nNew Balance: P" + String.format("%.2f", newBalance));

                // 🔄 Auto-refresh Dashboard when user goes back
                goToDashboard(event);

            } else {
                showAlert(Alert.AlertType.ERROR, "Account Error", "Account not found.");
            }

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

    // ==================== NAVIGATION ====================
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

    // ==================== ALERTS ====================
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
