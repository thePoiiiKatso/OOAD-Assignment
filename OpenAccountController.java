package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Date;

/**
 * Controller for OpenAccount.fxml
 * Handles customer account creation requests.
 * These accounts are saved with PENDING status
 * and must be approved by an admin.
 */
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
        try {
            String type = cmbAccountType.getValue();
            if (type == null || type.isBlank()) {
                lblFeedback.setText("Please select an account type.");
                return;
            }

            String depositStr = txtInitialDeposit.getText();
            if (depositStr == null || depositStr.isBlank()) {
                lblFeedback.setText("Enter an initial deposit amount.");
                return;
            }

            double initialDeposit = Double.parseDouble(depositStr);
            if (initialDeposit < 50) {
                lblFeedback.setText("Minimum initial deposit is P50.");
                return;
            }

            String employer = txtEmployer.getText().isBlank()
                    ? "N/A"
                    : txtEmployer.getText().trim();

            String reason = txtReason.getText().isBlank()
                    ? "N/A"
                    : txtReason.getText().trim();

            BankAccount newAccount = new BankAccount(
                    0,
                    initialDeposit,
                    "Lekgwere Main Branch",
                    customerId,
                    new Date(),
                    "PENDING"
            ) {
                @Override
                public void deposit(double x) {
                    balance += x;
                }

                @Override
                public boolean withdraw(double x) {
                    return false; // not allowed until approved
                }

                @Override
                public String getAccountType() {
                    return type;
                }
            };

            accountDAO.save(newAccount);

            lblFeedback.setText("Account request submitted. Awaiting admin approval.");
            clearForm();

        } catch (NumberFormatException e) {
            lblFeedback.setText("Invalid deposit amount. Enter numbers only.");
        } catch (Exception e) {
            lblFeedback.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        cmbAccountType.getSelectionModel().clearSelection();
        txtInitialDeposit.clear();
        txtEmployer.clear();
        txtReason.clear();
    }

    @FXML
    private void handleBack() {
        Navigator.goToWithController("/Dashboard.fxml", 873, 700, controller -> {
            if (controller instanceof DashboardController dash) {
                dash.setCustomerId(customerId);
            }
        });
    }
}
