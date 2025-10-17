package bankingsystem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SavingsAccount extends BankAccount implements InterestBearing {
    private double interestRate = 0.005;  
    private int accountID;
    private double initialBalance;
    private List<String> transactions = new ArrayList<>(); 

    public SavingsAccount(int accountNumber, double initialBalance, String branch,
                          int customerID, Date dateCreated, String status) {
        super(accountNumber, initialBalance, branch, customerID, dateCreated, status);
        this.accountID = accountNumber;
        this.initialBalance = initialBalance;
        transactions.add("Account opened with initial deposit: P" + initialBalance);
    }

    // Withdrawals not allowed
    public String withdrawMessage() {
    return "Withdrawals are not allowed for Savings Accounts.";
}

    // Deposits allowed (unlimited)
    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive!");
        balance += amount;
        transactions.add("Deposit: P" + amount);
    }

    // Calculate and add interest
    @Override
    public double calculateInterest() {
        double interest = balance * interestRate;
        balance += interest;
        transactions.add("Interest added: P" + interest);
        return interest;
    }

    // Getters and Setters
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }

    public double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(double initialBalance) { this.initialBalance = initialBalance; }
}
