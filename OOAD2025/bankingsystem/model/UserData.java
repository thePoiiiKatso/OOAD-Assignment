package bankingsystem.model;

import java.util.HashMap;

public class UserData {

    // Username → Password
    private static final HashMap<String, String> userPasswords = new HashMap<>();

    // Username → Balance
    private static final HashMap<String, Double> userBalances = new HashMap<>();

    // Username → Account Type
    private static final HashMap<String, String> userAccountTypes = new HashMap<>();

    static {
        // Admin creates these
        addCustomer("Katso", "Katso123", 2500.00, "Savings");
        addCustomer("Neo", "Neo123", 8900.50, "Investment");
        addCustomer("Pako", "Pako123", 500.00, "Cheque");
    }

    // Used by admin to add new customers
    public static void addCustomer(String name, String password, double balance, String accountType) {
        userPasswords.put(name, password);
        userBalances.put(name, balance);
        userAccountTypes.put(name, accountType);
    }

    public static boolean checkLogin(String name, String password) {
        return userPasswords.containsKey(name) && userPasswords.get(name).equals(password);
    }

    public static double getBalance(String name) {
        return userBalances.getOrDefault(name, 0.0);
    }

    public static String getAccountType(String name) {
        return userAccountTypes.getOrDefault(name, "Unknown");
    }

    public static HashMap<String, Double> getAllBalances() {
        return userBalances;
    }
}
