package bankingsystem.model;

import java.util.Date;

public class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(int accountNumber, double initialBalance, String branch,
                          int customerID, Date dateCreated, String status) {
        super(accountNumber, initialBalance, branch, customerID, dateCreated, status);
        this.interestRate = 0.005; // 0.5%
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive.");
        balance += amount;
    }

    @Override
    public boolean withdraw(double amount) {
        // No withdrawals for savings account
        return false;
    }

    public double calculateInterest() {
        double interest = balance * interestRate;
        balance += interest;
        return interest;
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
