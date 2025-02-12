package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller class for managing the scene responsible for displaying statistics regarding the exising flashcards.
 * Implements {@link Initializable}, {@link SceneManaged}, and {@link Observer} to handle UI initialization and
 * scene management.
 */
public class StatisticsController implements Initializable, SceneManaged {
    public Button exitButtton;
    private SceneManager sceneManager;
    private final FlashcardService flashcardService;

    @FXML
    public PieChart statePieChart;

    @FXML
    public PieChart difficultyPieChart;

    public StatisticsController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updatePieCharts();
    }

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void onReloadSceneAction() {
        updatePieCharts();
    }

    /**
     * Populates the pie charts according to the current statistics (state and difficulty)
     */
    public void updatePieCharts(){
        try {
            // Fetch data for flashcards by state and difficulty
            Map<Flashcard.State, Integer> stateCountMap = flashcardService.getFlashcardsByState();
            Map<Flashcard.Difficulty, Integer> difficultyCountMap = flashcardService.getFlashcardsByDifficulty();

            // Update the state pie chart
            statePieChart.getData().clear();
            for (Map.Entry<Flashcard.State, Integer> entry : stateCountMap.entrySet()) {
                PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
                statePieChart.getData().add(data);
            }

            // Update the difficulty pie chart
            difficultyPieChart.getData().clear();
            for (Map.Entry<Flashcard.Difficulty, Integer> entry : difficultyCountMap.entrySet()) {
                PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
                difficultyPieChart.getData().add(data);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles switching back to the main scene.
     */
    public void onExitButttonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }
}
