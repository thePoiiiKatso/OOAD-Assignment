package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import bankingsystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for Withdraw.fxml
 * Handles secure withdrawals with transaction recording.
 */
public class WithdrawController {

    // --- DAOs ---
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // --- FXML UI Components ---
    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtAvailableBalance;
    @FXML private TextField txtWithdrawAmount;
    @FXML private TextField txtMobileNumber;
    @FXML private Label lblFeedback;

    // --- State ---
    private int customerId;
    private List<BankAccount> customerAccounts;
    private BankAccount currentAccount;

    // --- Initialization ---
    @FXML
    private void initialize() {
        cmbAccountType.setItems(FXCollections.observableArrayList("Savings", "Cheque", "Investors"));
        cmbAccountType.setOnAction(e -> handleAccountSelection());

        // Load session user
        var sessionUser = SessionManager.getCurrentCustomer();
        if (sessionUser != null) {
            this.customerId = sessionUser.getId();
            loadCustomerAccounts();
        } else {
            lblFeedback.setText("⚠ Session expired. Please log in again.");
            disableInputs();
        }
    }

    /** If called manually by Navigator session injection */
    public void setCustomerId(int id) {
        this.customerId = id;
        loadCustomerAccounts();
    }

    // ------------------------------------------------------------------
    // Account Loading
    // ------------------------------------------------------------------
    private void loadCustomerAccounts() {
        try {
            customerAccounts = accountDAO.findAll().stream()
                    .filter(a -> a.getCustomerID() == customerId)
                    .toList();

            if (customerAccounts.isEmpty()) {
                lblFeedback.setText("⚠ No accounts found for this user.");
                disableInputs();
                return;
            }

            lblFeedback.setText("Select an account to withdraw from.");
            enableInputs();

        } catch (Exception e) {
            lblFeedback.setText("❌ Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void disableInputs() {
        cmbAccountType.setDisable(true);
        txtWithdrawAmount.setDisable(true);
        txtMobileNumber.setDisable(true);
    }

    private void enableInputs() {
        cmbAccountType.setDisable(false);
        txtWithdrawAmount.setDisable(false);
        txtMobileNumber.setDisable(false);
    }

    // ------------------------------------------------------------------
    // Account Selection
    // ------------------------------------------------------------------
    private void handleAccountSelection() {
        String selectedType = cmbAccountType.getValue();
        if (selectedType == null) return;

        currentAccount = customerAccounts.stream()
                .filter(a -> a.getAccountType().equalsIgnoreCase(selectedType))
                .findFirst()
                .orElse(null);

        if (currentAccount != null) {
            txtAvailableBalance.setText(String.format("P %.2f", currentAccount.getBalance()));

            // Disable withdrawal input for Savings account
            if (currentAccount.getAccountType().equalsIgnoreCase("Savings")) {
                lblFeedback.setText("ℹ Savings accounts cannot withdraw.");
                txtWithdrawAmount.setDisable(true);
            } else {
                txtWithdrawAmount.setDisable(false);
            }

        } else {
            txtAvailableBalance.setText("Account not found");
        }
    }

    // ------------------------------------------------------------------
    // Withdraw Logic
    // ------------------------------------------------------------------
    @FXML
    private void handleWithdraw() {
        if (currentAccount == null) {
            lblFeedback.setText("⚠ Please select an account first.");
            return;
        }

        // ❌ Block all withdrawals for Savings accounts
        if (currentAccount.getAccountType().equalsIgnoreCase("Savings")) {
            lblFeedback.setText("❌ Withdrawals are not allowed for Savings accounts.");
            return;
        }

        try {
            double amount = Double.parseDouble(txtWithdrawAmount.getText().trim());
            if (amount <= 0) {
                lblFeedback.setText("⚠ Enter a valid withdrawal amount.");
                return;
            }

            if (!currentAccount.withdraw(amount)) {
                lblFeedback.setText("❌ Insufficient funds.");
                return;
            }

            accountDAO.save(currentAccount);

            String mobile = txtMobileNumber.getText().isBlank()
                    ? "N/A"
                    : txtMobileNumber.getText().trim();

            transactionDAO.record(
                    currentAccount.getAccountNumber(),
                    "Withdrawal",
                    amount,
                    "Cash withdrawal (Mobile: " + mobile + ")"
            );

            lblFeedback.setText(String.format("✅ Successfully withdrew P%.2f", amount));
            txtAvailableBalance.setText(String.format("P %.2f", currentAccount.getBalance()));
            txtWithdrawAmount.clear();
            txtMobileNumber.clear();

        } catch (NumberFormatException e) {
            lblFeedback.setText("⚠ Invalid number format.");
        } catch (Exception e) {
            lblFeedback.setText("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------
    @FXML
    private void handleBack() {
        Navigator.goTo("/Dashboard.fxml", 873, 700);
    }
}
