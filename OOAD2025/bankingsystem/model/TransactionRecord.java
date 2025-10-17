package bankingsystem.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionRecord {
    private final String customerName;
    private final String accountType;
    private final double amount;
    private final double balanceAfter;
    private final String date;
    private final String time;

    public TransactionRecord(String customerName, String accountType, double amount, double balanceAfter) {
        this.customerName = customerName;
        this.accountType = accountType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;

        LocalDateTime now = LocalDateTime.now();
        this.date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getCustomerName() { return customerName; }
    public String getAccountType() { return accountType; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
