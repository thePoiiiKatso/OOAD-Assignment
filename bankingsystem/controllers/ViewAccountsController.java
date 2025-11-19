package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import bankingsystem.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for ViewAccounts.fxml.
 * Displays account balances and enables transfers between accounts.
 */
public class ViewAccountsController {

    @FXML private Label lblSavingsBalance;
    @FXML private Label lblInvestorsBalance;
    @FXML private Label lblChequeBalance;
    @FXML private Label lblTotalBalance;
    @FXML private ComboBox<String> comboAccountFrom;
    @FXML private ComboBox<String> comboAccountTo;
    @FXML private TextField txtTransferAmount;
    @FXML private Label lblFromBalance;
    @FXML private Label lblFeedback;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private List<BankAccount> allAccounts;

    private int customerId;

    // --------------------------------------------------------------
    // Initialization
    // --------------------------------------------------------------
    @FXML
    private void initialize() {

        comboAccountFrom.setOnAction(e -> updateFromBalance());

        // Load session user
        var sessionUser = SessionManager.getCurrentCustomer();
        if (sessionUser != null) {
            this.customerId = sessionUser.getId();
            loadAccounts();
        } else {
            lblFeedback.setText("⚠ Session expired. Please log in again.");
            disableInputs();
        }
    }

    public void setCustomerId(int id) {
        this.customerId = id;
        loadAccounts();
    }

    // --------------------------------------------------------------
    // Load Accounts (Always reload fresh DB values)
    // --------------------------------------------------------------
    private void loadAccounts() {
        try {
            allAccounts = accountDAO.findAll().stream()
                    .filter(a -> a.getCustomerID() == customerId)
                    .collect(Collectors.toList());

            if (allAccounts.isEmpty()) {
                lblFeedback.setText("⚠ No accounts found for this customer.");
                disableInputs();
                return;
            }

            enableInputs();
            updateBalances(); // update left panel

            var accountLabels = allAccounts.stream()
                    .map(a -> a.getAccountNumber() + " - " + a.getAccountType())
                    .collect(Collectors.toList());

            comboAccountFrom.setItems(FXCollections.observableArrayList(accountLabels));
            comboAccountTo.setItems(FXCollections.observableArrayList(accountLabels));

            lblFeedback.setText("Select accounts to transfer between.");

        } catch (Exception e) {
            lblFeedback.setText("❌ Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void disableInputs() {
        comboAccountFrom.setDisable(true);
        comboAccountTo.setDisable(true);
        txtTransferAmount.setDisable(true);
    }

    private void enableInputs() {
        comboAccountFrom.setDisable(false);
        comboAccountTo.setDisable(false);
        txtTransferAmount.setDisable(false);
    }

    // --------------------------------------------------------------
    // Updated Balances – ALWAYS reload from DB
    // --------------------------------------------------------------
    private void updateBalances() {

        // Always read live data from database
        allAccounts = accountDAO.findAll().stream()
                .filter(a -> a.getCustomerID() == customerId)
                .collect(Collectors.toList());

        double savings = 0, investors = 0, cheque = 0, total = 0;

        for (BankAccount acc : allAccounts) {
            double bal = acc.getBalance();
            total += bal;

            String type = acc.getAccountType().toLowerCase();
            if (type.contains("savings")) savings += bal;
            else if (type.contains("invest")) investors += bal;
            else if (type.contains("cheque")) cheque += bal;
        }

        lblSavingsBalance.setText("P " + String.format("%.2f", savings));
        lblInvestorsBalance.setText("P " + String.format("%.2f", investors));
        lblChequeBalance.setText("P " + String.format("%.2f", cheque));
        lblTotalBalance.setText("P " + String.format("%.2f", total));
    }

    // --------------------------------------------------------------
    // From account balance update
    // --------------------------------------------------------------
    private void updateFromBalance() {
        try {
            String sel = comboAccountFrom.getValue();
            if (sel == null) return;

            int accNo = Integer.parseInt(sel.split(" - ")[0]);
            BankAccount acc = accountDAO.findById(accNo);

            if (acc != null)
                lblFromBalance.setText("Balance: P " + String.format("%.2f", acc.getBalance()));
            else
                lblFromBalance.setText("Account not found.");

        } catch (Exception e) {
            lblFeedback.setText("❌ Error updating balance: " + e.getMessage());
        }
    }

    // --------------------------------------------------------------
    // Handle Transfer
    // --------------------------------------------------------------
    @FXML
    private void handleTransfer(ActionEvent event) {
        try {
            String fromStr = comboAccountFrom.getValue();
            String toStr = comboAccountTo.getValue();

            if (fromStr == null || toStr == null) {
                lblFeedback.setText("⚠ Please select both accounts.");
                return;
            }

            if (fromStr.equals(toStr)) {
                lblFeedback.setText("⚠ Cannot transfer to the same account.");
                return;
            }

            double amount = Double.parseDouble(txtTransferAmount.getText().trim());
            if (amount <= 0) {
                lblFeedback.setText("⚠ Enter a valid amount.");
                return;
            }

            int fromAccNo = Integer.parseInt(fromStr.split(" - ")[0]);
            int toAccNo = Integer.parseInt(toStr.split(" - ")[0]);

            BankAccount from = accountDAO.findById(fromAccNo);
            BankAccount to = accountDAO.findById(toAccNo);

            if (from == null || to == null) {
                lblFeedback.setText("❌ Account not found.");
                return;
            }

            if (from.getCustomerID() != customerId || to.getCustomerID() != customerId) {
                lblFeedback.setText("❌ Unauthorized transfer.");
                return;
            }

            if (from.withdraw(amount)) {

                to.deposit(amount);

                accountDAO.save(from);
                accountDAO.save(to);

                transactionDAO.record(fromAccNo, "TransferOut", amount,
                        "Transferred to account " + toAccNo);
                transactionDAO.record(toAccNo, "TransferIn", amount,
                        "Received from account " + fromAccNo);

                lblFeedback.setText("✅ Transferred P" + String.format("%.2f", amount));
                txtTransferAmount.clear();

                // Refresh UI instantly
                loadAccounts();
                updateFromBalance();

            } else {
                lblFeedback.setText("❌ Insufficient funds.");
            }

        } catch (NumberFormatException e) {
            lblFeedback.setText("⚠ Invalid number format.");
        } catch (Exception e) {
            lblFeedback.setText("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------
    // Navigation
    // --------------------------------------------------------------
    @FXML
    private void handleBack(ActionEvent event) {
        Navigator.goTo("/Dashboard.fxml", 873, 700);
    }
}
