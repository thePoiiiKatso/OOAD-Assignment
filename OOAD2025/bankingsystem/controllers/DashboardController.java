package bankingsystem.controllers;

import bankingsystem.model.DatabaseConnection;
import bankingsystem.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

    @FXML private Button btnDeposit;
    @FXML private Button btnHelpDesk;
    @FXML private Button btnLogoutCustomer;
    @FXML private Button btnPayment;
    @FXML private Button btnViewAccounts;
    @FXML private Button btnViewTransactions;
    @FXML private Button btnWithdraw;

    @FXML private Label lblWelcome;
    @FXML private Label txtAccountType;
    @FXML private Label txtBalance;

    @FXML
    public void initialize() {
        // Use title + name for welcome message
        String welcomeName = Session.currentTitle != null ? 
            Session.currentTitle + " " + Session.currentUserName : Session.currentUserName;
        lblWelcome.setText("Welcome, " + welcomeName);
        
        txtAccountType.setText(Session.currentAccountType != null ? Session.currentAccountType : "—");
        updateBalance();
    }

    private void updateBalance() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT SUM(balance) AS total FROM accounts WHERE customer_id = " +
                     "(SELECT id FROM customers WHERE username = ?) AND approved = 'Approved'")) {
            
            // We need to get the username - let's modify Session to store it
            // For now, we'll use a different approach
            String sql = """
                SELECT SUM(a.balance) AS total 
                FROM accounts a 
                JOIN customers c ON a.customer_id = c.id 
                WHERE c.name = ? AND a.approved = 'Approved'
                """;
            ps.setString(1, Session.currentUserName);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalBalance = rs.getDouble("total");
                Session.currentBalance = totalBalance;
                txtBalance.setText("P " + String.format("%,.2f", totalBalance));
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtBalance.setText("P 0.00");
        }
    }

    // =========================================
    // BUTTON ACTIONS
    // =========================================
    @FXML 
    void handleDeposit(ActionEvent event) { 
        loadScene("views/Deposit.fxml", event, "Deposit Funds"); 
    }
    
    @FXML 
    void handleWithdraw(ActionEvent event) { 
        loadScene("views/Withdraw.fxml", event, "Withdraw Funds"); 
    }
    
    @FXML 
    void handleViewAccounts(ActionEvent event) { 
        loadScene("views/ViewAccounts.fxml", event, "Your Accounts"); 
    }
    
    @FXML 
    void handleViewTransactions(ActionEvent event) { 
        loadScene("views/ViewTransactions.fxml", event, "Transactions"); 
    }
    
    @FXML 
    void handleMakePayments(ActionEvent event) { 
        loadScene("views/Payments.fxml", event, "Make Payments"); 
    }

    @FXML
    void handleHelpDesk(ActionEvent event) {
        loadScene("views/OpenAccount.fxml", event, "Open or Request Account");
    }

    @FXML
    void handleLogoutCustomer(ActionEvent event) {
        Session.clear();
        loadScene("views/Login.fxml", event, "Lekgwere Bank - Login");
    }

    // =========================================
    // NAVIGATION LOGIC
    // =========================================
    private void loadScene(String fxmlPath, ActionEvent event, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/" + fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Cannot load " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}