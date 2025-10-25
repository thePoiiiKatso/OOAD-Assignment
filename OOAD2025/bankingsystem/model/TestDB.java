import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        try {
            // Create or open a local database file
            Connection conn = DriverManager.getConnection("jdbc:sqlite:bank_system.db");
            System.out.println("✅ Connected to SQLite!");

            // Create a simple table
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (id INTEGER PRIMARY KEY, name TEXT, balance REAL)");
            System.out.println("✅ Table created successfully!");

            conn.close();
        } catch (Exception e) {
            System.out.println(" " + e.getMessage());
        }
    }
}
