package bankingsystem.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    protected int customerID;
    protected String email;
    protected String phoneNumber;
    protected Login login;
    protected List<BankAccount> accounts;
    protected String status;

    public Customer(int customerID, String email, String phoneNumber, Login login) {
        this.customerID = customerID;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.login = login;
        this.accounts = new ArrayList<>();
        this.status = "Active";
    }

    public void openAccount(BankAccount account) {
        accounts.add(account);
    }

    public void closeAccount(int accountNumber) {
        accounts.removeIf(acc -> acc.getAccountNumber() == accountNumber);
    }

    public double getTotalBalance() {
        return accounts.stream().mapToDouble(BankAccount::getBalance).sum();
    }

    public List<BankAccount> getAccounts() { return accounts; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getCustomerID() { return customerID; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public abstract String getCustomerType();
    public abstract String getDisplayName();

    @Override
    public String toString() {
        return getCustomerType() + " Customer #" + customerID + " (" + getDisplayName() + ")";
    }
}
