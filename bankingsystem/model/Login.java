package bankingsystem.model;

public class Login {
    private String username;
    private String password;
    private String role;

    public Login(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
