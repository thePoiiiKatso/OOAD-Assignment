
import java.util.Date;

public class TransactionAccount {
    private int accountID;
    private int transactionID;
    private String transactionType;
    private double amount;
    private Date date;

    // Constructor
    public TransactionAccount(int accountID, int transactionID, String transactionType, double amount) {
        if (accountID <= 0)
            throw new IllegalArgumentException("Invalid account ID!");
        if (transactionID <= 0)
            throw new IllegalArgumentException("Invalid transaction ID!");
        if (transactionType == null || transactionType.trim().isEmpty())
            throw new IllegalArgumentException("Transaction type cannot be empty!");
        if (amount <= 0)
            throw new IllegalArgumentException("Transaction amount must be positive!");

        this.accountID = accountID;
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = new Date(); // Record transaction date/time
    }

    // Getters/Setters with validation
    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) {
        if (accountID <= 0)
            throw new IllegalArgumentException("Invalid account ID!");
        this.accountID = accountID;
    }

    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) {
        if (transactionID <= 0)
            throw new IllegalArgumentException("Invalid transaction ID!");
        this.transactionID = transactionID;
    }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) {
        if (transactionType == null || transactionType.trim().isEmpty())
            throw new IllegalArgumentException("Transaction type cannot be empty!");
        this.transactionType = transactionType;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Transaction amount must be positive!");
        this.amount = amount;
    }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @Override
    public String toString() {
        return "Transaction #" + transactionID +
               " | Type: " + transactionType +
               " | Amount: P" + amount +
               " | Date: " + date;
    }
}
