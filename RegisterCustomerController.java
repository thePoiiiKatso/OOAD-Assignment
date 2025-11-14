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

/**
 * Handles admin registration of new customers and creation
 * of their first bank account. First admin-created accounts
 * are always ACTIVE and do not require approval.
 */
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
        if (cmbTitle.getItems().isEmpty()) {
            cmbTitle.getItems().addAll("Mr", "Mrs", "Ms", "Dr", "Prof");
        }
        if (cmbAccountType.getItems().isEmpty()) {
            cmbAccountType.getItems().addAll("Savings", "Investors", "Cheque");
        }

        int generatedId = generateCustomerId();
        txtCustomerId.setText(String.valueOf(generatedId));
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

            // Save customer record
            customerDAO.upsert(customerId, fullName, "", "", address, username, password);

            // Admin-created account: ACTIVE immediately
            final String status = "ACTIVE";
            final int accNo = generateAccountNo();

            BankAccount account = new BankAccount(accNo, opening, address, customerId, new Date(), status) {
                @Override
                public void deposit(double amount) {
                    if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive.");
                    balance += amount;
                }

                @Override
                public boolean withdraw(double amount) {
                    if (amount <= 0) throw new IllegalArgumentException("Withdrawal must be positive.");
                    if (amount > balance) return false;
                    balance -= amount;
                    return true;
                }

                @Override
                public String getAccountType() {
                    return acctType;
                }
            };

            accountDAO.save(account);

            if (opening > 0) {
                txDAO.record(accNo, "OpeningDeposit", opening,
                        "Initial deposit for new " + acctType + " account created by admin.");
            }

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                    "Customer registered successfully.\n"
                    + "Name: " + fullName + "\n"
                    + "Account Type: " + acctType + "\n"
                    + "Opening Balance: P" + String.format("%.2f", opening) + "\n"
                    + "Status: ACTIVE");

            setFeedback("Customer registration completed.");
            handleClear();

            txtCustomerId.setText(String.valueOf(generateCustomerId()));

        } catch (Exception ex) {
            setFeedback("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int generateCustomerId() { 
        return 1000 + new Random().nextInt(9000); 
    }

    private int generateAccountNo() { 
        return 100000 + new Random().nextInt(900000); 
    }

    @FXML
    private void handleClear() {
        clear(txtFirstName, txtSurname, txtAddress, txtUsername, txtPassword, txtInitialBalance);
        cmbTitle.getSelectionModel().clearSelection();
        cmbAccountType.getSelectionModel().clearSelection();
        lblFeedback.setText("");
    }

    @FXML
    private void handleBack() {
        Navigator.goTo("AdminDashboard.fxml", 1100, 700);
    }

    private String required(TextInputControl c, String name) {
        String v = c.getText() == null ? "" : c.getText().trim();
        if (v.isEmpty())
            throw new IllegalArgumentException(name + " is required.");
        return v;
    }

    private String requiredCombo(ComboBox<String> cb, String name) {
        String v = cb.getValue();
        if (v == null || v.isBlank())
            throw new IllegalArgumentException(name + " is required.");
        return v.trim();
    }

    private double parseDouble(TextField tf, String name) {
        String s = required(tf, name);
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a valid number.");
        }
    }

    private void clear(TextInputControl... controls) {
        for (TextInputControl c : controls) {
            if (c != null) c.clear();
        }
    }

    private void setFeedback(String msg) {
        lblFeedback.setText(msg == null ? "" : msg);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
