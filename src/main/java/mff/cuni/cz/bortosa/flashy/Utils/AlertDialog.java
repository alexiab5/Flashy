package mff.cuni.cz.bortosa.flashy.Utils;

import javafx.scene.control.Alert;

/**
 * Alert Dialog utility class
 */
public class AlertDialog {
    /**
     * Displays an Alert Dialog of the provided type, with the provided message and context.
     * @param alertType The type of the alert.
     * @param message The message of the alert.
     * @param context The reason of the alert.
     */
    public static void show(Alert.AlertType alertType, String message, String context) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(message);
        alert.setContentText(context);

        alert.getDialogPane().getScene().getStylesheets().add(AlertDialog.class.getResource("/Fxml/styles.css").toExternalForm());

        alert.show();
    }
}
