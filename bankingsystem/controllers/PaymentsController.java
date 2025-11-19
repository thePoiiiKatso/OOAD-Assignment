package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PaymentsController {

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private int customerId;

    // ---------- GENERAL BILL PAYMENT ----------
    @FXML private ComboBox<String> cmbPayFrom;
    @FXML private ComboBox<String> cmbPaymentType;
    @FXML private TextField txtReference;
    @FXML private TextField txtAmount;
    @FXML private Label lblPaymentFeedback;

    // ---------- AIRTIME PURCHASE ----------
    @FXML private TextField txtAirtimePhone;
    @FXML private ComboBox<String> cmbAirtimeProvider;
    @FXML private TextField txtAirtimeAmount;
    @FXML private Label lblAirtimeFeedback;

    @FXML
    public void initialize() {

        // Fill Payment Types
        if (cmbPaymentType != null) {
            cmbPaymentType.setItems(FXCollections.observableArrayList(
                    "Electricity (BPC)",
                    "Water Utilities",
                    "Mobile Airtime",
                    "Internet Service",
                    "TV Subscription (DSTV)"
            ));
        }

        // Fill Airtime Providers
        if (cmbAirtimeProvider != null) {
            cmbAirtimeProvider.setItems(FXCollections.observableArrayList(
                    "Mascom",
                    "Orange Botswana",
                    "BTC Mobile"
            ));
        }
    }

    /** Set the customer ID passed from dashboard */
    public void setCustomerId(int id) {
        this.customerId = id;

        // Load accounts for customer
        if (cmbPayFrom != null) {
            cmbPayFrom.setItems(FXCollections.observableArrayList(
                    accountDAO.findByCustomerId(customerId).stream()
                            .map(acc -> acc.getAccountNumber() + " (" + acc.getAccountType() + ")")
                            .toList()
            ));
        }
    }

    // ============================================================
    //                     GENERAL PAYMENT
    // ============================================================
    @FXML
    private void handleGeneralPayment() {
        try {
            String accSelection = cmbPayFrom.getValue();
            String type = cmbPaymentType.getValue();
            String ref = txtReference.getText().trim();
            String amountStr = txtAmount.getText().trim();

            // ---- VALIDATION ----
            if (accSelection == null || type == null || ref.isEmpty() || amountStr.isEmpty()) {
                alertWarning("Missing Information", "Please complete all fields before payment.");
                return;
            }

            // Reference must be 11 digits
            if (!ref.matches("\\d{11}")) {
                alertWarning("Invalid Reference", "Reference number must be exactly 11 digits.");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                alertWarning("Invalid Amount", "Amount must be greater than zero.");
                return;
            }

            // Extract account number
            int accNumber = Integer.parseInt(accSelection.split(" ")[0]);

            BankAccount acc = accountDAO.findById(accNumber);
            if (acc == null) {
                alertWarning("Account Error", "Selected account not found.");
                return;
            }

            // ---- WITHDRAW ----
            if (!acc.withdraw(amount)) {
                alertWarning("Insufficient Funds",
                        "You do not have enough balance for this payment.");
                return;
            }

            // Save new balance
            accountDAO.save(acc);

            // Record transaction
            transactionDAO.record(
                    acc.getAccountNumber(),
                    type,
                    amount,
                    "Reference: " + ref
            );

            // ---- SUCCESS MESSAGES ----
            if (type.equals("Electricity (BPC)")) {
                alertSuccess(
                        "Electricity Payment Successful",
                        "Electricity voucher purchased for meter: " + ref +
                        ".\nYou will shortly receive an SMS with your token."
                );
            } else {
                alertSuccess(
                        "Payment Successful",
                        "Successfully paid P" + amount + " for " + type + "."
                );
            }

            lblPaymentFeedback.setText("✔ Payment completed.");
            clearGeneralPaymentFields();

        } catch (Exception e) {
            alertError("Payment Error", e.getMessage());
        }
    }

    private void clearGeneralPaymentFields() {
        txtReference.clear();
        txtAmount.clear();
        cmbPaymentType.getSelectionModel().clearSelection();
        lblPaymentFeedback.setText("");
    }


    // ============================================================
    //                    AIRTIME PURCHASE
    // ============================================================
    @FXML
    private void handleAirtimePurchase() {
        try {
            String phone = txtAirtimePhone.getText().trim();
            String provider = cmbAirtimeProvider.getValue();
            String amountStr = txtAirtimeAmount.getText().trim();

            // ---- VALIDATION ----
            if (phone.isEmpty() || provider == null || amountStr.isEmpty()) {
                alertWarning("Missing Fields", "Please complete all Airtime fields.");
                return;
            }

            // Phone must be 8 digits
            if (!phone.matches("\\d{8}")) {
                alertWarning("Invalid Phone Number",
                        "Phone number must be exactly 8 digits.");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                alertWarning("Invalid Amount", "Amount must be greater than zero.");
                return;
            }

            // Get customer's first account
            BankAccount acc = getFirstAccountForCustomer();
            if (acc == null) {
                alertWarning("Account Error", "Your account could not be found.");
                return;
            }

            if (!acc.withdraw(amount)) {
                alertWarning("Insufficient Funds", "Not enough balance.");
                return;
            }

            accountDAO.save(acc);

            // Record transaction
            transactionDAO.record(
                    acc.getAccountNumber(),
                    "Airtime Purchase - " + provider,
                    amount,
                    "Phone: +267" + phone
            );

            // ---- SUCCESS MESSAGE ----
            alertSuccess(
                    "Airtime Purchase Successful",
                    "Airtime P" + amount + " bought for " + phone +
                    ".\nAIRTIME SUCCESSFULLY RECHARGED."
            );

            lblAirtimeFeedback.setText("✔ Airtime recharged.");
            clearAirtimeFields();

        } catch (Exception e) {
            alertError("Airtime Error", e.getMessage());
        }
    }

    private void clearAirtimeFields() {
        txtAirtimePhone.clear();
        txtAirtimeAmount.clear();
        cmbAirtimeProvider.getSelectionModel().clearSelection();
        lblAirtimeFeedback.setText("");
    }


    // ============================================================
    //                       NAVIGATION
    // ============================================================
    @FXML
    private void handleBack() {
        Navigator.goToWithController("/Dashboard.fxml", 900, 700, c -> {
            if (c instanceof DashboardController dash) {
                dash.setCustomerId(customerId);
            }
        });
    }


    // ============================================================
    //                        HELPERS
    // ============================================================
    private BankAccount getFirstAccountForCustomer() {
        return accountDAO.findByCustomerId(customerId).stream()
                .findFirst()
                .orElse(null);
    }

    // ---- Alert Helpers ----
    private void alertWarning(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertSuccess(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
