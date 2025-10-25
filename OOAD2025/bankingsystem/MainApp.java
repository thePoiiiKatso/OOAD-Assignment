package bankingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
     File fxmlFile = new File("C:/Users/Admin/Desktop/OOAD2025/bankingsystem/views/Login2.fxml");
     URL fxmlUrl = fxmlFile.toURI().toURL();
     Parent root = FXMLLoader.load(fxmlUrl);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Lekgwere Banking System - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
