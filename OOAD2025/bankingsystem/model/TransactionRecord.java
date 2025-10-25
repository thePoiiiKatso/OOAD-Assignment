package bankingsystem.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ✅ TransactionRecord Model
 * Represents one row in the transaction TableView.
 * Used in ViewTransactionsController.
 */
public class TransactionRecord {

    private final StringProperty transactionID;
    private final StringProperty transaction;
    private final StringProperty date;
    private final StringProperty time;
    private final StringProperty accountType;
    private final StringProperty customerName;

    public TransactionRecord(String transactionID, String transaction, String date, String time, String accountType, String customerName) {
        this.transactionID = new SimpleStringProperty(transactionID);
        this.transaction = new SimpleStringProperty(transaction);
        this.date = new SimpleStringProperty(date);
        this.time = new SimpleStringProperty(time);
        this.accountType = new SimpleStringProperty(accountType);
        this.customerName = new SimpleStringProperty(customerName);
    }

    // ==================================================
    // GETTERS
    // ==================================================
    public String getTransactionID() { return transactionID.get(); }
    public String getTransaction() { return transaction.get(); }
    public String getDate() { return date.get(); }
    public String getTime() { return time.get(); }
    public String getAccountType() { return accountType.get(); }
    public String getCustomerName() { return customerName.get(); }

    // ==================================================
    // PROPERTIES (used for TableColumn bindings)
    // ==================================================
    public StringProperty transactionIDProperty() { return transactionID; }
    public StringProperty transactionProperty() { return transaction; }
    public StringProperty dateProperty() { return date; }
    public StringProperty timeProperty() { return time; }
    public StringProperty accountTypeProperty() { return accountType; }
    public StringProperty customerNameProperty() { return customerName; }

    // ==================================================
    // SETTERS (optional — not required if you don’t edit rows)
    // ==================================================
    public void setTransactionID(String value) { transactionID.set(value); }
    public void setTransaction(String value) { transaction.set(value); }
    public void setDate(String value) { date.set(value); }
    public void setTime(String value) { time.set(value); }
    public void setAccountType(String value) { accountType.set(value); }
    public void setCustomerName(String value) { customerName.set(value); }
}
