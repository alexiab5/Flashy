package mff.cuni.cz.bortosa.flashy.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManaged;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;

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

    // Method to populate the PieChart with data for flashcard states
    public void updatePieCharts(){
        try {
            // Fetch data for flashcards by state and difficulty
            Map<Flashcard.State, Integer> stateCountMap = flashcardService.getFlashcardsByState();
            Map<Flashcard.Difficulty, Integer> difficultyCountMap = flashcardService.getFlashcardsByDifficulty();

            // Update the state pie chart
            statePieChart.getData().clear();  // Clear any existing data
            for (Map.Entry<Flashcard.State, Integer> entry : stateCountMap.entrySet()) {
                PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
                statePieChart.getData().add(data);
            }

            // Update the difficulty pie chart
            difficultyPieChart.getData().clear();  // Clear any existing data
            for (Map.Entry<Flashcard.Difficulty, Integer> entry : difficultyCountMap.entrySet()) {
                PieChart.Data data = new PieChart.Data(entry.getKey().toString(), entry.getValue());
                difficultyPieChart.getData().add(data);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onExitButttonAction(ActionEvent actionEvent) {
        sceneManager.switchTo(SceneType.MAIN_MENU);
    }
}
