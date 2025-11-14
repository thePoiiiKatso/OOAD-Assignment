package bankingsystem.model;

import java.util.Date;

public class ChequeAccount extends BankAccount {
    private String companyName;
    private String companyAddress;
    private int employeeID;
    private String employeeAddress;

    public ChequeAccount(int accountNumber, double balance, String branch,
                         int customerID, Date dateCreated, String status,
                         String companyName, String companyAddress,
                         int employeeID, String employeeAddress) {
        super(accountNumber, balance, branch, customerID, dateCreated, status);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.employeeID = employeeID;
        this.employeeAddress = employeeAddress;
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit must be positive.");
        if (amount > 50000)
            throw new IllegalStateException("Suspicious deposit over 50,000.");
        balance += amount;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal must be positive.");
        if (amount > balance)
            throw new IllegalStateException("Insufficient funds.");
        if (amount > 20000)
            throw new IllegalStateException("Suspicious withdrawal over 20,000.");
        balance -= amount;
        return true;
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public int getEmployeeID() { return employeeID; }
    public String getEmployeeAddress() { return employeeAddress; }
}
