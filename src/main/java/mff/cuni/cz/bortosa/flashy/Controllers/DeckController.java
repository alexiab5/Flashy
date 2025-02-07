package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Utils.AlertDialog;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DeckController implements Initializable, SceneManaged {
    private final FlashcardService flashcardService;
    private SceneManager sceneManager;

    @FXML
    private TextArea nameTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private RadioButton descriptionRadioButton;

    @FXML
    private TextArea descriptionTextField;

    public DeckController(FlashcardService flashcardService){
        this.flashcardService = flashcardService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void onReloadSceneAction() {
    }

    @FXML
    public void onBackButtonAction(javafx.event.ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }

    public void onAddButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            String name = nameTextField.getText().trim();
            String description = descriptionRadioButton.isSelected() ?  descriptionTextField.getText().trim() : null;

            if(name.isEmpty()){
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "The name field cannot be empty!");
                return;
            }

            if (descriptionRadioButton.isSelected() && description.isEmpty()) {
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Please provide the description of the new deck!");
                return;
            }

            Deck newDeck = new Deck(name, description);
            flashcardService.addDeck(newDeck);

            // Clear input fields
            nameTextField.clear();
            descriptionTextField.clear();
            descriptionRadioButton.setSelected(false);

            AlertDialog.show(Alert.AlertType.CONFIRMATION, "Deck added successfully!", null);

        } catch (SQLException e) {
            AlertDialog.show(Alert.AlertType.ERROR, "Error adding deck.", e.getMessage());
        }
    }
}
