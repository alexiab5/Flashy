package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Event;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Utils.AlertDialog;
import mff.cuni.cz.bortosa.flashy.Utils.ComboBoxUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddingFlashcardController implements Initializable, SceneManaged, Observer {
    private final FlashcardService flashcardService;
    private SceneManager sceneManager;
    private ObservableList<Deck> decks;

    @FXML
    public Button backButton;

    @FXML
    public Label deckComboboxLabel;

    @FXML
    public RadioButton addHintRadioButton;

    @FXML
    public RadioButton createDeckRadioButton;

    @FXML
    public TextField newDeckTextField;

    @FXML
    public TextArea hintTextArea;

    @FXML
    public Button addFlashcardButton;

    @FXML
    public TextArea questionTextArea;

    @FXML
    public TextArea answerTextArea;

    @FXML
    public ComboBox<Deck> deckCombobox;

    public AddingFlashcardController(FlashcardService flashcardService){
        this.flashcardService = flashcardService;
    }

    private void loadDecksComboBox() {
        decks = FXCollections.observableArrayList();

        try {
            List<Deck> deckList = flashcardService.getAllDecks();
            decks.setAll(deckList);

            ComboBoxUtil.createDecksComboBox(decks, deckCombobox);

        }
        catch (SQLException e) {
            e.printStackTrace();
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error fetching decks.");
        }
    }

    @Override
    public void update(Event eventType, Object data) {
        switch(eventType) {
            case ADD_DECK:
                decks.add((Deck) data);
                break;
            case REMOVE_DECK:
                decks.remove((Deck) data);
                break;
            default:
                break;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDecksComboBox();
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

    @FXML
    public void onAddButtonAction(javafx.event.ActionEvent actionEvent) {
        try {
            String question = questionTextArea.getText().trim();
            String answer = answerTextArea.getText().trim();
            String hint = addHintRadioButton.isSelected() ? hintTextArea.getText().trim() : null;
            Deck selectedDeck = deckCombobox.getSelectionModel().getSelectedItem();

            // Validate inputs
            if (question.isEmpty() || answer.isEmpty()) {
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Question and Answer fields cannot be empty.");
                return;
            }

            if (addHintRadioButton.isSelected() && (hint == null || hint.isEmpty())) {
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Hint is required but empty.");
                return;
            }

            if (createDeckRadioButton.isSelected() && newDeckTextField.getText().trim().isEmpty()){
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Please provide the name of the new deck!");
                return;
            }

            Flashcard flashcard = new Flashcard(question, answer, hint);
            String newDeckName = newDeckTextField.getText().trim();
            if (createDeckRadioButton.isSelected() && !newDeckName.isEmpty()) {
                Deck newDeck = new Deck(newDeckName, "");
                flashcardService.addFlashcardWithNewDeck(flashcard, newDeck);
            } else if (selectedDeck != null) {
                flashcardService.addFlashcardWithDeck(flashcard, selectedDeck);
            } else {
                flashcardService.addFlashcardWithoutDeck(flashcard);
            }

            // Clear input fields
            questionTextArea.clear();
            answerTextArea.clear();
            hintTextArea.clear();
            newDeckTextField.clear();
            deckCombobox.setValue(null);
            addHintRadioButton.setSelected(false);
            createDeckRadioButton.setSelected(false);

            AlertDialog.show(Alert.AlertType.CONFIRMATION, "Flashcard added successfully!", null);
        }
        catch (SQLException e) {
            if (e.getMessage().contains("decks.name_UNIQUE"))
                AlertDialog.show(Alert.AlertType.ERROR, "Error adding flashcard", "The new deck already exists.");
            else
                AlertDialog.show(Alert.AlertType.ERROR, "Error adding flashcard.", e.getMessage());
        }
    }

}
