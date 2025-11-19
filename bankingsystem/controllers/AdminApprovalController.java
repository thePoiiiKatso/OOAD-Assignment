package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.TransactionDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.Navigator;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AdminApprovalController {

    @FXML private TableView<BankAccount> tblPendingAccounts;

    @FXML private TableColumn<BankAccount, Integer> colAccNo;
    @FXML private TableColumn<BankAccount, String> colType;
    @FXML private TableColumn<BankAccount, Integer> colCustomer;
    @FXML private TableColumn<BankAccount, Double> colBalance;
    @FXML private TableColumn<BankAccount, String> colStatus;

    @FXML private TableColumn<BankAccount, String> colReason;
    @FXML private TableColumn<BankAccount, String> colEmployer;
    @FXML private TableColumn<BankAccount, String> colDate;

    @FXML private TableColumn<BankAccount, Void> colActions;

    @FXML private Label lblFeedback;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @FXML
    private void initialize() {

        colAccNo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAccountNumber()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountType()));
        colCustomer.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustomerID()));
        colBalance.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getBalance()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colEmployer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployer()));
        colDate.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getRequestDate() != null
                        ? c.getValue().getRequestDate().toString() : "")
        );

        // ---- Action buttons ----
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(10, approveBtn, rejectBtn);

            {
                approveBtn.setOnAction(e -> approve(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> reject(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        loadPendingAccounts();
    }

    // ---------------------------------------------------------
    // LOAD ALL PENDING REQUESTS
    // ---------------------------------------------------------
    private void loadPendingAccounts() {
        try {
            List<BankAccount> pending = accountDAO.findAll().stream()
                    .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                    .toList();

            tblPendingAccounts.setItems(FXCollections.observableArrayList(pending));
            lblFeedback.setText(pending.isEmpty() ? "No pending approvals." : "");

        } catch (Exception e) {
            lblFeedback.setText("Error loading accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // APPROVE
    // ---------------------------------------------------------
    private void approve(BankAccount pendingAcc) {

        double requiredDeposit = pendingAcc.getBalance();

        // Get ALL ACTIVE accounts from the customer
        List<BankAccount> activeAccounts =
                accountDAO.findByCustomerId(pendingAcc.getCustomerID()).stream()
                        .filter(a -> a.getStatus().equalsIgnoreCase("ACTIVE"))
                        .toList();

        if (activeAccounts.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "No ACTIVE account found to deduct the deposit from.");
            return;
        }

        // Total balance across ALL active accounts
        double totalCustomerBalance = activeAccounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

        if (totalCustomerBalance < requiredDeposit) {
            showAlert(Alert.AlertType.ERROR, "Insufficient Funds",
                    "Customer does not have enough money.\n" +
                            "Required: P" + requiredDeposit + "\n" +
                            "Available: P" + totalCustomerBalance);
            return;
        }

        try {
            // Deduct money from FIRST active account (or best strategy)
            BankAccount deductFrom = activeAccounts.get(0);
            deductFrom.setBalance(deductFrom.getBalance() - requiredDeposit);
            accountDAO.save(deductFrom);

            // Approve new account
            pendingAcc.setStatus("ACTIVE");
            accountDAO.save(pendingAcc);

            // Record transactions
            transactionDAO.record(
                    deductFrom.getAccountNumber(),
                    "AccountOpenDeduction",
                    requiredDeposit,
                    "Deducted for opening new " + pendingAcc.getAccountType() + " account."
            );

            transactionDAO.record(
                    pendingAcc.getAccountNumber(),
                    "AccountOpened",
                    requiredDeposit,
                    "Admin approved the new account."
            );

            showAlert(Alert.AlertType.INFORMATION, "Approved", "Account approved successfully.");
            loadPendingAccounts();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // REJECT
    // ---------------------------------------------------------
    private void reject(BankAccount account) {
        try {
            accountDAO.delete(account.getAccountNumber());
            showAlert(Alert.AlertType.INFORMATION, "Rejected",
                    "Account opening request rejected.");
            loadPendingAccounts();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error rejecting", e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // NAVIGATION + HELPERS
    // ---------------------------------------------------------
    @FXML
    private void handleRefresh() { loadPendingAccounts(); }

    @FXML
    private void handleBack() {
        Navigator.goTo("AdminDashboard.fxml", 1100, 700);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
