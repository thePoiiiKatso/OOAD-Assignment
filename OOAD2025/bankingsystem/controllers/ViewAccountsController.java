package bankingsystem.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewAccountsController {

    @FXML
    private TableView<Account> tblAccounts;

    @FXML
    private TableColumn<Account, String> colAccountNumber;

    @FXML
    private TableColumn<Account, String> colAccountType;

    @FXML
    private TableColumn<Account, Double> colBalance;

    @FXML
    public void initialize() {
        // link table columns to Account properties
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colAccountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // load sample accounts
        ObservableList<Account> accounts = FXCollections.observableArrayList(
            new Account("001", "Cheque Account", 1000.00),
            new Account("002", "Savings Account", 4500.00),
            new Account("003", "Investment Account", 12000.50)
        );

        tblAccounts.setItems(accounts);
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

    // inner class model for displaying accounts
    public static class Account {
        private final String accountNumber;
        private final String accountType;
        private final Double balance;

        public Account(String accountNumber, String accountType, Double balance) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.balance = balance;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public Double getBalance() { return balance; }
    }
}
