package mff.cuni.cz.bortosa.flashy.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;

/**
 * Custom Confirmation Dialog.
 */
public class ConfirmationDialog {
    public static boolean show(String title, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okButton = new ButtonType("OK");
        ButtonType closeButton = new ButtonType("Close");

        alert.getButtonTypes().setAll(okButton, closeButton);

        alert.getDialogPane().getScene().getStylesheets().add(ConfirmationDialog.class.getResource("/Fxml/styles.css").toExternalForm());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == okButton;
    }
}

