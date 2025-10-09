package bankingsystem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

   public class InvestmentAccount extends BankAccount implements Withdrawable, InterestBearing  {
    private double interestRate = 0.05;  
    private int accountID;
    private List<String> transactions = new ArrayList<>();  

    public InvestmentAccount(int accountNumber, double openingBalance, String branch,
                             int customerID, Date dateCreated, String status) {
        super(accountNumber, openingBalance, branch, customerID, dateCreated, status);

        if (openingBalance < 500)
            throw new IllegalArgumentException("Initial deposit must be at least BWP 500.00");

        this.accountID = accountNumber;
        transactions.add("Account opened with initial deposit: P" + openingBalance);
    }

    // Deposit 
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive!");
        balance += amount;
        transactions.add("Deposit: P" + amount);
    }
    // withdraw
    public void withdraw(double amount) {
    if (amount <= 0)
        throw new IllegalArgumentException("Withdrawal amount must be positive!");
    if (amount > balance)
        throw new RuntimeException("Insufficient funds! Current balance: P" + balance);
    balance -= amount;
}


    // Calculate and apply interest
    @Override
    public double calculateInterest() {
        double interest = balance * interestRate;
        balance += interest;
        transactions.add("Interest added: P" + interest);
        return interest;
    }
    
    public String showTransactions() {
    StringBuilder summary = new StringBuilder("\nTransaction history for Investment Account #" + accountID + ":\n");
    for (String t : transactions) {
        summary.append(" - ").append(t).append("\n");
    }
    return summary.toString();
}

    // Getters and setters
    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }
}
