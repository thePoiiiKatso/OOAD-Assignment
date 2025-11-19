package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.CustomerDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class SettingsController {

    @FXML private ComboBox<String> cmbAccounts;

    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;

    @FXML private Label lblFeedback;

    private final AccountDAO accountDAO = new AccountDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();


    // ---------------------------------------------------------
    // INITIALIZE
    // ---------------------------------------------------------
    @FXML
    public void initialize() {
        loadAccountsIntoDropdown();
    }


    // ---------------------------------------------------------
    // LOAD ACCOUNTS INTO COMBOBOX
    // ---------------------------------------------------------
    private void loadAccountsIntoDropdown() {
        try {
            if (SessionManager.getCurrentCustomer() == null) {
                System.out.println("⚠ No customer in session!");
                return;
            }

            int customerId = SessionManager.getCurrentCustomer().getId();
            List<BankAccount> accounts = accountDAO.findByCustomerId(customerId);

            ObservableList<String> items = FXCollections.observableArrayList();

            for (BankAccount acc : accounts) {
                items.add(acc.getAccountType() + " – " + acc.getAccountNumber());
            }

            cmbAccounts.setItems(items);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ---------------------------------------------------------
    // UPDATE PASSWORD
    // ---------------------------------------------------------
    @FXML
    private void handleUpdatePassword() {

        lblFeedback.setText(""); // Clear previous feedback

        String current = txtCurrentPassword.getText().trim();
        String newPass = txtNewPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();

        CustomerDAO.CustomerRecord currentUser = SessionManager.getCurrentCustomer();

        if (currentUser == null) {
            lblFeedback.setText("❌ Error: No user session.");
            return;
        }

        if (!current.equals(currentUser.getPassword())) {
            lblFeedback.setText("❌ Current password is incorrect.");
            return;
        }

        if (newPass.isEmpty()) {
            lblFeedback.setText("❌ New password cannot be empty.");
            return;
        }

        if (!newPass.equals(confirm)) {
            lblFeedback.setText("❌ Passwords do not match.");
            return;
        }

        try {
            // Update in database
            customerDAO.upsert(
                    currentUser.getId(),
                    currentUser.getName(),
                    currentUser.getEmail(),
                    currentUser.getPhone(),
                    currentUser.getAddress(),
                    currentUser.getUsername(),
                    newPass
            );

            // Update session
            SessionManager.setCurrentCustomer(
                    new CustomerDAO.CustomerRecord(
                            currentUser.getId(),
                            currentUser.getName(),
                            currentUser.getEmail(),
                            currentUser.getPhone(),
                            currentUser.getAddress(),
                            currentUser.getUsername(),
                            newPass
                    )
            );

            lblFeedback.setText("✅ Password updated successfully!");

            // CLEAR FIELDS AFTER SUCCESS
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();

        } catch (Exception e) {
            lblFeedback.setText("❌ Failed to update password.");
            e.printStackTrace();
        }
    }


    // ---------------------------------------------------------
    // DELETE ACCOUNT (ALERT ONLY)
    // ---------------------------------------------------------
    @FXML
    private void handleDeleteAccount() {

        String selected = cmbAccounts.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Account Selected");
            alert.setHeaderText("Please select an account");
            alert.setContentText("You must choose an account before deleting.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Deletion");
        alert.setHeaderText("Administrator Required");
        alert.setContentText(
                "Account deletion must be approved by the bank administrator.\n" +
                "Please contact the bank to request account closure."
        );
        alert.showAndWait();
    }


    // ---------------------------------------------------------
    // VIEW ACCOUNTS POPUP
    // ---------------------------------------------------------
    @FXML
    private void handleViewAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewAccountsPopup.fxml"));
            Parent root = loader.load();

            Stage popup = new Stage();
            popup.setTitle("Your Accounts");
            popup.setScene(new Scene(root));
            popup.setResizable(false);

            popup.show();

        } catch (Exception e) {
            System.out.println("⚠ Failed to load ViewAccountsPopup.fxml");
            e.printStackTrace();
        }
    }


    // ---------------------------------------------------------
    // BACK BUTTON
    // ---------------------------------------------------------
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) cmbAccounts.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            System.out.println("⚠ Failed to return to Dashboard");
            e.printStackTrace();
        }
    }
}
