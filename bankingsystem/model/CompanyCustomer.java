package bankingsystem.model;

public class CompanyCustomer extends Customer {
    private String companyName;
    private String registrationNumber;
    private String contactPerson;
    private String address;
    private String industry;

    public CompanyCustomer(int customerID, String email, String phoneNumber, Login login,
                           String companyName, String registrationNumber,
                           String contactPerson, String address, String industry) {
        super(customerID, email, phoneNumber, login);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactPerson = contactPerson;
        this.address = address;
        this.industry = industry;
    }

    @Override
    public String getCustomerType() { return "Company"; }

    @Override
    public String getDisplayName() { return companyName; }

    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactPerson() { return contactPerson; }
    public String getAddress() { return address; }
    public String getIndustry() { return industry; }
}
