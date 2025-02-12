package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Event;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Utils.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller class for managing the main scene of the application.
 * This scene is responsible for viewing the available decks together with their description,
 * but it also handles switching to all the other scenes in the application.
 * Implements {@link Initializable}, {@link SceneManaged}, {@link Observer} to handle UI initialization,
 *  * scene management, and event updates respectively.
 */
public class MainController implements Initializable, SceneManaged, Observer {
    private SceneManager sceneManager;
    private final FlashcardService flashcardService;
    private ObservableList<Deck> decks;

    @FXML
    public ListView<Deck> decksListView;

    @FXML
    public Label titleLabel;

    @FXML
    public Button browseFlashcards;

    @FXML
    public Button createFlashcardButton;

    @FXML
    public Button addDeckButton;

    @FXML
    public Button quizModeButton;

    @FXML
    public Button studySessionButton;

    @FXML
    public Button viewStatsButton;

    @FXML
    public Button createQuizButton;

    @FXML
    private ComboBox<Deck> chooseDeckCombobox;

    @FXML
    private Label chooseDeckLabel;

    @FXML
    private ComboBox<String> chooseStudyModeCombobox;

    @FXML
    private Label chooseStudyModeLabel;

    @FXML
    private Label chooseDifficultyLabel;

    @FXML
    private ComboBox<String> chooseDifficultyCombobox;

    @FXML
    public Button deleteDeckButton;

    @FXML
    public Button exitAppButton;

    @FXML
    public Button exportDeckButton;

    private TitledPane currentlyExpandedPane = null; // Track the expanded pane in the decks view

    public MainController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    private void initializeCustomDeckListView() {
        decks = FXCollections.observableArrayList();

        // Custom cell factory to display TitledPane for each deck
        decksListView.setCellFactory(_ -> new ListCell<>() {
            private final TitledPane titledPane = new TitledPane();
            private final Label contentLabel = new Label();
            private final TextField contentTextField = new TextField();
            {
                contentTextField.setEditable(false);
                titledPane.setContent(contentTextField);
                titledPane.setExpanded(false);

                // Handle expanding/collapsing logic
                titledPane.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                    if (isNowExpanded) {
                        // Collapse the previously expanded pane
                        if (currentlyExpandedPane != null && currentlyExpandedPane != titledPane) {
                            currentlyExpandedPane.setExpanded(false);
                        }
                        currentlyExpandedPane = titledPane; // Track new expanded pane
                    } else if (currentlyExpandedPane == titledPane) {
                        currentlyExpandedPane = null; // Reset if collapsed
                    }
                });
            }

            @Override
            protected void updateItem(Deck deck, boolean empty) {
                super.updateItem(deck, empty);
                if (empty || deck == null) {
                    setGraphic(null);
                } else {
                    titledPane.setText(deck.getName());
//                    contentLabel.setText(deck.getDescription());
                    contentTextField.setText(deck.getDescription());
                    setGraphic(titledPane);
                }
            }
        });

        try{
            List<Deck> decksFromDB = flashcardService.getAllDecks();
            decks.addAll(decksFromDB);
            decksListView.setItems(decks);
            decksListView.setOnMousePressed(_ -> decksListView.getSelectionModel().clearSelection());
        }
        catch (SQLException e){
            e.printStackTrace();
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error fetching decks");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        initializeDeckListView();
        initializeCustomDeckListView();
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void onReloadSceneAction() {
    }

    @Override
    public void update(Event eventType, Object data) {
        switch(eventType){
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
     * Switches to the scene responsible for creating and adding a new flashcard to a deck.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    @FXML
    public void onCreateFlashcardsButtonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.ADD_FLASHCARD);
    }

    /**
     * Switches to the scene responsible for viewing/browsing through the available flashcards.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    @FXML
    public void onBrowseButtonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.BROWSE);
    }

    /**
     * Switches to the scene responsible for creating a new deck with description.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    @FXML
    public void onCreateDeckButtonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.ADD_DECK);
    }

    /**
     * Handles creating the dialog necessary for getting the information required when creating a new study session.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    public void onStudySessionAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/EnteringStudySession-View.fxml"));
            loader.setController(this);
            DialogPane studySessionInfoPane = loader.load();

            Dialog<ButtonType> studySessionInfoDialog = new Dialog<>();
            studySessionInfoDialog.setDialogPane(studySessionInfoPane);
            initializeStudySessionInfoDialog();
            Button okButton = (Button) studySessionInfoPane.lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                if (chooseStudyModeCombobox.getSelectionModel().getSelectedItem() == null ||
                        chooseDifficultyCombobox.getSelectionModel().getSelectedItem() == null) {
                    AlertDialog.show(Alert.AlertType.ERROR, "Error", "Please select all the required fields!");
                    event.consume();
                }
            });
            Optional<ButtonType> clickedButton = studySessionInfoDialog.showAndWait();

            if (clickedButton.get() == ButtonType.OK) {
                createStudySession();
            }
        }
        catch(Exception e){
            throw new RuntimeException("Failed to load session info dialog ", e);
        }
    }

    private void initializeStudySessionInfoDialog(){
        ComboBoxUtil.createDecksComboBox(decks, chooseDeckCombobox);

        chooseStudyModeCombobox.getItems().addAll(
                Stream.of(StudySessionController.StudyMode.values())
                        .map(StudySessionController.StudyMode::getName)
                        .toList()
        );

        chooseDifficultyCombobox.getItems().addAll(
                Stream.of(StudySessionController.StudySessionDifficulty.values())
                        .map(StudySessionController.StudySessionDifficulty::name)
                        .toList()
        );
    }

    /**
     * Switches to the scene responsible for the study session.
     */
    private void createStudySession(){
        Deck deck = chooseDeckCombobox.getSelectionModel().getSelectedItem();
        String selectedMode = chooseStudyModeCombobox.getSelectionModel().getSelectedItem();
        String selectedDifficulty = chooseDifficultyCombobox.getSelectionModel().getSelectedItem();

        StudySessionController controller = (StudySessionController) sceneManager.getController(SceneType.STUDY_SESSION);
        controller.setCurrentDeckID(deck.getId());
        //!!
        if(selectedMode == StudySessionController.StudyMode.TO_REVIEW.getName())
            controller.setStudyMode(StudySessionController.StudyMode.TO_REVIEW);
        else
            controller.setStudyMode(StudySessionController.StudyMode.valueOf(selectedMode));
        controller.setStudySessionDifficulty(StudySessionController.StudySessionDifficulty.valueOf(selectedDifficulty));

        sceneManager.switchTo(SceneType.STUDY_SESSION);
    }

    /**
     * Handles deleting a deck form the available list.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    public void onDeleteButtonAction(ActionEvent actionEvent) {
//        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();
        if (currentlyExpandedPane != null) {
            try {
                flashcardService.deleteDeckByName(currentlyExpandedPane.getText().trim());
                AlertDialog.show(Alert.AlertType.CONFIRMATION, "Confirmation", "Deck successfully deleted!");
            } catch (SQLException e) {
                e.printStackTrace();
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error deleting deck");
            }
        } else {
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "No deck selected");
        }
    }

    /**
     * Handles exiting the application.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    public void onExitButtonAction(ActionEvent actionEvent) {
        boolean confirmed = ConfirmationDialog.show("Exit Confirmation", "Unsaved changes may be lost. Are you sure you want to exit?");
        if (confirmed) {
            Platform.exit();
        }
    }

    /**
     * Handles exporting a deck from the available list, to a csv file. It uses a dialog to get the name of the file.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    public void onExportDeckButtonAction(ActionEvent actionEvent) {
//        Deck selectedDeck = decksListView.getSelectionModel().getSelectedItem();

        if(currentlyExpandedPane == null) {
            AlertDialog.show(Alert.AlertType.ERROR, "Error", "No deck selected!");
            return;
        }

        String title = "Export Flashcards";
        String content = "Enter the file name for export:";

        Optional<String> result = GetFileNameDialog.show(title, content);
        result.ifPresent(fileName -> {
            try {
                flashcardService.exportDeckToCSV(currentlyExpandedPane.getText().trim(), fileName.trim());
                AlertDialog.show(Alert.AlertType.INFORMATION, "Confirmation", "Deck successfully exported!");
            } catch(SQLException | IOException e){
                e.printStackTrace();
                AlertDialog.show(Alert.AlertType.ERROR, "Error", "Error exporting deck");
            }
        });
    }

    /**
     * Switches to the scene responsible for viewing the statistics for the current flashcards.
     * @param actionEvent - The action event triggered by clicking the button.
     */
    public void onViewStatsButtonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.STATISTICS);
    }
}
