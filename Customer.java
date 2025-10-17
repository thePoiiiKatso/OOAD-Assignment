package bankingsystem.model;


import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected int customerID;
    protected String email;
    protected String phoneNumber;
    protected Login login;
    protected List<BankAccount> accounts = new ArrayList<>();
    protected String status = "Active"; 

    // Constructor
    public Customer(int customerID, String email, String phoneNumber, Login login) {
        this.customerID = customerID;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.login = login;
    }

    // Business Methods
    public void openAccount(BankAccount account) {
        accounts.add(account);
    }

    public void closeAccount(int accountNumber) {
        accounts.removeIf(acc -> acc.getAccountNumber() == accountNumber);
    }


    public String listAccounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("Accounts for ").append(getDisplayName()).append(":\n");

        if (accounts.isEmpty()) {
            sb.append("No accounts found.\n");
        } else {
            for (BankAccount acc : accounts) {
                sb.append(" - Account #").append(acc.getAccountNumber())
                  .append(" | Balance: P").append(String.format("%.2f", acc.getBalance()))
                  .append(" | Branch: ").append(acc.getBranch())
                  .append(" | Status: ").append(acc.getStatus())
                  .append("\n");
            }
        }

        sb.append("Total Balance: P").append(String.format("%.2f", getTotalBalance())).append("\n");
        return sb.toString();
    }

  
    public double getTotalBalance() {
        return accounts.stream().mapToDouble(BankAccount::getBalance).sum();
    }

    // Abstract methods (for subclasses)
    public abstract String getCustomerType();
    public abstract String getDisplayName();

    // Getters and Setters
    public int getCustomerID() { return customerID; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Login getLogin() { return login; }
    public List<BankAccount> getAccounts() { return accounts; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Customer ID: " + customerID +
               "\nType: " + getCustomerType() +
               "\nEmail: " + email +
               "\nPhone: " + phoneNumber +
               "\nStatus: " + status;
    }
}
