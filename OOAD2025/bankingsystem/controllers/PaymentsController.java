package bankingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class PaymentsController {

    @FXML
    private TextField txtRecipient;

    @FXML
    private TextField txtAmount;

    @FXML
    private void handleSendPayment(ActionEvent event) {
        String recipient = txtRecipient.getText().trim();
        String amount = txtAmount.getText().trim();

        if (recipient.isEmpty() || amount.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Info", "Please fill all fields before sending payment.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "You sent P" + amount + " to " + recipient + ".");
        txtRecipient.clear();
        txtAmount.clear();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Dashboard2.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
