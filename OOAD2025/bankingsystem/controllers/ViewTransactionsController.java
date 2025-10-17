package bankingsystem.controllers;

import bankingsystem.model.TransactionManager;
import bankingsystem.model.TransactionRecord;
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

public class ViewTransactionsController {

    @FXML
    private TableView<TransactionRecord> tblTransactions;
    @FXML
    private TableColumn<TransactionRecord, String> colTransactionID;
    @FXML
    private TableColumn<TransactionRecord, String> colTransaction;
    @FXML
    private TableColumn<TransactionRecord, String> colDate;
    @FXML
    private TableColumn<TransactionRecord, String> colTime;
    @FXML
    private TableColumn<TransactionRecord, String> colAccountType;

    @FXML
    public void initialize() {
        colTransactionID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        colTransaction.setCellValueFactory(new PropertyValueFactory<>("transaction"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colAccountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        // Load live data from TransactionManager
        ObservableList<TransactionRecord> transactions = TransactionManager.getTransactions();
        tblTransactions.setItems(transactions);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
