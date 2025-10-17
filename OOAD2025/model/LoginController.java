
public class LoginController {
    private Login login;

    public LoginController(Login login) {
        this.login = login;
    }

    public String getUsername() {
        return login.getUsername();
    }

    public String getPassword() {
        return login.getPassword();
    }

      public boolean authenticate(String inputUser, String inputPass) {
        return login.getUsername().equals(inputUser) && login.getPassword().equals(inputPass);
    }
     }