package mff.cuni.cz.bortosa.flashy;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mff.cuni.cz.bortosa.flashy.DatabaseOperations.DatabaseManager;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Scenes.DependencyInjector;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;

public class FlashCardsApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager databaseManager = new DatabaseManager();
        DependencyInjector injector = new DependencyInjector(databaseManager);
        SceneManager sceneManager = new SceneManager(primaryStage, injector);

        // preloading the scenes
        for (SceneType sceneType : SceneType.values()) {
            sceneManager.loadScene(sceneType);
        }

//        FlashcardService serv = injector.getFlashcardService();
//        serv.importDeckFromCSV(new Deck("Harry Potter French", "Phrases from the first chapter of Harry Potter à l'École des Sorciers by J.K. Rowling, with their translation in English"), "harry.csv");
//        serv.importDeckFromCSV(new Deck("Advanced French Vocab", "A selection of advanced French vocabulary. As a hint, an example of the word used in a specific context is given."), "advanced.csv");
//        serv.importDeckFromCSV(new Deck("Upper Limb Anatomy", "A set of questions from upper limb anatomy"), "anat.csv");
//        serv.importDeckFromCSV(new Deck("Microbiology", "This set of flashcards focuses on key concepts and definitions from Microbiology."), "microb.csv");
//        serv.importDeckFromCSV(new Deck("German A1", "A selection of useful phrases in German level A1."), "german.csv");

        primaryStage.setScene(new Scene(sceneManager.getScene(SceneType.MAIN_MENU)));
        primaryStage.setTitle("Flashcard Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
