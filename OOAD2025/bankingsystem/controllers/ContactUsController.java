package bankingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class ContactUsController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextArea txtMessage;

    // Handle the Send button
    @FXML
    private void handleSend(ActionEvent event) {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String message = txtMessage.getText().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all fields before sending your message.");
            return;
        }

        // Simulate message submission
        showAlert(Alert.AlertType.INFORMATION, "Message Sent",
                "Thank you " + name + ", your message has been sent!\nWe'll respond to " + email + " soon.");

        txtName.clear();
        txtEmail.clear();
        txtMessage.clear();
    }

    // Handle the Back button
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

    // Helper popup
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
