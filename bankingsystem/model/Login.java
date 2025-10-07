package bankingsystem.model;

public class Login {
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    
    public void displayUsername() {
        System.out.println("Username: " + username);
    }

    public void displayPassword() {
        System.out.println("Password: " + password);
    }
}
