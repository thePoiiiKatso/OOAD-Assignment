
public class BankApp {
    public static void main(String[] args) {
        Login login = new Login("", "");
        LoginController controller = new LoginController(login);

        
        login.displayUsername();
        login.displayPassword();

       
    }
}
