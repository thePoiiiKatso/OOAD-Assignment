import java.util.Date;

public class IndividualCustomer extends Customer {
    private String firstname;
    private String surname;
    private String address;
    private String gender;
    private String occupation;
    private int age;
    private Date dateOfBirth;
    private String nationalID;

    public IndividualCustomer(int customerID, String firstname, String surname,
                              String email, String phoneNumber, Login login,
                              String address, String gender, String occupation,
                              int age, Date dateOfBirth, String nationalID) {
        super(customerID, email, phoneNumber, login);
        this.firstname = firstname;
        this.surname = surname;
        this.address = address;
        this.gender = gender;
        this.occupation = occupation;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.nationalID = nationalID;
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }

    @Override
    public String getDisplayName() {
        return firstname + " " + surname;
    }

    @Override
    public String toString() {
        return super.toString() +
               "\nName: " + firstname + " " + surname +
               "\nGender: " + gender +
               "\nOccupation: " + occupation +
               "\nAge: " + age +
               "\nNational ID: " + nationalID +
               "\nAddress: " + address;
    }
}
