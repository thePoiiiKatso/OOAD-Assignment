package bankingsystem.controllers;

import bankingsystem.model.DatabaseConnection;
import bankingsystem.model.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

/**
 * ✅ AdminApprovalController
 * Admin panel to view, approve, or reject customer account opening requests.
 * Each request row has Approve/Reject buttons in the Action column.
 */
public class AdminApprovalController {

    @FXML private TableView<Request> tblRequests;
    @FXML private TableColumn<Request, String> colCustomerName;
    @FXML private TableColumn<Request, String> colAccountType;
    @FXML private TableColumn<Request, Double> colDeposit;
    @FXML private TableColumn<Request, String> colEmployer;
    @FXML private TableColumn<Request, String> colReason;
    @FXML private TableColumn<Request, String> colStatus;
    @FXML private TableColumn<Request, String> colTimestamp;
    @FXML private TableColumn<Request, Void> colActions;

    // ✅ Back button to return to AdminController
    @FXML private Button btnBack;

    private final ObservableList<Request> requestList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colCustomerName.setCellValueFactory(c -> c.getValue().customerNameProperty());
        colAccountType.setCellValueFactory(c -> c.getValue().accountTypeProperty());
        colDeposit.setCellValueFactory(c -> c.getValue().depositProperty().asObject());
        colEmployer.setCellValueFactory(c -> c.getValue().employerProperty());
        colReason.setCellValueFactory(c -> c.getValue().reasonProperty());
        colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
        colTimestamp.setCellValueFactory(c -> c.getValue().timestampProperty());

        addActionButtons();
        loadRequests();
    }

    /** ✅ Load all requests (Pending, Approved, Rejected) */
    private void loadRequests() {
        requestList.clear();

        String sql = """
            SELECT ar.id, c.name AS customer_name, ar.accountType, ar.depositAmount,
                   ar.employer, ar.status, ar.request_date
            FROM account_requests ar
            JOIN customers c ON ar.customer_id = c.id
            ORDER BY ar.request_date DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                requestList.add(new Request(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("accountType"),
                        rs.getDouble("depositAmount"),
                        rs.getString("employer"),
                        "", // no reason stored yet
                        rs.getString("status"),
                        rs.getString("request_date")
                ));
            }

            tblRequests.setItems(requestList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    /** ✅ Add Approve/Reject buttons dynamically in each Action column cell */
    private void addActionButtons() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox container = new HBox(8, approveBtn, rejectBtn);

            {
                container.setPadding(new Insets(5, 0, 5, 15));
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                rejectBtn.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-font-weight: bold;");

                approveBtn.setOnAction(event -> {
                    Request req = getTableView().getItems().get(getIndex());
                    handleApprove(req);
                });

                rejectBtn.setOnAction(event -> {
                    Request req = getTableView().getItems().get(getIndex());
                    handleReject(req);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    /** ✅ Approve selected request */
    private void handleApprove(Request selected) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert approved account
            PreparedStatement insert = conn.prepareStatement("""
                INSERT INTO accounts (customer_id, accountType, balance)
                VALUES ((SELECT id FROM customers WHERE name = ?), ?, ?)
            """);
            insert.setString(1, selected.getCustomerName());
            insert.setString(2, selected.getAccountType());
            insert.setDouble(3, selected.getDeposit());
            insert.executeUpdate();

            // Update request status
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE account_requests SET status = 'Approved' WHERE id = ?");
            update.setInt(1, selected.getId());
            update.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);

            showAlert(Alert.AlertType.INFORMATION,
                    "✅ Approved",
                    selected.getCustomerName() + "'s " + selected.getAccountType() + " account approved successfully!");
            loadRequests();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    /** ✅ Reject selected request */
    private void handleReject(Request selected) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Request");
        dialog.setHeaderText("Reject " + selected.getCustomerName() + "'s request?");
        dialog.setContentText("Enter reason for rejection:");

        dialog.showAndWait().ifPresent(reason -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE account_requests SET status = 'Rejected', reason = ? WHERE id = ?");
                ps.setString(1, reason);
                ps.setInt(2, selected.getId());
                ps.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION,
                        "❌ Rejected",
                        selected.getCustomerName() + "'s request was rejected for reason: " + reason);
                loadRequests();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        });
    }

    /** 🔙 Back button — return to AdminController (Admin.fxml) */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/bankingsystem/views/Admin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard – Lekgwere Banking System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to return to Admin Dashboard.");
        }
    }

    /** ⚙️ Utility for showing alerts */
    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

