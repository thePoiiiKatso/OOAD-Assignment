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

/**
 * Controller for Dashboard.fxml.
 * Displays logged-in customer details (name, balance, account type)
 * and handles navigation for customer actions.
 */
public class DashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label txtBalance;
    @FXML private Label txtAccountType;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    /** The logged-in customer's ID (injected automatically via Navigator). */
    private int customerId;

    // ---------------------------------------------------------------
    // INITIALIZATION
    // ---------------------------------------------------------------
    @FXML
    private void initialize() {
        // If Navigator injected session automatically, load customer data
        if (customerId != 0 || SessionManager.getCurrentCustomer() != null) {
            loadCustomerData();
        } else {
            lblWelcome.setText("Session expired. Please log in again.");
            txtAccountType.setText("Account Type: N/A");
            txtBalance.setText("P 0.00");
        }
    }

    /** Called automatically via Navigator’s injectSessionIfAvailable() */
    public void setCustomerId(int id) {
        this.customerId = id;
        loadCustomerData();
    }

    /** Called automatically via Navigator’s injectSessionIfAvailable() */
    public void setCustomer(CustomerRecord customer) {
        if (customer != null) {
            this.customerId = customer.getId();
            SessionManager.setCurrentCustomer(customer);
            loadCustomerData();
        }
    }

    /** Optional manual setter (still supported). */
    public void setWelcomeInfo(String name, String accountType, double balance) {
        lblWelcome.setText("Welcome, " + name + "!");
        txtBalance.setText("P " + String.format("%.2f", balance));
        txtAccountType.setText("Account Type: " + accountType);
    }

    // ---------------------------------------------------------------
    // LOAD CUSTOMER DATA
    // ---------------------------------------------------------------
    private void loadCustomerData() {
        try {
            CustomerRecord customer = SessionManager.getCurrentCustomer();

            // Reload from DB if missing
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

            final CustomerRecord current = customer;
            String name = current.getName();

            List<BankAccount> accounts = accountDAO.findAll().stream()
                    .filter(a -> a.getCustomerID() == current.getId())
                    .collect(Collectors.toList());

            double totalBalance = accounts.stream()
                    .mapToDouble(BankAccount::getBalance)
                    .sum();

            String accountTypes = accounts.isEmpty()
                    ? "No accounts"
                    : accounts.stream()
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

    // ---------------------------------------------------------------
    // NAVIGATION BUTTONS
    // ---------------------------------------------------------------
    @FXML private void handleViewAccounts() {
        Navigator.goTo("/ViewAccounts.fxml", 873, 700);
    }

    @FXML private void handleMakePayments() {
        Navigator.goTo("/Payments.fxml", 873, 700);
    }

    @FXML private void handleWithdraw() {
        Navigator.goTo("/Withdraw.fxml", 873, 700);
    }

    @FXML private void handleDeposit() {
        Navigator.goTo("/Deposit.fxml", 873, 700);
    }

    /** Opens ViewTransactions page and passes customer session automatically */
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

    @FXML private void handleHelpDesk() {
        Navigator.goTo("/OpenAccount.fxml", 873, 700);
    }

    @FXML
    private void handleLogoutCustomer() {
        SessionManager.clear();
        Navigator.goTo("/Login.fxml", 800, 600);
    }

    // ---------------------------------------------------------------
    // QUICK ACTION BUTTONS
    // ---------------------------------------------------------------
    @FXML private void handleFastDeposit() {
        Navigator.goTo("/Deposit.fxml", 873, 700);
    }

    @FXML private void handleQuickTransfer() {
        Navigator.goTo("/ViewAccounts.fxml", 873, 700);
    }

    @FXML private void handlePayBills() {
        Navigator.goTo("/Payments.fxml", 873, 700);
    }
}
