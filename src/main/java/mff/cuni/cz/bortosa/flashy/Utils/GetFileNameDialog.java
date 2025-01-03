package mff.cuni.cz.bortosa.flashy.Utils;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;
public class GetFileNameDialog {
    public static Optional<String> show(String title, String content) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        dialog.getDialogPane().getStylesheets().add(GetFileNameDialog.class.getResource("/Fxml/styles.css").toExternalForm());

        TextField textField = new TextField();
        textField.setPromptText("Enter file name (e.g., flashcards.csv)");

        VBox layout = new VBox(10);
        layout.getChildren().add(textField);
        dialog.getDialogPane().setContent(layout);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

        dialog.getDialogPane().lookupButton(okButton).setDisable(true);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            dialog.getDialogPane().lookupButton(okButton).setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(button -> {
            if (button == okButton) {
                return textField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result;
    }
}
