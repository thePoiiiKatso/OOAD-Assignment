package bankingsystem.model;

public class Session {
    public static String currentUserName;
    public static String currentSurname;
    public static String currentTitle;
    public static boolean isAdmin;
    public static double currentBalance;
    public static String currentAccountType;
    
    // Clear session on logout
    public static void clear() {
        currentUserName = null;
        currentSurname = null;
        currentTitle = null;
        isAdmin = false;
        currentBalance = 0.0;
        currentAccountType = null;
    }
}