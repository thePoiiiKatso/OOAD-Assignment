package bankingsystem.controllers;

import bankingsystem.model.TransactionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class WithdrawController {

    @FXML
    private TextField txtWithdrawAmount;

    @FXML
    private TextField txtAvailableBalance;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnBack;

    private double balance = 1000.00;
    private final String accountType = "Cheque Account";
    private final String customerName = "Katso";

    @FXML
    public void initialize() {
        txtAvailableBalance.setText("P" + String.format("%.2f", balance));
    }

    @FXML
    private void handleWithdraw(ActionEvent event) {
        try {
            String amountText = txtWithdrawAmount.getText().trim();
            if (amountText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Empty Field", "Please enter a withdrawal amount.");
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Please enter a valid withdrawal amount.");
                return;
            }

            if (amount > balance) {
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "You don’t have enough balance to withdraw P" + amount);
                return;
            }

            // Deduct and update balance
            balance -= amount;
            txtAvailableBalance.setText("P" + String.format("%.2f", balance));

            // Add transaction record
            TransactionManager.addTransaction(customerName, accountType, amount, balance);

            // Success alert
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Withdrawal Successful");
            success.setHeaderText(null);
            success.setContentText(
                "You have withdrawn P" + amount +
                "\nNew Balance: P" + String.format("%.2f", balance)
            );
            success.showAndWait();

            // Clear input after confirmation
            txtWithdrawAmount.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a numeric withdrawal amount.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
