package bankingsystem.model;

import java.util.Date;

public class InvestmentAccount extends BankAccount {
    private double interestRate;

    public InvestmentAccount(int accountNumber, double openingBalance, String branch,
                             int customerID, Date dateCreated, String status) {
        super(accountNumber, openingBalance, branch, customerID, dateCreated, status);
        if (openingBalance < 500)
            throw new IllegalArgumentException("Initial deposit must be at least P500.00");
        this.interestRate = 0.05; // 5%
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit must be positive.");
        balance += amount;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal must be positive.");
        if (amount > balance)
            throw new IllegalStateException("Insufficient funds.");
        balance -= amount;
        return true;
    }

    public double calculateInterest() {
        double interest = balance * interestRate;
        balance += interest;
        return interest;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
