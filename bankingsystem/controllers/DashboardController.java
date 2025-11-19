package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.CustomerDAO;
import bankingsystem.dao.CustomerDAO.CustomerRecord;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import bankingsystem.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label txtBalance;
    @FXML private Label txtAccountType;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    private int customerId;

    @FXML
    private void initialize() {
        if (customerId != 0 || SessionManager.getCurrentCustomer() != null) {
            loadCustomerData();
        } else {
            lblWelcome.setText("Session expired. Please log in again.");
            txtAccountType.setText("Account Type: N/A");
            txtBalance.setText("P 0.00");
        }
    }

    public void setCustomerId(int id) {
        this.customerId = id;
        loadCustomerData();
    }

    public void setCustomer(CustomerRecord customer) {
        if (customer != null) {
            this.customerId = customer.getId();
            SessionManager.setCurrentCustomer(customer);
            loadCustomerData();
        }
    }

    private void loadCustomerData() {
        try {
            CustomerRecord customer = SessionManager.getCurrentCustomer();

            if (customer == null && customerId > 0) {
                customer = customerDAO.findById(customerId);
                if (customer != null)
                    SessionManager.setCurrentCustomer(customer);
            }

            if (customer == null) {
                lblWelcome.setText("Customer not found.");
                txtBalance.setText("P 0.00");
                txtAccountType.setText("Account Type: N/A");
                return;
            }

            final CustomerRecord finalCustomer = customer;

            String name = finalCustomer.getName();

            List<BankAccount> allAccounts = accountDAO.findAll().stream()
                    .filter(a -> a.getCustomerID() == finalCustomer.getId())
                    .collect(Collectors.toList());

            List<BankAccount> activeAccounts = allAccounts.stream()
                    .filter(a -> "ACTIVE".equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());

            double totalBalance = activeAccounts.stream()
                    .mapToDouble(BankAccount::getBalance)
                    .sum();

            String accountTypes = activeAccounts.isEmpty()
                    ? "No active accounts"
                    : activeAccounts.stream()
                            .map(BankAccount::getAccountType)
                            .distinct()
                            .collect(Collectors.joining(", "));

            lblWelcome.setText("Welcome, " + name + "!");
            txtAccountType.setText("Account Type: " + accountTypes);
            txtBalance.setText("P " + String.format("%.2f", totalBalance));

        } catch (Exception e) {
            lblWelcome.setText("⚠ Error loading data.");
            e.printStackTrace();
        }
    }

    // NAVIGATION
    @FXML private void handleViewAccounts() { Navigator.goTo("/ViewAccounts.fxml", 873, 700); }
    @FXML private void handleMakePayments() { Navigator.goTo("/Payments.fxml", 873, 700); }
    @FXML private void handleWithdraw() { Navigator.goTo("/Withdraw.fxml", 873, 700); }
    @FXML private void handleDeposit() { Navigator.goTo("/Deposit.fxml", 873, 700); }

    @FXML
    private void handleViewTransactions() {
        Navigator.goToWithController("/ViewTransactions.fxml", 873, 700, controller -> {
            if (controller instanceof ViewTransactionsController view) {
                if (customerId != 0)
                    view.setCustomerId(customerId);
                view.loadCustomerTransactions();
            }
        });
    }

    @FXML private void handleHelpDesk() { Navigator.goTo("/OpenAccount.fxml", 873, 700); }

    @FXML
    private void handleLogoutCustomer() {
        SessionManager.clear();
        Navigator.goTo("/Login.fxml", 800, 600);
    }

    // QUICK BUTTONS
    @FXML private void handleFastDeposit() { Navigator.goTo("/Deposit.fxml", 873, 700); }
    @FXML private void handleQuickTransfer() { Navigator.goTo("/ViewAccounts.fxml", 873, 700); }
    @FXML private void handlePayBills() { Navigator.goTo("/Payments.fxml", 873, 700); }

    // ✅ SETTINGS BUTTON — NOW CORRECTLY INSIDE CLASS
    @FXML
    private void handleSettings() {
        Navigator.goTo("/Settings.fxml", 873, 700);
    }
}
