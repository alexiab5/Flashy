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

/**
 * Controller class for managing the scene responsible for creating and adding flashcards to decks.
 * Implements {@link Initializable}, {@link SceneManaged}, and {@link Observer} to handle UI initialization,
 * scene management, and event updates respectively.
 */
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

    /**
     * Constructor for AddingFlashcardController.
     * @param flashcardService Service for handling flashcard-related database operations.
     */
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

    /**
     * Handles updates when a deck is added or removed.
     * @param eventType The type of event that occurred.
     * @param data The data associated with the event (e.g., a new or removed deck).
     */
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

    /**
     * Initializes the controller and loads deck data into the combo box.
     * @param url The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDecksComboBox();
        // the text field is visible only when their associated radio buttons are selected
        this.newDeckTextField.setVisible(false);
        this.hintTextArea.setVisible(false);
    }

    /**
     * Sets the scene manager for navigation between scenes.
     * @param sceneManager The scene manager instance.
     */
    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Resets certain UI elements when the scene is reloaded.
     */
    @Override
    public void onReloadSceneAction() {
        this.newDeckTextField.setVisible(false);
        this.hintTextArea.setVisible(false);
    }

    /**
     * Handles the action for the back button, switching back to the main menu scene.
     * @param actionEvent The action event triggered by clicking the button.
     */
    @FXML
    public void onBackButtonAction(javafx.event.ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }

    /**
     * Handles adding a new flashcard to the selected or newly created deck.
     * @param actionEvent The action event triggered by clicking the add button.
     */
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

    /**
     * Toggles the visibility of the hint text area based on radio button selection.
     */
    public void onHintButtonSelectedAction() {
        if(addHintRadioButton.isSelected()){
            hintTextArea.setVisible(true);
        }
        else{
            hintTextArea.setVisible(false);
        }
    }

    /**
     * Toggles the visibility of the new deck text field based on radio button selection.
     */
    public void setCreateNewDeckButtonAction(){
        if(createDeckRadioButton.isSelected()){
            newDeckTextField.setVisible(true);
        }
        else{
            newDeckTextField.setVisible(false);
        }
    }
}
