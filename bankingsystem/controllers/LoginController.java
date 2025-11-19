package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.CustomerDAO;
import bankingsystem.dao.CustomerDAO.CustomerRecord;
import bankingsystem.util.Navigator;
import bankingsystem.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Handles login authentication for Admins and Customers.
 * Validates users from embedded H2 database and stores session data.
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // --- Input validation ---
        if (username == null || username.isBlank() ||
            password == null || password.isBlank()) {
            showAlert("Missing Fields", "Please enter both username and password.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // --- Admin login (static credentials) ---
            if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
                Navigator.goTo("AdminDashboard.fxml", 1100, 700);
                return;
            }

            // --- Customer login ---
            CustomerRecord customer = customerDAO.findByUsernameAndPassword(username, password);

            if (customer == null) {
                showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
                return;
            }

            // --- Store session ---
            SessionManager.setCurrentCustomer(customer);

            // --- Fetch total balance and first account type for display ---
            int customerId = customer.getId();
            var accounts = accountDAO.findAllDetailed().stream()
                    .filter(a -> a.getCustomerId() == customerId)
                    .toList();

            double balance = accounts.stream().mapToDouble(a -> a.getBalance()).sum();
            String acctType = accounts.stream().map(a -> a.getType()).findFirst().orElse("N/A");

            // --- Debug feedback ---
            System.out.println("✅ Login successful for user: " + username +
                    " | Account Type: " + acctType + " | Balance: " + balance);

            // --- Navigate to customer dashboard ---
            Navigator.goTo("Dashboard.fxml", 1100, 700);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Login failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Utility for showing alert dialogs.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
