package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.CustomerDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;
import java.util.Random;

public class RegisterCustomerController {

    @FXML private ComboBox<String> cmbTitle;
    @FXML private TextField txtCustomerId;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtSurname;
    @FXML private TextField txtAddress;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtInitialBalance;
    @FXML private Label lblFeedback;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO   = new AccountDAO();
    private final TransactionDAO txDAO    = new TransactionDAO();

    @FXML
    private void initialize() {
        cmbTitle.getItems().addAll("Mr", "Mrs", "Ms", "Dr", "Prof");
        cmbAccountType.getItems().addAll("Savings", "Investors", "Cheque");

        txtCustomerId.setText(String.valueOf(generateCustomerId()));
        txtCustomerId.setEditable(false);
    }

    @FXML
    private void handleRegister() {
        try {
            String title       = requiredCombo(cmbTitle, "Title");
            String firstName   = required(txtFirstName, "First Name");
            String surname     = required(txtSurname, "Surname");
            String fullName    = title + " " + firstName + " " + surname;
            String address     = required(txtAddress, "Address");
            String username    = required(txtUsername, "Username");
            String password    = required(txtPassword, "Password");
            String acctType    = requiredCombo(cmbAccountType, "Account Type");
            double opening     = parseDouble(txtInitialBalance, "Initial Balance");

            int customerId = Integer.parseInt(txtCustomerId.getText().trim());

            // 1) SAVE CUSTOMER
            customerDAO.upsert(customerId, fullName, "", "", address, username, password);

            // 2) CREATE BANK ACCOUNT — DB AUTO-GENERATES acc_no
            BankAccount account = new BankAccount(
                    0,              // ❗ IMPORTANT: 0 → insert mode
                    opening,
                    address,
                    customerId,
                    new Date(),
                    "ACTIVE"
            ) {
                @Override
                public void deposit(double amount) { balance += amount; }

                @Override
                public boolean withdraw(double amount) {
                    if (amount > balance) return false;
                    balance -= amount;
                    return true;
                }

                @Override
                public String getAccountType() { return acctType; }
            };

            // 3) INSERT + GET REAL acc_no
            int accNo = accountDAO.create(account);

            // 4) RECORD INITIAL DEPOSIT
            if (opening > 0) {
                txDAO.record(accNo, "OpeningDeposit", opening,
                        "Initial deposit for new " + acctType + " account created by admin.");
            }

            showAlert(Alert.AlertType.INFORMATION,
                    "Registration Successful",
                    "Customer: " + fullName + "\n"
                    + "Account No: " + accNo + "\n"
                    + "Type: " + acctType + "\n"
                    + "Opening Balance: P" + opening);

            setFeedback("Customer registration completed.");
            handleClear();

            txtCustomerId.setText(String.valueOf(generateCustomerId()));

        } catch (Exception ex) {
            setFeedback("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int generateCustomerId() { return 1000 + new Random().nextInt(9000); }

    @FXML
    private void handleClear() {
        txtFirstName.clear();
        txtSurname.clear();
        txtAddress.clear();
        txtUsername.clear();
        txtPassword.clear();
        txtInitialBalance.clear();
        cmbTitle.getSelectionModel().clearSelection();
        cmbAccountType.getSelectionModel().clearSelection();
        lblFeedback.setText("");
    }

    @FXML
    private void handleBack() { Navigator.goTo("AdminDashboard.fxml", 1100, 700); }

    // ------------------ VALIDATION HELPERS ------------------
    private String required(TextInputControl c, String name) {
        String v = c.getText() == null ? "" : c.getText().trim();
        if (v.isEmpty()) throw new IllegalArgumentException(name + " is required.");
        return v;
    }

    private String requiredCombo(ComboBox<String> cb, String name) {
        if (cb.getValue() == null || cb.getValue().isBlank())
            throw new IllegalArgumentException(name + " is required.");
        return cb.getValue().trim();
    }

    private double parseDouble(TextField tf, String name) {
        try {
            return Double.parseDouble(required(tf, name));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a valid number.");
        }
    }

    private void setFeedback(String msg) { lblFeedback.setText(msg == null ? "" : msg); }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
