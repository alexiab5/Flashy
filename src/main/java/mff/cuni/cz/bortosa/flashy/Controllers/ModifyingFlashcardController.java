package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Event;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Utils.AlertDialog;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Controller class for managing the scene responsible for displaying the existing flashcards, deleting and
 * updating them.
 * Implements {@link Initializable}, {@link SceneManaged}, and {@link Observer} to handle UI initialization,
 * scene management, and event updates respectively.
 */
public class ModifyingFlashcardController implements Initializable, SceneManaged, Observer {
    private final FlashcardService flashcardService;
    private SceneManager sceneManager;

    private ObservableList<Flashcard> flashcards;

    @FXML
    public TableView<Flashcard> flashcardsTableView;

    @FXML
    private TableColumn<Flashcard, String> questionColumn;

    @FXML
    private TableColumn<Flashcard, String> answerColumn;

    @FXML
    private TableColumn<Flashcard, String> hintColumn;

    @FXML
    private TableColumn<Flashcard, String> stateColumn;

    @FXML
    private TableColumn<Flashcard, String> difficultyColumn;

    @FXML
    public Label titleLabel;

    @FXML
    public Button editButton;

    @FXML
    public Button deleteButton;

    @FXML
    public Button backButton;

    @FXML
    private TextArea editAnswerTextArea;

    @FXML
    private ComboBox<String> editDifficultyCombobox;

    @FXML
    private TextArea editHintTextArea;

    @FXML
    private TextArea editQuestionTextArea;

    @FXML
    private ComboBox<String> editStateCombobox;

    public ModifyingFlashcardController(FlashcardService flashcardService){
        this.flashcardService = flashcardService;
    }

    private void initializeFlashcardsTableView(){
        flashcards = FXCollections.observableArrayList();

        // Define the Cell Value Factory for each column
        questionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getQuestion()));

        answerColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAnswer()));

        hintColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHint()));

        stateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getState().toString()));

        difficultyColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDifficulty().toString()));

        // Fetch the initial list of flashcards from the database and populate the table
        try {
            List<Flashcard> flashcardsFromDB = flashcardService.getAllFlashcards();
            flashcards.addAll(flashcardsFromDB);
            flashcardsTableView.setItems(flashcards);
        } catch (SQLException e) {
            e.printStackTrace();
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error fetching flashcards");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeFlashcardsTableView();
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void onReloadSceneAction() {
    }

    /**
     * Handles updates when a flashcard is added, removed or updated.
     * @param eventType The type of event that occurred.
     * @param data The data associated with the event (e.g., a new or removed flashcard).
     */
    @Override
    public void update(Event eventType, Object data) {
        switch (eventType) {
            case ADD_FLASHCARD:
                flashcards.add((Flashcard) data);
                break;
            case REMOVE_FLASHCARD:
                flashcards.remove((Flashcard) data);
                break;
            case UPDATE_FLASHCARD:
                int index = flashcards.indexOf((Flashcard) data);
                if (index != -1) {
                    flashcards.set(index, (Flashcard) data);
                }
                break;
            default:
                break;
        }
    }

    @FXML
    public void onBackButtonAction(javafx.event.ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }

    /**
     * Handles the action of editing a flashcard. It creates a new dialog with all the fields the user
     * can update, already initialized with the flashcard's current information.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    @FXML
    public void onEditButtonAction(javafx.event.ActionEvent actionEvent) {
        Flashcard selectedFlashcard = flashcardsTableView.getSelectionModel().getSelectedItem();
        if (selectedFlashcard == null) {
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "No flashcard selected");
        }
        else{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/EditFlashcard-View.fxml"));
                loader.setController(this);
                DialogPane editFlashcardPane = loader.load();

                Dialog<ButtonType> editFlashcardDialog = new Dialog<>();
                editFlashcardDialog.setDialogPane(editFlashcardPane);
                initializeEditDialogComponents(selectedFlashcard);
                Button applyButton = (Button) editFlashcardPane.lookupButton(ButtonType.APPLY);
                applyButton.addEventFilter(ActionEvent.ACTION, event -> {
                    if (editStateCombobox.getSelectionModel().getSelectedItem() == null ||
                            editDifficultyCombobox.getSelectionModel().getSelectedItem() == null ||
                            editQuestionTextArea.getText().isEmpty() || editAnswerTextArea.getText().isEmpty()) {
                        AlertDialog.show(Alert.AlertType.ERROR, "Error", "Please make sure all required fields are correct!");
                        event.consume();
                    }
                });
                Optional<ButtonType> clickedButton = editFlashcardDialog.showAndWait();

                if (clickedButton.get() == ButtonType.APPLY) {
                    try{
                        updateFlashcardInfo(selectedFlashcard);
                        AlertDialog.show(Alert.AlertType.INFORMATION, "Success", "Flashcard updated successfully");
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                        AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error updating flashcard");
                    }
                }
            }
            catch(Exception e){
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error editing flashcard");
            }
        }
    }

    private void initializeEditDialogComponents(Flashcard flashcard){
        editAnswerTextArea.setText(flashcard.getAnswer());
        editQuestionTextArea.setText(flashcard.getQuestion());
        String hint = flashcard.getHint();
        if(hint != null)
            editHintTextArea.setText(hint);

        editStateCombobox.getItems().addAll(
                Stream.of(Flashcard.State.values())
                        .map(Flashcard.State::name)
                        .toList()
        );
        editStateCombobox.getSelectionModel().select(flashcard.getState().toString());

        editDifficultyCombobox.getItems().addAll(
                Stream.of(Flashcard.Difficulty.values())
                        .map(Flashcard.Difficulty::name)
                        .toList()
        );
        editDifficultyCombobox.getSelectionModel().select(flashcard.getDifficulty().toString());
    }

    private void updateFlashcardInfo(Flashcard flashcard) throws SQLException{
        String newQuestion = editQuestionTextArea.getText();
        String newAnswer = editAnswerTextArea.getText();
        String newHint = editHintTextArea.getText();
        String newState = editStateCombobox.getSelectionModel().getSelectedItem();
        String newDifficulty = editDifficultyCombobox.getSelectionModel().getSelectedItem();

        flashcard.setQuestion(newQuestion);
        flashcard.setAnswer(newAnswer);
        flashcard.setHint(newHint);
        flashcard.setState(Flashcard.State.valueOf(newState));
        flashcard.setDifficulty(Flashcard.Difficulty.valueOf(newDifficulty));

        flashcardService.updateFlashcard(flashcard);
    }

    /**
     * Handles deleting a flashcard from the list.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    @FXML
    public void onDeleteButtonAction(javafx.event.ActionEvent actionEvent) {
        Flashcard selectedFlashcard = flashcardsTableView.getSelectionModel().getSelectedItem();
        if (selectedFlashcard != null) {
            try {
                flashcardService.deleteFlashcardWithDecks(selectedFlashcard.getFlashcardId());
            } catch (SQLException e) {
                e.printStackTrace();
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error deleting flashcard");
            }
        } else {
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "No flashcard selected");
        }
    }

}
