package bankingsystem.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionManager {

    private static final ObservableList<TransactionRecord> transactions = FXCollections.observableArrayList();

    public static void addTransaction(String customerName, String accountType, double amount, double balanceAfter) {

        // create a unique transaction ID (timestamp-based)
        String transactionID = "TXN" + System.currentTimeMillis();

        // define transaction type — e.g., Deposit or Withdraw
        String transactionType = (amount >= 0) ? "Deposit" : "Withdrawal";

        // timestamp for date and time
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // create new transaction record
        TransactionRecord record = new TransactionRecord(
                transactionID,
                transactionType + " of P" + Math.abs(amount),
                date,
                time,
                accountType,
                customerName
        );

        transactions.add(record);
    }

    public static ObservableList<TransactionRecord> getTransactions() {
        return transactions;
    }
}


