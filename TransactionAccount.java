package bankingsystem.model;

import java.util.Date;

public class TransactionAccount {
    private int accountID;
    private int transactionID;
    private String transactionType;
    private double amount;
    private Date date;

    public TransactionAccount(int accountID, int transactionID, String transactionType, double amount) {
        if (accountID <= 0) throw new IllegalArgumentException("Invalid account ID");
        if (transactionID <= 0) throw new IllegalArgumentException("Invalid transaction ID");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (transactionType == null || transactionType.isBlank())
            throw new IllegalArgumentException("Transaction type cannot be empty");
        this.accountID = accountID;
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = new Date();
    }

    public int getAccountID() { return accountID; }
    public int getTransactionID() { return transactionID; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return "Transaction #" + transactionID + " [" + transactionType + "] " +
                "P" + amount + " on " + date;
    }
}
