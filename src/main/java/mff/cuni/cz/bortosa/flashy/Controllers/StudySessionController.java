package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Services.StudySessionService;
import mff.cuni.cz.bortosa.flashy.Utils.AlertDialog;
import mff.cuni.cz.bortosa.flashy.Utils.ConfirmationDialog;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller class for managing the scene responsible for handling the study session.
 * It provides a means of going through the filtered list of flashcards in order to learn them,
 * updating the information about their state and difficulty if needed.
 * The user can see the answer for a specific flashcard only by pressing a button. The same goes for seeing a hint.
 * Implements {@link Initializable}, {@link SceneManaged}, and {@link Observer} to handle UI initialization,
 * scene management, and event updates respectively.
 */
public class StudySessionController implements Initializable, SceneManaged {

    public enum StudyMode {
        CREATED("CREATED"),
        LEARNT("LEARNT"),
        LEARNING("LEARNING"),
        TO_REVIEW("TO REVIEW"),
        ALL("ALL");

        private String name;

        StudyMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum StudySessionDifficulty {
        EASY, MEDIUM, HARD, DEFAULT, ALL;
    }

    private final StudySessionService studySessionService;
    private SceneManager sceneManager;
    private int currentDeckID;
    private int currentFlashcardIndex;
    private StudyMode studyMode;
    private StudySessionDifficulty sessionDifficulty;
    private List<Flashcard> currentFlashcards;

    @FXML
    private Label cardDifficultyLabel;

    @FXML
    private RadioButton easyDifficultyRadioButton;

    @FXML
    private RadioButton hardDifficultyRadioButton;

    @FXML
    private RadioButton mediumDifficultyRadioButton;

    @FXML
    private Button nextFlashcardButton;

    @FXML
    private TextArea questionTextArea;

    @FXML
    private Button showAnswerButton;

    @FXML
    public Button exitSessionButton;

    @FXML
    public TextArea answerTextArea;

    @FXML
    public Button showHintButton;

    @FXML
    public ToggleGroup difficulty;

    @FXML
    public TextArea hintTextArea;

    @FXML
    public ToggleGroup answerKnew;

    @FXML
    public RadioButton yesRadioButton;

    @FXML
    public RadioButton noRadioButton;

    @FXML
    public RadioButton toReviewRadioButton;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Resets certain UI elements when a new study session is created, ensuring the user starts seeing
     * the filtered flashcard from the beginning of the selected deck.
     */
    @Override
    public void onReloadSceneAction() {
        try{
            List<Integer> flashcardIds = studySessionService.getFlashcardsFromDeck(currentDeckID);
            List<Flashcard> flashcards = new ArrayList<>();

            // filter flashcards
            for(Integer id : flashcardIds){
                flashcards.add(studySessionService.getFlashcardByID(id));
            }

            if (studyMode == StudyMode.ALL && sessionDifficulty == StudySessionDifficulty.ALL) {
                currentFlashcards = flashcards;
            } else {
                currentFlashcards = flashcards.stream()
                        .filter(flashcard ->
                                (studyMode == StudyMode.ALL || flashcard.getState().name().equals(studyMode.name())) &&
                                        (sessionDifficulty == StudySessionDifficulty.ALL ||
                                                flashcard.getDifficulty().name().equals(sessionDifficulty.name()))
                        )
                        .collect(Collectors.toList());
            }

            // initialize first question text area
            answerTextArea.setText("");
            hintTextArea.setText("");

            if(currentFlashcards.isEmpty()){
                currentFlashcardIndex = -1;
                displayEndOfCardsMessage();
            }
            else{
                currentFlashcardIndex = 0;
                setQuestionTextArea(currentFlashcards.get(0));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayEndOfCardsMessage() {
        questionTextArea.setText("You have reached the end of your cards!");
    }

    private void setQuestionTextArea(Flashcard flashcard) {
        questionTextArea.setText(flashcard.getQuestion());
    }

    private void setAnswerTextArea(Flashcard flashcard) {
        answerTextArea.setText(flashcard.getAnswer());
    }

    public void setCurrentDeckID(int deckID) {
        this.currentDeckID = deckID;
    }

    public void setStudyMode(StudyMode studyMode) {
        this.studyMode = studyMode;
    }

    /**
     * Updates the difficulty of the study session.
     */
    public void setStudySessionDifficulty(StudySessionDifficulty sessionDifficulty) {
        this.sessionDifficulty = sessionDifficulty;
    }

    private void processUserInput(Flashcard flashcard) {
        try{
            // handle flashcard state changed
            if(answerKnew.getSelectedToggle() == yesRadioButton) {
                studySessionService.setFlashcardState(flashcard, Flashcard.State.LEARNT);
            }
            else if(answerKnew.getSelectedToggle() == noRadioButton){
                studySessionService.setFlashcardState(flashcard, Flashcard.State.LEARNING);
            }
            else if (answerKnew.getSelectedToggle() == toReviewRadioButton){
                studySessionService.setFlashcardState(flashcard, Flashcard.State.TO_REVIEW);
            }

            // handle flashcard difficulty changed
            if(difficulty.getSelectedToggle() == easyDifficultyRadioButton){
                studySessionService.setFlashcardDifficulty(flashcard, Flashcard.Difficulty.EASY);
            }
            else if(difficulty.getSelectedToggle() == mediumDifficultyRadioButton){
                studySessionService.setFlashcardDifficulty(flashcard, Flashcard.Difficulty.MEDIUM);
            }
            else if(difficulty.getSelectedToggle() == hardDifficultyRadioButton){
                studySessionService.setFlashcardDifficulty(flashcard, Flashcard.Difficulty.HARD);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "Could not save changes to the flashcard.");
        }
    }

    /**
     * Handles switching to the next flashcard in the deck for the current study session.\
     *  @param actionEvent The action event triggered by clicking the add button.
     */
    public void onNextButtonAction(ActionEvent actionEvent) {
        if(currentFlashcardIndex == -1){
            return;
        }

        processUserInput(currentFlashcards.get(currentFlashcardIndex));

        currentFlashcardIndex++;
        if(currentFlashcardIndex == currentFlashcards.size()){
            currentFlashcardIndex = -1;
            displayEndOfCardsMessage();
            answerTextArea.setText("");
            hintTextArea.setText("");
            resetUserInput();
            return;
        }

        setQuestionTextArea(currentFlashcards.get(currentFlashcardIndex));
        answerTextArea.setText("");
        hintTextArea.setText("");
        resetUserInput();
    }

    private void resetUserInput(){
        answerKnew.selectToggle(null);
        difficulty.selectToggle(null);
    }

    /**
     * Handles switching back to the main scene.
     *  @param actionEvent The action event triggered by clicking the add button.
     */
    public void onExitSessionAction(ActionEvent actionEvent) {
        if(currentFlashcardIndex != -1){
            processUserInput(currentFlashcards.get(currentFlashcardIndex));
        }
        questionTextArea.setText("");
        answerTextArea.setText("");
        hintTextArea.setText("");
        resetUserInput();
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }

    /**
     * Handles displaying the answer for the current flashcard in the study session.
     *  @param actionEvent The action event triggered by clicking the add button.
     */
    public void onShowAnswerAction(ActionEvent actionEvent) {
        if(currentFlashcardIndex == -1){
            return;
        }
        setAnswerTextArea(currentFlashcards.get(currentFlashcardIndex));
    }

    /**
     * Handles displaying the hint for the current flashcard in the study session.
     *  @param actionEvent The action event triggered by clicking the add button.
     */
    public void onShowHintAction(ActionEvent actionEvent) {
        if(currentFlashcardIndex == -1){
            return;
        }

        boolean confirmed = ConfirmationDialog.show(
                "Confirmation",
                "Are you sure you want to see the hint for this card?"
        );

        if (confirmed) {
            String hint = currentFlashcards.get(currentFlashcardIndex).getHint();
            if(hint == null || hint.isEmpty()){
                hintTextArea.setText("There is no hint for this card!");
            }
            else{
                hintTextArea.setText(hint);
            }
        }
    }
}
