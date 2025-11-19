package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import bankingsystem.util.SessionManager;
import bankingsystem.dao.CustomerDAO.CustomerRecord;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for Deposit.fxml.
 * Handles deposits and records transactions in the embedded H2 database.
 * Automatically fills account info for the logged-in customer (if available).
 */
public class DepositController {

    @FXML private TextField txtAccountNumber;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<String> cmbAccountType;
    @FXML private Label lblFeedback;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    private void initialize() {
        // Load possible account types
        if (cmbAccountType != null && cmbAccountType.getItems().isEmpty()) {
            cmbAccountType.getItems().addAll("Savings", "Investors", "Cheque");
        }

        // ✅ Auto-populate if logged-in customer exists
        CustomerRecord customer = SessionManager.getCurrentCustomer();
        if (customer != null) {
            // If customer has an account, fetch it
            accountDAO.findAll().stream()
                    .filter(a -> a.getCustomerID() == customer.getId())
                    .findFirst()
                    .ifPresent(acc -> {
                        txtAccountNumber.setText(String.valueOf(acc.getAccountNumber()));
                        cmbAccountType.setValue(acc.getAccountType());
                    });
        }
    }

    /**
     * Handles Deposit button click.
     */
    @FXML
    private void handleDeposit() {
        try {
            // --- Validate input ---
            String accText = txtAccountNumber.getText();
            String amtText = txtAmount.getText();
            String type = cmbAccountType.getValue();

            if (accText == null || accText.isBlank()) {
                lblFeedback.setText("Please enter an account number.");
                return;
            }
            if (amtText == null || amtText.isBlank()) {
                lblFeedback.setText("Please enter an amount to deposit.");
                return;
            }
            if (type == null || type.isBlank()) {
                lblFeedback.setText("Please select an account type.");
                return;
            }

            int accNo = Integer.parseInt(accText.trim());
            double amount = Double.parseDouble(amtText.trim());

            if (amount <= 0) {
                lblFeedback.setText("Deposit amount must be greater than zero.");
                return;
            }

            // --- Fetch account ---
            BankAccount account = accountDAO.findById(accNo);
            if (account == null) {
                lblFeedback.setText("Account not found.");
                return;
            }

            // --- Perform deposit ---
            account.deposit(amount);
            accountDAO.save(account); // persist new balance
            transactionDAO.record(accNo, "Deposit", amount,
                    "Customer deposit into " + type + " account");

            lblFeedback.setText(String.format("✅ Successfully deposited P%.2f into account #%d", amount, accNo));

            // --- Clear form ---
            txtAmount.clear();

        } catch (NumberFormatException e) {
            lblFeedback.setText("Invalid number format. Please check inputs.");
        } catch (Exception e) {
            e.printStackTrace();
            lblFeedback.setText("Error during deposit: " + e.getMessage());
        }
    }

    /**
     * Navigate back to the Dashboard.
     */
    @FXML
    private void handleBack() {
        Navigator.goTo("/Dashboard.fxml", 873, 700);
    }
}
