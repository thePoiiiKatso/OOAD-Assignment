package bankingsystem;

import bankingsystem.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Start with the Login page
            Navigator.init(stage, "Login.fxml", 700, 500);
            stage.setTitle("Lekgwere Banking System - Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
