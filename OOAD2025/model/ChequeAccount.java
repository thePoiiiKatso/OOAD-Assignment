import java.util.ArrayList;
import java.util.Date;
import java.util.List;

    public class ChequeAccount extends BankAccount implements Withdrawable {
     
    private String companyName;
    private String companyAddress;
    private int employeeID;
    private String employeeAddress;
    private int accountID;
    private List<String> transactions = new ArrayList<>();

    public ChequeAccount(int accountNumber, double balance, String branch,
                         int customerID, Date dateCreated, String status,
                         String companyName, String companyAddress,
                         int employeeID, String employeeAddress) {
        super(accountNumber, balance, branch, customerID, dateCreated, status);
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.employeeID = employeeID;
        this.employeeAddress = employeeAddress;
        this.accountID = accountNumber;
    }

    // Deposit
    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive!");
        if (amount > 50000)
            throw new RuntimeException("Suspicious deposit: P" + amount);
        setBalance(getBalance() + amount);
        transactions.add("Deposit: P" + amount);
    }

    // Withdraw
      @Override
    public void withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive!");
        if (amount > getBalance())
            throw new RuntimeException("Insufficient funds! Current balance: P" + getBalance());
        if (amount > 20000)
            throw new RuntimeException("Suspicious withdrawal: P" + amount);
        setBalance(getBalance() - amount);
        transactions.add("Withdrawal: P" + amount);
    }

    public double calculateInterest() { return 0.0; }

    // Getters/Setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public int getEmployeeID() { return employeeID; }
    public void setEmployeeID(int employeeID) { this.employeeID = employeeID; }
    public String getEmployeeAddress() { return employeeAddress; }
    public void setEmployeeAddress(String employeeAddress) { this.employeeAddress = employeeAddress; }
    public int getAccountID() { return accountID; }
    public void setAccountID(int accountID) { this.accountID = accountID; }
}
