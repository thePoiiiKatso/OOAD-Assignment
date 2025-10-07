
package bankingsystem.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Customer {
    private String firstname;
    private String surname;
    private String address;
    private String gender;
    private String email;
    private String phoneNumber;
    private int customerID;
    private String occupation;
    private int age;
    private Date date;
    private String nationalID;
    private Login login;

    private List<BankAccount> accounts = new ArrayList<>();

    // Constructor
    public Customer(int customerID, String firstname, String surname, String email, Login login) {
        this.customerID = customerID;
        this.firstname = firstname;
        this.surname = surname;
        this.email = email;
        this.login = login;
    }

    // Methods
    public void openAccount(BankAccount account) {
        accounts.add(account);
    }

    
    public void listAccounts() {
        System.out.println("\nAccounts for " + firstname + " " + surname + ":");
        for (BankAccount acc : accounts) {
            System.out.println(" - Account #" + acc.getAccountNumber() + " | Balance: P" + acc.getBalance());
        }
    }

    // Getters and Setters
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getNationalID() { return nationalID; }
    public void setNationalID(String nationalID) { this.nationalID = nationalID; }

    public Login getLogin() { return login; }
    public void setLogin(Login login) { this.login = login; }

    public List<BankAccount> getAccounts() { return accounts; }

    @Override
    public String toString() {
        return "Customer ID: " + customerID +
               "\nName: " + firstname + " " + surname +
               "\nEmail: " + email +
               "\nGender: " + gender +
               "\nOccupation: " + occupation;
    }
}
