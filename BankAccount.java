package bankingsystem.model;

import java.util.Date;

/**
 * Abstract base class for all bank account types.
 * Supports admin approval system fields (reason, employer, requestDate)
 */
public abstract class BankAccount {

    protected int accountNumber;
    protected String accountType;
    protected double balance;
    protected String address;
    protected String status;
    protected int customerID;
    protected Date dateCreated;

    // -------------------------------------------------
    // NEW FIELDS FOR ACCOUNT REQUESTS
    // -------------------------------------------------
    protected String reason;        // Why customer wants account
    protected String employer;      // Employer or income source
    protected Date requestDate;     // When request was submitted

    /**
     * Constructor for BankAccount.
     */
    public BankAccount(int accountNumber, double balance, String address, int customerID,
                       Date dateCreated, String status) {

        this.accountNumber = accountNumber;
        this.balance = balance;
        this.address = address;
        this.customerID = customerID;

        // Original creation date
        this.dateCreated = (dateCreated == null ? new Date() : dateCreated);

        // Account state (ACTIVE, PENDING, CLOSED)
        this.status = (status == null ? "PENDING" : status.toUpperCase());

        // This will be overridden by subclasses or controllers
        this.accountType = getClass().getSimpleName();

        // Default values for request system
        this.reason = "N/A";
        this.employer = "N/A";
        this.requestDate = new Date();  // Now
    }

    // ---------------------------------------------------------
    // ABSTRACT METHODS
    // ---------------------------------------------------------
    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);

    // ---------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------
    public int getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public int getCustomerID() { return customerID; }
    public Date getDateCreated() { return dateCreated; }

    public String getReason() { return reason; }
    public String getEmployer() { return employer; }
    public Date getRequestDate() { return requestDate; }

    // Compatibility
    public int getId() { return accountNumber; }

    // ---------------------------------------------------------
    // SETTERS
    // ---------------------------------------------------------
    public void setAccountNumber(int accountNumber) { this.accountNumber = accountNumber; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setStatus(String status) { this.status = status; }
    public void setAddress(String address) { this.address = address; }

    public void setReason(String reason) { this.reason = reason; }
    public void setEmployer(String employer) { this.employer = employer; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public void setAccountType(String type) { this.accountType = type; }

    // ---------------------------------------------------------
    // DEBUG OUTPUT
    // ---------------------------------------------------------
    @Override
    public String toString() {
        return String.format(
            "Account #%d (%s) | Balance: %.2f | Customer: %d | Status: %s | Reason: %s",
            accountNumber, accountType, balance, customerID, status, reason
        );
    }
}
