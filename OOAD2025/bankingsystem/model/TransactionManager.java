package bankingsystem.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransactionManager {

    private static final ObservableList<TransactionRecord> transactions =
            FXCollections.observableArrayList();

    public static void addTransaction(String customerName, String accountType, double amount, double newBalance) {
        //Only four arguments
        TransactionRecord record = new TransactionRecord(customerName, accountType, amount, newBalance);
        transactions.add(record);
        System.out.println("Transaction added: " + customerName + " withdrew P" + amount);
    }

    public static ObservableList<TransactionRecord> getTransactions() {
        return transactions;
    }
}
