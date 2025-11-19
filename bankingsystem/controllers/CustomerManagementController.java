package bankingsystem.controllers;

import bankingsystem.dao.AccountDAO;
import bankingsystem.dao.AccountDAO.AccountRecord;
import bankingsystem.util.Navigator;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CustomerManagementController {

    @FXML private TableView<CustomerRow> tblCustomers;
    @FXML private TableColumn<CustomerRow, Number> colCustomerId;
    @FXML private TableColumn<CustomerRow, String> colFullName;
    @FXML private TableColumn<CustomerRow, String> colUsername;
    @FXML private TableColumn<CustomerRow, String> colAddress;
    @FXML private TableColumn<CustomerRow, String> colAccountType;
    @FXML private TableColumn<CustomerRow, Number> colBalance;

    @FXML private TextField txtSearch;
    @FXML private Label lblTotalCustomers;
    @FXML private Label lblActiveAccounts;

    private final ObservableList<CustomerRow> customerList = FXCollections.observableArrayList();
    private final AccountDAO accountDAO = new AccountDAO();

    @FXML
    private void initialize() {
        setupTable();
        loadAllCustomers();
    }

    private void setupTable() {
        colCustomerId.setCellValueFactory(d -> d.getValue().idProperty());
        colFullName.setCellValueFactory(d -> d.getValue().fullNameProperty());
        colUsername.setCellValueFactory(d -> d.getValue().usernameProperty());
        colAddress.setCellValueFactory(d -> d.getValue().addressProperty());
        colAccountType.setCellValueFactory(d -> d.getValue().accountTypeProperty());
        colBalance.setCellValueFactory(d -> d.getValue().balanceProperty());
        tblCustomers.setItems(customerList);
    }

    private void loadAllCustomers() {
        customerList.clear();
        try {
            for (AccountRecord a : accountDAO.findAllDetailed()) {
                customerList.add(new CustomerRow(
                        a.getCustomerId(),
                        a.getCustomerName(),
                        a.getUsername() != null ? a.getUsername() : "(no username)",
                        a.getAddress() != null ? a.getAddress() : "N/A",
                        a.getType(),
                        a.getBalance()
                ));
            }

            // TOTAL CUSTOMERS = number of rows
            lblTotalCustomers.setText(String.valueOf(customerList.size()));

            // ACTIVE ACCOUNTS from findAll()
            long active = accountDAO.findAll().stream()
                    .filter(ac -> "ACTIVE".equalsIgnoreCase(ac.getStatus()))
                    .count();
            lblActiveAccounts.setText(String.valueOf(active));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText();
        if (query == null || query.isBlank()) {
            loadAllCustomers();
            return;
        }

        String lower = query.toLowerCase();
        customerList.clear();

        try {
            accountDAO.findAllDetailed().stream()
                    .filter(a ->
                            a.getCustomerName().toLowerCase().contains(lower) ||
                            (a.getAddress() != null && a.getAddress().toLowerCase().contains(lower)) ||
                            (a.getUsername() != null && a.getUsername().toLowerCase().contains(lower)) ||
                            a.getType().toLowerCase().contains(lower) ||
                            String.valueOf(a.getAccNo()).contains(lower)
                    )
                    .forEach(a -> customerList.add(new CustomerRow(
                            a.getCustomerId(),
                            a.getCustomerName(),
                            a.getUsername() != null ? a.getUsername() : "(no username)",
                            a.getAddress() != null ? a.getAddress() : "N/A",
                            a.getType(),
                            a.getBalance()
                    )));

            lblTotalCustomers.setText(String.valueOf(customerList.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowAll() {
        txtSearch.clear();
        loadAllCustomers();
    }

    @FXML
    private void handleBack() {
        Navigator.goTo("AdminDashboard.fxml", 1100, 700);
    }

    public static class CustomerRow {
        private final IntegerProperty id;
        private final StringProperty fullName;
        private final StringProperty username;
        private final StringProperty address;
        private final StringProperty accountType;
        private final DoubleProperty balance;

        public CustomerRow(int id, String fullName, String username, String address,
                           String accountType, double balance) {
            this.id = new SimpleIntegerProperty(id);
            this.fullName = new SimpleStringProperty(fullName);
            this.username = new SimpleStringProperty(username);
            this.address = new SimpleStringProperty(address);
            this.accountType = new SimpleStringProperty(accountType);
            this.balance = new SimpleDoubleProperty(balance);
        }

        public IntegerProperty idProperty() { return id; }
        public StringProperty fullNameProperty() { return fullName; }
        public StringProperty usernameProperty() { return username; }
        public StringProperty addressProperty() { return address; }
        public StringProperty accountTypeProperty() { return accountType; }
        public DoubleProperty balanceProperty() { return balance; }
    }
}
