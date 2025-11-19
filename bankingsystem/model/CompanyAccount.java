package bankingsystem.model;

import java.util.Date;

public class CompanyAccount extends BankAccount implements Withdraw {

    public CompanyAccount(int accountNumber, double balance, String address,
                          int customerID, Date dateCreated, String status) {
        super(accountNumber, balance, address, customerID, dateCreated, status);
        this.accountType = "CompanyAccount";
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
            return false;
        }

        if (amount > balance) {
            System.out.println("Insufficient funds for company account.");
            return false;
        }

        balance -= amount;
        return true;
    }

    @Override
    public String toString() {
        return "CompanyAccount{" +
                "accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", address='" + address + '\'' +
                ", customerID=" + customerID +
                ", status='" + status + '\'' +
                '}';
    }
}
