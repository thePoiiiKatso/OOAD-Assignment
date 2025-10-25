package bankingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * PaymentsController - Handles all customer payment actions (airtime, electricity, international, etc.)
 */
public class PaymentsController {

    @FXML private ComboBox<String> cmbAirtime;
    @FXML private ComboBox<String> cmbElectricity;
    @FXML private ComboBox<String> cmbMetsi;

    @FXML private TextField txtAirtimeAmount;
    @FXML private TextField txtAmount;
    @FXML private TextField txtElectricityAmount;
    @FXML private TextField txtIntlAccountNumber;
    @FXML private TextField txtIntlAmount;
    @FXML private TextField txtMeterNumber;
    @FXML private TextField txtMetsiAccountNumber;
    @FXML private TextField txtMetsiAmount;
    @FXML private TextField txtPhoneNumber;
    @FXML private PasswordField txtPin;
    @FXML private TextField txtRecipient;

    // ✅ Initialization: populate combo boxes when the scene loads
    @FXML
    public void initialize() {
        cmbAirtime.getItems().addAll("Mascom", "Orange", "BTC");
        cmbElectricity.getItems().addAll("Botswana Power Corporation (BPC)");
        cmbMetsi.getItems().addAll("Water Utilities Corporation");
    }

    // =================== PAYMENT HANDLERS =================== //

    @FXML
    void handleSendPayment(ActionEvent event) {
        String recipient = txtRecipient.getText().trim();
        String amountText = txtAmount.getText().trim();
        String pin = txtPin.getText().trim();

        if (recipient.isEmpty() || amountText.isEmpty() || pin.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill all Local Transfer fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment of P" + amount + " sent to " + recipient + ".");
            clearLocalTransfer();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Amount must be a valid number greater than zero.");
        }
    }

    @FXML
    void handleInternationalTransfer(ActionEvent event) {
        String intlAcc = txtIntlAccountNumber.getText().trim();
        String amountText = txtIntlAmount.getText().trim();

        if (intlAcc.isEmpty() || amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Details", "Please fill in all international transfer fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            showAlert(Alert.AlertType.INFORMATION, "Transfer Complete", "International transfer of P" + amount + " sent to account " + intlAcc + ".");
            clearInternational();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid number.");
        }
    }

    @FXML
    void handleElectricityPayment(ActionEvent event) {
        String provider = cmbElectricity.getValue();
        String meter = txtMeterNumber.getText().trim();
        String amountText = txtElectricityAmount.getText().trim();

        if (provider == null || meter.isEmpty() || amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Details", "Please select provider and fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "Electricity bill of P" + amount + " paid to " + provider + ".");
            clearElectricity();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
        }
    }

    @FXML
    void handleAirtimePayment(ActionEvent event) {
        String provider = cmbAirtime.getValue();
        String phone = txtPhoneNumber.getText().trim();
        String amountText = txtAirtimeAmount.getText().trim();

        if (provider == null || phone.isEmpty() || amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Details", "Please select provider and fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            showAlert(Alert.AlertType.INFORMATION, "Airtime Purchase", "P" + amount + " airtime sent to " + phone + " via " + provider + ".");
            clearAirtime();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric amount.");
        }
    }

    @FXML
    void handleMetsiPayments(ActionEvent event) {
        String provider = cmbMetsi.getValue();
        String account = txtMetsiAccountNumber.getText().trim();
        String amountText = txtMetsiAmount.getText().trim();

        if (provider == null || account.isEmpty() || amountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Details", "Please select provider and fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            showAlert(Alert.AlertType.INFORMATION, "Metsi Payment", "Water bill of P" + amount + " paid to " + provider + ".");
            clearMetsi();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid numeric amount.");
        }
    }

    // =================== BACK BUTTON =================== //

    @FXML
    void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to dashboard.");
            e.printStackTrace();
        }
    }

    // =================== HELPER METHODS =================== //

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearLocalTransfer() {
        txtRecipient.clear();
        txtAmount.clear();
        txtPin.clear();
    }

    private void clearInternational() {
        txtIntlAccountNumber.clear();
        txtIntlAmount.clear();
    }

    private void clearElectricity() {
        cmbElectricity.getSelectionModel().clearSelection();
        txtMeterNumber.clear();
        txtElectricityAmount.clear();
    }

    private void clearAirtime() {
        cmbAirtime.getSelectionModel().clearSelection();
        txtPhoneNumber.clear();
        txtAirtimeAmount.clear();
    }

    private void clearMetsi() {
        cmbMetsi.getSelectionModel().clearSelection();
        txtMetsiAccountNumber.clear();
        txtMetsiAmount.clear();
    }
}
