
public class Login {
    private String username;
    private String password;

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    
    public String getUsernameInfo() {
        return "Username: " + username;
    }

    public String getPasswordInfo() {
        return "Password: " + password;
    }
}
