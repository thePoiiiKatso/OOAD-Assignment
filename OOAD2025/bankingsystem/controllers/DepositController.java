package bankingsystem.controllers;

import bankingsystem.model.TransactionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class DepositController {

    @FXML
    private TextField txtDepositAmount;

    @FXML
    private TextField txtAvailableBalance;

    private double balance = 1000.00;
    private final String accountType = "Cheque Account";
    private final String customerName = "Katso";

    @FXML
    public void initialize() {
        txtAvailableBalance.setText("P" + String.format("%.2f", balance));
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txtDepositAmount.getText().trim());
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Invalid Amount", "Please enter a valid deposit amount.");
                return;
            }

            balance += amount;
            txtAvailableBalance.setText("P" + String.format("%.2f", balance));

            TransactionManager.addTransaction(customerName, accountType, amount, balance);

            showAlert(Alert.AlertType.INFORMATION, "Deposit Successful",
                    "You deposited P" + amount + "\nNew balance: P" + balance);

            txtDepositAmount.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a numeric deposit amount.");
        }
    }

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
