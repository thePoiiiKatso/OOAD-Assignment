package bankingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

    @FXML
    private void handleWithdraw(ActionEvent event) {
        loadScene(event, "/bankingsystem/views/Withdraw.fxml", "Withdraw Funds - Lekgwere Banking System");
    }

    @FXML
    private void handleViewAccounts(ActionEvent event) {
        loadScene(event, "/bankingsystem/views/ViewAccounts.fxml", "View Accounts - Lekgwere Banking System");
    }

    @FXML
    private void handleMakePayments(ActionEvent event) {
        loadScene(event, "/bankingsystem/views/payments.fxml", "Make Payments - Lekgwere Banking System");
    }

    @FXML
    private void handleDeposit(ActionEvent event) {
        loadScene(event, "/bankingsystem/views/Deposit.fxml", "Deposit Funds - Lekgwere Banking System");
    }

    @FXML
    private void handleViewTransactions(ActionEvent event) {
        // ✅ Corrected spelling of file name
        loadScene(event, "/bankingsystem/views/ViewTransactions.fxml", "View Transactions - Lekgwere Banking System");
    }

    @FXML
    private void handleHelpDesk(ActionEvent event) {
        loadScene(event, "/bankingsystem/views/contactus.fxml", "Help Desk - Lekgwere Banking System");
    }

    // ✅ Helper method for scene switching
    private void loadScene(ActionEvent event, String fxmlPath, String title) {
        try {
            // Normalize path to avoid missing slash errors
            String normalizedPath = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
            System.out.println("Trying to load: " + normalizedPath);

            Parent root = FXMLLoader.load(getClass().getResource(normalizedPath));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Error loading: " + fxmlPath);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("⚠️ Could not find FXML file: " + fxmlPath);
        }
    }
}
