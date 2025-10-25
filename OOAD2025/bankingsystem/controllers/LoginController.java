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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private static final String ADMIN_USER = "Admin";
    private static final String ADMIN_PASS = "Admin123";

    @FXML
    private void handleLoginCustomer(ActionEvent event) {
        login(event, false);
    }

    @FXML
    private void handleLoginAdmin(ActionEvent event) {
        login(event, true);
    }

    private void login(ActionEvent event, boolean asAdmin) {
        String user = txtUsername.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please enter both username and password!");
            return;
        }

        boolean valid = asAdmin
                ? ADMIN_USER.equals(user) && ADMIN_PASS.equals(pass)
                : checkCustomerLogin(user, pass);

        if (!valid) {
            showError("Invalid " + (asAdmin ? "Admin" : "Customer") + " credentials!");
            return;
        }

        // Set session info
        Session.isAdmin = asAdmin;
        if (asAdmin) {
            Session.currentUserName = "Administrator";
        }

        String fxmlTarget = asAdmin
                ? "/bankingsystem/views/AdminDashboard.fxml"
                : "/bankingsystem/views/Dashboard.fxml"; // Fixed to match your Dashboard.fxml
        String title = asAdmin
                ? "Admin Dashboard – Lekgwere Banking"
                : "Customer Dashboard – Lekgwere Banking";

        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlTarget));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot load " + (asAdmin ? "admin" : "customer") + " screen: " + e.getMessage());
        }
    }

    private boolean checkCustomerLogin(String username, String password) {
        String sql = """
                SELECT c.name, c.surname, c.gender, a.accountType, a.balance
                FROM customers c
                JOIN accounts a ON c.id = a.customer_id
                WHERE c.username = ? AND c.password = ? AND a.approved = 'Approved'
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // ✅ Save all session info
                Session.currentUserName = rs.getString("name");
                Session.currentSurname = rs.getString("surname");
                Session.currentTitle = rs.getString("gender"); // stores "Mr", "Ms", etc.
                Session.currentAccountType = rs.getString("accountType");
                Session.currentBalance = rs.getDouble("balance");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }

        return false;
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Failed");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}