package bankingsystem.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ✅ TransactionService
 * Centralized helper for recording all financial transactions.
 * Ensures all deposits, withdrawals, payments, and transfers are
 * consistently logged in the database with proper account linkage.
 *
 * Used by:
 *  - DepositController
 *  - WithdrawController
 *  - PaymentsController
 *  - ViewAccountsController
 */
public class TransactionService {

    /**
     * Records a transaction in the database.
     *
     * @param customerName   The logged-in user's name (Session.currentUserName)
     * @param type           Transaction type: Deposit, Withdrawal, Payment, Transfer, etc.
     * @param reference      Short note or reference for the transaction (e.g. “Electricity Bill”)
     * @param amount         Transaction amount
     * @param fromAccountId  Account the money is sent from (null if not applicable)
     * @param toAccountId    Account the money is sent to (null if not applicable)
     */
    public static void recordTransaction(
            String customerName,
            String type,
            String reference,
            double amount,
            Integer fromAccountId,
            Integer toAccountId
    ) {
        String sql = """
            INSERT INTO transactions (
                customer_id,
                from_account_id,
                to_account_id,
                type,
                reference,
                amount,
                date
            )
            VALUES (
                (SELECT id FROM customers WHERE name = ?),
                ?, ?, ?, ?, ?, datetime('now')
            )
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerName);

            if (fromAccountId != null) ps.setInt(2, fromAccountId);
            else ps.setNull(2, java.sql.Types.INTEGER);

            if (toAccountId != null) ps.setInt(3, toAccountId);
            else ps.setNull(3, java.sql.Types.INTEGER);

            ps.setString(4, type);
            ps.setString(5, reference != null ? reference : "N/A");
            ps.setDouble(6, amount);

            ps.executeUpdate();

            System.out.printf("✅ Transaction recorded: %s | P%.2f | Ref: %s%n",
                    type, amount, reference);

        } catch (SQLException e) {
            System.err.println("❌ TransactionService Error: " + e.getMessage());
        }
    }
}
