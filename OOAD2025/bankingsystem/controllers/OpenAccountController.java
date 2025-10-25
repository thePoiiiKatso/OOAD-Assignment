package bankingsystem.controllers;

import bankingsystem.model.DatabaseConnection;
import bankingsystem.model.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OpenAccountController {

    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtInitialDeposit;
    @FXML private TextField txtEmployer;
    @FXML private TextField txtReason;
    @FXML private Label lblFeedback;
    @FXML private Button btnSubmitRequest;

    @FXML
    private void initialize() {
        cmbAccountType.getItems().addAll("Savings Account", "Cheque Account", "Investors Account");

        txtEmployer.setVisible(false);
        txtReason.setVisible(false);

        cmbAccountType.setOnAction(e -> {
            String selected = cmbAccountType.getValue();
            boolean isCheque = "Cheque Account".equals(selected);
            txtEmployer.setVisible(isCheque);
            txtReason.setVisible(isCheque);
        });
    }

    @FXML
    private void handleSubmitRequest(ActionEvent event) {
        String accountType = cmbAccountType.getValue();
        String depositStr = txtInitialDeposit.getText().trim();
        String employer = txtEmployer.getText().trim();
        String reason = txtReason.getText().trim();

        if (accountType == null || depositStr.isEmpty()) {
            showFeedback("⚠️ Please select an account type and enter deposit amount.", "red");
            return;
        }

        double deposit;
        try {
            deposit = Double.parseDouble(depositStr);
        } catch (NumberFormatException e) {
            showFeedback("❌ Deposit amount must be numeric.", "red");
            return;
        }

        if (accountType.equals("Investors Account") && deposit < 5000) {
            showFeedback("❌ Minimum deposit for Investors Account is P5000.", "red");
            return;
        }

        if (accountType.equals("Cheque Account") && (employer.isEmpty() || reason.isEmpty())) {
            showFeedback("⚠️ Cheque Account requires Employer and Reason fields.", "red");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showFeedback("❌ Database connection failed.", "red");
                return;
            }

            String sql = """
                INSERT INTO account_requests (customer_id, accountType, depositAmount, employer, companyAddress, status)
                VALUES ((SELECT id FROM customers WHERE name = ?), ?, ?, ?, ?, 'Pending')
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, Session.currentUserName);
            ps.setString(2, accountType);
            ps.setDouble(3, deposit);
            ps.setString(4, employer.isEmpty() ? null : employer);
            ps.setString(5, reason.isEmpty() ? null : reason);
            ps.executeUpdate();

            showFeedback("✅ Request submitted! Awaiting admin approval.", "green");
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showFeedback("❌ Database error: " + e.getMessage(), "red");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeedback(String message, String color) {
        lblFeedback.setText(message);
        lblFeedback.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    private void clearFields() {
        cmbAccountType.getSelectionModel().clearSelection();
        txtInitialDeposit.clear();
        txtEmployer.clear();
        txtReason.clear();
        txtEmployer.setVisible(false);
        txtReason.setVisible(false);
    }
}
