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
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboardController {

    @FXML private Label lblPendingApprovals;
    @FXML private Label lblTotalAccounts;
    @FXML private Label lblTotalCustomers;

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total Customers
            String customersSQL = "SELECT COUNT(*) as count FROM customers";
            try (PreparedStatement ps = conn.prepareStatement(customersSQL)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) lblTotalCustomers.setText(String.valueOf(rs.getInt("count")));
            }

            // Pending Approvals
            String pendingSQL = "SELECT COUNT(*) as count FROM accounts WHERE approved = 'Pending'";
            try (PreparedStatement ps = conn.prepareStatement(pendingSQL)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) lblPendingApprovals.setText(String.valueOf(rs.getInt("count")));
            }

            // Total Accounts
            String accountsSQL = "SELECT COUNT(*) as count FROM accounts";
            try (PreparedStatement ps = conn.prepareStatement(accountsSQL)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) lblTotalAccounts.setText(String.valueOf(rs.getInt("count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load statistics: " + e.getMessage());
        }
    }

    @FXML
    void handleRegisterCustomer(ActionEvent event) {
        loadScene("views/RegisterCustomer.fxml", event, "Register New Customer");
    }

    @FXML
    void handleManageApprovals(ActionEvent event) {
        loadScene("views/AdminApproval.fxml", event, "Account Approvals");
    }

    @FXML
    void handleViewCustomers(ActionEvent event) {
        loadScene("views/CustomerManagement.fxml", event, "Customer Management");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        Session.clear();
        loadScene("views/Login.fxml", event, "Lekgwere Bank - Login");
    }

    private void loadScene(String fxmlPath, ActionEvent event, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/" + fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot load " + fxmlPath + ": " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}