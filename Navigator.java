package bankingsystem.util;

import bankingsystem.dao.CustomerDAO.CustomerRecord;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Centralized utility for JavaFX scene navigation.
 * Supports both normal navigation and controller-based data passing.
 * Automatically injects the current session (if available) into controllers.
 */
public class Navigator {

    private static Stage mainStage;

    // ---------------------------------------------------------------
    // Stage Management
    // ---------------------------------------------------------------

    /** Initializes the main stage and loads the initial FXML file. */
    public static void init(Stage stage, String fxmlFile, double width, double height) {
        mainStage = stage;
        goTo(fxmlFile, width, height);
    }

    /** Allows other classes to manually set the main stage. */
    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    // ---------------------------------------------------------------
    // Basic Navigation
    // ---------------------------------------------------------------

    /** Loads an FXML scene without controller access. */
    public static void goTo(String fxmlFile, double width, double height) {
        try {
            if (mainStage == null)
                throw new IllegalStateException("Main stage not set. Call Navigator.init() first.");

            String normalized = fxmlFile.startsWith("/") ? fxmlFile : "/" + fxmlFile;
            var resource = Navigator.class.getResource(normalized);
            if (resource == null)
                throw new IllegalStateException("FXML not found: " + normalized);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            injectSessionIfAvailable(loader.getController()); // ✅ auto session injection

            Scene scene = new Scene(root, width, height);
            mainStage.setScene(scene);
            mainStage.centerOnScreen();
            mainStage.show();

            System.out.println("✅ Loaded scene: " + normalized);

        } catch (Exception e) {
            System.err.println("❌ Failed to load: " + fxmlFile);
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Navigation failed: " + e.getMessage()).showAndWait();
        }
    }

    // ---------------------------------------------------------------
    // Navigation with Controller Consumer
    // ---------------------------------------------------------------

    /**
     * Loads an FXML scene and gives access to its controller before showing it.
     * Allows passing data (e.g. customer ID) to the controller manually.
     */
    public static <T> void goToWithController(String fxmlFile, double width, double height, Consumer<T> controllerConsumer) {
        try {
            if (mainStage == null)
                throw new IllegalStateException("Main stage not set. Call Navigator.init() first.");

            String normalized = fxmlFile.startsWith("/") ? fxmlFile : "/" + fxmlFile;
            var resource = Navigator.class.getResource(normalized);
            if (resource == null)
                throw new IllegalStateException("FXML not found: " + normalized);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Get controller
            T controller = loader.getController();

            // ✅ Inject session automatically
            injectSessionIfAvailable(controller);

            // ✅ Also run user-supplied consumer callback
            if (controllerConsumer != null && controller != null) {
                controllerConsumer.accept(controller);
            }

            Scene scene = new Scene(root, width, height);
            mainStage.setScene(scene);
            mainStage.centerOnScreen();
            mainStage.show();

            System.out.println("✅ Loaded scene with controller: " + normalized);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Navigation failed: " + e.getMessage()).showAndWait();
        }
    }

    // ---------------------------------------------------------------
    // Session Injection Utility
    // ---------------------------------------------------------------

    /**
     * Automatically passes the current logged-in customer to new controllers.
     * It looks for `setCustomerId(int)` or `setCustomer(CustomerRecord)` methods.
     */
    private static void injectSessionIfAvailable(Object controller) {
        try {
            if (controller == null) return;
            CustomerRecord sessionUser = SessionManager.getCurrentCustomer();
            if (sessionUser == null) return;

            // Try setCustomerId(int)
            for (Method method : controller.getClass().getMethods()) {
                if (method.getName().equals("setCustomerId")
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == int.class) {
                    method.invoke(controller, sessionUser.getId());
                    System.out.println("🔗 Injected session via setCustomerId()");
                    return;
                }

                // Try setCustomer(CustomerRecord)
                if (method.getName().equals("setCustomer")
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0] == CustomerRecord.class) {
                    method.invoke(controller, sessionUser);
                    System.out.println("🔗 Injected session via setCustomer()");
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Failed to inject session: " + e.getMessage());
        }
    }
}
