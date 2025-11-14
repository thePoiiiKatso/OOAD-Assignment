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

        // ----- BASIC COLUMNS -----
        colAccNo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAccountNumber()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountType()));
        colCustomer.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCustomerID()));
        colBalance.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getBalance()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        // ----- NEW COLUMNS -----
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason()));
        colEmployer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployer()));

        colDate.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getRequestDate() != null
                        ? c.getValue().getRequestDate().toString()
                        : ""
        ));

        // ----- ACTION BUTTON PER ROW -----
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox container = new HBox(8, approveBtn, rejectBtn);

            {
                approveBtn.getStyleClass().add("button");
                rejectBtn.getStyleClass().add("button");

                approveBtn.setOnAction(e -> {
                    BankAccount acc = getTableView().getItems().get(getIndex());
                    approve(acc);
                });

                rejectBtn.setOnAction(e -> {
                    BankAccount acc = getTableView().getItems().get(getIndex());
                    reject(acc);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        loadPendingAccounts();
    }

    private void loadPendingAccounts() {
        try {
            List<BankAccount> pending = accountDAO.findAll().stream()
                    .filter(a -> "PENDING".equalsIgnoreCase(a.getStatus()))
                    .toList();

            tblPendingAccounts.setItems(FXCollections.observableArrayList(pending));

            lblFeedback.setText(pending.isEmpty() ? "No pending approvals." : "");

        } catch (Exception e) {
            lblFeedback.setText("Error loading pending accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void approve(BankAccount account) {
        try {
            account.setStatus("ACTIVE");
            accountDAO.save(account);

            transactionDAO.record(
                    account.getAccountNumber(),
                    "AccountOpen",
                    account.getBalance(),
                    "Admin approved " + account.getAccountType() + " account."
            );

            lblFeedback.setText("Approved.");
            loadPendingAccounts();

        } catch (Exception e) {
            lblFeedback.setText("Error approving: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reject(BankAccount account) {
        try {
            accountDAO.delete(account.getAccountNumber());
            lblFeedback.setText("Rejected.");
            loadPendingAccounts();

        } catch (Exception e) {
            lblFeedback.setText("Error rejecting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadPendingAccounts();
    }

    @FXML
    private void handleBack() {
        Navigator.goTo("AdminDashboard.fxml", 1100, 700);
    }
}
