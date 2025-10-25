package bankingsystem.model;

import javafx.beans.property.*;

/**
 * Represents an account opening request made by a customer.
 */
public class Request {
    private final IntegerProperty id;
    private final StringProperty customerName;
    private final StringProperty accountType;
    private final DoubleProperty deposit;
    private final StringProperty employer;
    private final StringProperty reason;
    private final StringProperty status;
    private final StringProperty timestamp;

    public Request(int id, String customerName, String accountType, double deposit,
                   String employer, String reason, String status, String timestamp) {
        this.id = new SimpleIntegerProperty(id);
        this.customerName = new SimpleStringProperty(customerName);
        this.accountType = new SimpleStringProperty(accountType);
        this.deposit = new SimpleDoubleProperty(deposit);
        this.employer = new SimpleStringProperty(employer);
        this.reason = new SimpleStringProperty(reason);
        this.status = new SimpleStringProperty(status);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getCustomerName() { return customerName.get(); }
    public String getAccountType() { return accountType.get(); }
    public double getDeposit() { return deposit.get(); }
    public String getEmployer() { return employer.get(); }
    public String getReason() { return reason.get(); }
    public String getStatus() { return status.get(); }
    public String getTimestamp() { return timestamp.get(); }

    // Properties for TableView binding
    public IntegerProperty idProperty() { return id; }
    public StringProperty customerNameProperty() { return customerName; }
    public StringProperty accountTypeProperty() { return accountType; }
    public DoubleProperty depositProperty() { return deposit; }
    public StringProperty employerProperty() { return employer; }
    public StringProperty reasonProperty() { return reason; }
    public StringProperty statusProperty() { return status; }
    public StringProperty timestampProperty() { return timestamp; }
}

