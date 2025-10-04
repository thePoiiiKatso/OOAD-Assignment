import java.util.Date;

public abstract class BankAccount {
    protected int accountNumber;
    protected double balance;
    protected String branch;
    protected int customerID;
    protected Date dateCreated;
    protected String status;

    // Constructor
    public BankAccount(int accountNumber, double initialBalance, String branch, int customerID) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.branch = branch;
        this.customerID = customerID;
        this.dateCreated = new Date();
        this.status = "Active";
    }

    // Deposit method
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount + " | New Balance: " + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    // Withdraw method
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: " + amount + " | New Balance: " + balance);
        } else {
            System.out.println("Insufficient funds or invalid withdrawal.");
        }
    }

    // Getters
    public int getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public int getCustomerID() { return customerID; }
    public Date getDateCreated() { return dateCreated; }
    public String getStatus() { return status; }

    //  Setters
    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Close account
    public void closeAccount() {
        this.status = "Closed";
        System.out.println("Account " + accountNumber + " closed.");
    }
}
 
