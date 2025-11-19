package bankingsystem.util;

import bankingsystem.dao.CustomerDAO.CustomerRecord;

/**
 * Stores the logged-in customer's information for reuse across scenes.
 */
public class SessionManager {
    private static CustomerRecord currentCustomer;

    public static void setCurrentCustomer(CustomerRecord customer) {
        currentCustomer = customer;
    }

    public static CustomerRecord getCurrentCustomer() {
        return currentCustomer;
    }

    public static void clear() {
        currentCustomer = null;
    }
}
