package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;
import java.util.List;

public class OpenAccountController {

    @FXML private ComboBox<String> cmbAccountType;
    @FXML private TextField txtInitialDeposit;
    @FXML private TextField txtEmployer;
    @FXML private TextField txtReason;
    @FXML private Label lblFeedback;

    private final AccountDAO accountDAO = new AccountDAO();
    private int customerId;

    @FXML
    private void initialize() {
        cmbAccountType.getItems().addAll("Savings", "Investors", "Cheque");
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @FXML
    private void handleSubmitRequest() {
        lblFeedback.setText("");

        try {

            //-----------------------------
            // ACCOUNT TYPE SELECTION
            //-----------------------------
            String type = cmbAccountType.getValue();
            if (type == null || type.isBlank()) {
                warn("Missing Field", "Please select an account type.");
                return;
            }

            //-----------------------------
            // DEPOSIT VALIDATION
            //-----------------------------
            String depositStr = txtInitialDeposit.getText().trim();
            if (depositStr.isBlank()) {
                warn("Missing Field", "Enter an initial deposit amount.");
                return;
            }

            double initialDeposit;
            try {
                initialDeposit = Double.parseDouble(depositStr);
            } catch (NumberFormatException e) {
                warn("Invalid Input", "Deposit must be a valid number.");
                return;
            }

            //-----------------------------
            // MINIMUM REQUIRED DEPOSIT
            //-----------------------------
            double minimumRequired = switch (type.toLowerCase()) {
                case "savings" -> 100;
                case "investors" -> 500;
                case "cheque" -> 1000;
                default -> 0;
            };

            if (initialDeposit < minimumRequired) {
                warn("Insufficient Deposit",
                        type + " accounts require at least P" + minimumRequired +
                                "\nYou entered: P" + initialDeposit);
                return;
            }

            //-----------------------------
            // GET CUSTOMER ACCOUNTS
            //-----------------------------
            List<BankAccount> customerAccounts = accountDAO.findByCustomerId(customerId);

            //-----------------------------
            // PREVENT DUPLICATE PENDING REQUEST
            //-----------------------------
            boolean hasPending = customerAccounts.stream()
                    .anyMatch(a -> a.getAccountType().equalsIgnoreCase(type)
                            && a.getStatus().equalsIgnoreCase("PENDING"));

            if (hasPending) {
                warn("Duplicate Request",
                        "You already have a pending request for a " + type + " account.");
                return;
            }

            //-----------------------------
            // GET FUNDING ACCOUNT (FIRST ACTIVE)
            //-----------------------------
            BankAccount fundingAccount = customerAccounts.stream()
                    .filter(a -> a.getStatus().equalsIgnoreCase("ACTIVE"))
                    .findFirst()
                    .orElse(null);

            if (fundingAccount == null) {
                warn("No Active Account",
                        "You must have at least one ACTIVE account to fund this new account.");
                return;
            }

            //-----------------------------
            // CHECK IF CUSTOMER CAN AFFORD IT
            //-----------------------------
            if (fundingAccount.getBalance() < initialDeposit) {
                warn("Insufficient Balance",
                        "Your active account does not have enough funds.\n" +
                                "Required: P" + initialDeposit +
                                "\nAvailable: P" + fundingAccount.getBalance());
                return;
            }

            //-----------------------------
            // EXTRA INFO VALIDATION
            //-----------------------------
            String employer = txtEmployer.getText().trim();
            if (employer.isBlank()) {
                warn("Missing Employer", "Please enter your employer/income source.");
                return;
            }

            String reason = txtReason.getText().trim();
            if (reason.isBlank()) {
                warn("Missing Reason", "Please provide a reason for opening this account.");
                return;
            }

            //-----------------------------
            // CREATE PENDING REQUEST
            //-----------------------------
            BankAccount newAccount = new BankAccount(
                    0,                     // auto ID
                    initialDeposit,        // stored until admin approves
                    "Lekgwere Main Branch",
                    customerId,
                    new Date(),
                    "PENDING"
            ) {
                @Override public void deposit(double x) { balance += x; }
                @Override public boolean withdraw(double x) { return false; }
                @Override public String getAccountType() { return type; }
            };

            newAccount.setEmployer(employer);
            newAccount.setReason(reason);

            accountDAO.save(newAccount);

            info("Request Submitted",
                    "Your " + type + " account request has been submitted.\n" +
                            "Admin must approve it.");

            clearForm();

        } catch (Exception e) {
            lblFeedback.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //-----------------------------
    // CLEAR FIELDS
    //-----------------------------
    private void clearForm() {
        cmbAccountType.getSelectionModel().clearSelection();
        txtInitialDeposit.clear();
        txtEmployer.clear();
        txtReason.clear();
    }

    //-----------------------------
    // BACK BUTTON
    //-----------------------------
    @FXML
    private void handleBack() {
        Navigator.goToWithController("/Dashboard.fxml", 873, 700, controller -> {
            if (controller instanceof DashboardController dash) {
                dash.setCustomerId(customerId);
            }
        });
    }

    //-----------------------------
    // ALERT HELPERS
    //-----------------------------
    private void warn(String t, String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }

    private void info(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}
