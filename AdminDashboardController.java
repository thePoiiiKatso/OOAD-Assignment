package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblPendingApprovals;
    @FXML private Label lblTotalAccounts;   // FIXED: matches FXML ID

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    private void initialize() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        try {
            int totalCustomers = accountDAO.countCustomers();
            int pending = accountDAO.countPending();
            int totalAccounts = accountDAO.countAll();   // FIXED: count all accounts

            lblTotalCustomers.setText(String.valueOf(totalCustomers));
            lblPendingApprovals.setText(String.valueOf(pending));
            lblTotalAccounts.setText(String.valueOf(totalAccounts));  // FIXED binding

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleManageApprovals() {
        Navigator.goTo("AdminApproval.fxml", 900, 600);
    }

    @FXML
    private void handleViewCustomers() {
        Navigator.goTo("CustomerManagement.fxml", 1100, 700);
    }

    @FXML
    private void handleRegisterCustomer() {
        Navigator.goTo("RegisterCustomer.fxml", 900, 600);
    }

    @FXML
    private void handleTransactions() {
        Navigator.goTo("TransactionHistory.fxml", 1100, 700);
    }

    @FXML
    private void handleLogout() {
        Navigator.goTo("Login.fxml", 600, 400);
    }
}
