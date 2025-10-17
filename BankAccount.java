package bankingsystem.model;



import java.util.Date;

public abstract class BankAccount {
    protected int accountNumber;
    protected double balance;
    protected String branch;
    protected int customerID;
    protected Date dateCreated;
    protected String status;

    // Constructor
    public BankAccount(int accountNumber, double balance, String branch, int customerID, Date dateCreated, String status) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customerID = customerID;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    // methods
    public void deposit(double amount) {
        balance += amount;
    }

    public void closeAccount() {
        status = "Closed";
    }

    // Getters
    public int getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public int getCustomerID() { return customerID; }
    public Date getDateCreated() { return dateCreated; }
    public String getStatus() { return status; }

    // Setters
    public void setAccountNumber(int accountNumber) { this.accountNumber = accountNumber; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setBranch(String branch) { this.branch = branch; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setStatus(String status) { this.status = status; }
}
