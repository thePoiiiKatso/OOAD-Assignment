package bankingsystem.model;

public class IndividualCustomer extends Customer {
    private String firstName;
    private String lastName;

    public IndividualCustomer(int customerID, String email, String phoneNumber,
                              Login login, String firstName, String lastName) {
        super(customerID, email, phoneNumber, login);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getCustomerType() { return "Individual"; }

    @Override
    public String getDisplayName() { return firstName + " " + lastName; }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
