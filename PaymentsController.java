package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for managing all payment-related operations:
 * Electricity bills, Airtime purchases, and Water (Metsi) payments.
 */
public class PaymentsController {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Logged-in customer ID
    private int customerId;

    // Electricity section
    @FXML private ComboBox<String> cmbElectricity;
    @FXML private TextField txtMeterNumber;
    @FXML private TextField txtElectricityAmount;

    // Airtime section
    @FXML private ComboBox<String> cmbAirtime;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtAirtimeAmount;

    // Water (Metsi) section
    @FXML private ComboBox<String> cmbMetsi;
    @FXML private TextField txtMetsiAccountNumber;
    @FXML private TextField txtMetsiAmount;

    // Feedback label
    @FXML private Label lblFeedback;

    /** Called automatically when the FXML loads */
    @FXML
    private void initialize() {
        if (cmbElectricity != null) {
            cmbElectricity.setItems(FXCollections.observableArrayList(
                    "Botswana Power Corporation (BPC)",
                    "Ideal Prepaid"
            ));
        }

        if (cmbAirtime != null) {
            cmbAirtime.setItems(FXCollections.observableArrayList(
                    "Mascom",
                    "BTC Mobile",
                    "Orange Botswana"
            ));
        }

        if (cmbMetsi != null) {
            cmbMetsi.setItems(FXCollections.observableArrayList(
                    "Water Utilities Corporation"
            ));
        }
    }

    /** Receives customer ID from Dashboard */
    public void setCustomerId(int id) {
        this.customerId = id;
    }

    // ---------------- ELECTRICITY PAYMENT ----------------
    @FXML
    private void handleElectricityPayment() {
        try {
            String provider = cmbElectricity.getValue();
            String meter = txtMeterNumber.getText();
            double amount = parseAmount(txtElectricityAmount.getText());

            if (provider == null || meter.isBlank()) {
                lblFeedback.setText("Please complete all fields for Electricity Payment.");
                return;
            }

            BankAccount sender = getFirstAccountForCustomer();
            if (sender == null) {
                lblFeedback.setText("Your account could not be found.");
                return;
            }

            if (sender.withdraw(amount)) {
                accountDAO.save(sender);
                transactionDAO.record(sender.getAccountNumber(),
                        "Electricity Bill - " + provider, amount,
                        "Meter: " + meter);
                success("Paid P" + amount + " to " + provider + " (Meter: " + meter + ")");
            } else {
                lblFeedback.setText("Insufficient funds.");
            }

        } catch (NumberFormatException e) {
            lblFeedback.setText("Enter a valid amount.");
        } catch (Exception e) {
            lblFeedback.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- AIRTIME PURCHASE ----------------
    @FXML
    private void handleAirtimePayment() {
        try {
            String network = cmbAirtime.getValue();
            String phone = txtPhoneNumber.getText();
            double amount = parseAmount(txtAirtimeAmount.getText());

            if (network == null || phone.isBlank()) {
                lblFeedback.setText("Please complete all fields for Airtime Purchase.");
                return;
            }

            BankAccount sender = getFirstAccountForCustomer();
            if (sender == null) {
                lblFeedback.setText("Your account could not be found.");
                return;
            }

            if (sender.withdraw(amount)) {
                accountDAO.save(sender);
                transactionDAO.record(sender.getAccountNumber(),
                        "Airtime Purchase - " + network, amount,
                        "Phone: " + phone);
                success("Bought P" + amount + " Airtime on " + network + " for " + phone);
            } else {
                lblFeedback.setText("Insufficient funds.");
            }

        } catch (NumberFormatException e) {
            lblFeedback.setText("Enter a valid amount.");
        } catch (Exception e) {
            lblFeedback.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- WATER BILL (METSI) ----------------
    @FXML
    private void handleMetsiPayments() {
        try {
            String provider = cmbMetsi.getValue();
            String account = txtMetsiAccountNumber.getText();
            double amount = parseAmount(txtMetsiAmount.getText());

            if (provider == null || account.isBlank()) {
                lblFeedback.setText("Please complete all fields for Water Bill Payment.");
                return;
            }

            BankAccount sender = getFirstAccountForCustomer();
            if (sender == null) {
                lblFeedback.setText("Your account could not be found.");
                return;
            }

            if (sender.withdraw(amount)) {
                accountDAO.save(sender);
                transactionDAO.record(sender.getAccountNumber(),
                        "Water Bill - " + provider, amount,
                        "Account: " + account);
                success("Paid P" + amount + " Water Bill to " + provider + " (Account: " + account + ")");
            } else {
                lblFeedback.setText("Insufficient funds.");
            }

        } catch (NumberFormatException e) {
            lblFeedback.setText("Enter a valid amount.");
        } catch (Exception e) {
            lblFeedback.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------- NAVIGATION ----------------
    @FXML
    private void handleBack() {
        Navigator.goToWithController("/Dashboard.fxml", 900, 700, controller -> {
            if (controller instanceof DashboardController dash) {
                dash.setCustomerId(customerId);
            }
        });
    }

    // ---------------- HELPERS ----------------
    private double parseAmount(String amountStr) {
        if (amountStr == null || amountStr.isBlank())
            throw new NumberFormatException();
        return Double.parseDouble(amountStr);
    }

    private void success(String message) {
        lblFeedback.setText("✅ " + message);
        showAlert("Payment Successful", message);
        clearInputs();
    }

    private void clearInputs() {
        txtElectricityAmount.clear();
        txtMeterNumber.clear();
        txtPhoneNumber.clear();
        txtAirtimeAmount.clear();
        txtMetsiAccountNumber.clear();
        txtMetsiAmount.clear();

        if (cmbElectricity != null) cmbElectricity.getSelectionModel().clearSelection();
        if (cmbAirtime != null) cmbAirtime.getSelectionModel().clearSelection();
        if (cmbMetsi != null) cmbMetsi.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Confirmation");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Retrieves the first active account for this customer */
    private BankAccount getFirstAccountForCustomer() {
        return accountDAO.findAll().stream()
                .filter(a -> a.getCustomerID() == customerId)
                .findFirst()
                .orElse(null);
    }
}
