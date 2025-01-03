package mff.cuni.cz.bortosa.flashy.Utils;

import javafx.scene.control.Alert;

public class AlertDialog {
    public static void show(Alert.AlertType alertType, String message, String context) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(message);
        alert.setContentText(context);

        alert.getDialogPane().getScene().getStylesheets().add(AlertDialog.class.getResource("/Fxml/styles.css").toExternalForm());

        alert.show();
    }
}
