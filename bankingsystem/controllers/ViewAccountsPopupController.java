package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.model.BankAccount;
import bankingsystem.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ViewAccountsPopupController {

    @FXML private TableView<BankAccount> tblAccounts;
    @FXML private TableColumn<BankAccount, String> colType;
    @FXML private TableColumn<BankAccount, String> colNumber;
    @FXML private TableColumn<BankAccount, Double> colBalance;

    private final AccountDAO accountDAO = new AccountDAO();

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        loadAccounts();
    }

    private void loadAccounts() {
        try {
            int id = SessionManager.getCurrentCustomer().getId();
            List<BankAccount> accounts = accountDAO.findByCustomerId(id);

            ObservableList<BankAccount> list = FXCollections.observableArrayList(accounts);
            tblAccounts.setItems(list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) tblAccounts.getScene().getWindow();
        stage.close();
    }
}
