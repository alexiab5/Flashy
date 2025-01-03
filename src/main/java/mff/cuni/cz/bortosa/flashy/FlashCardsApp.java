package mff.cuni.cz.bortosa.flashy;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mff.cuni.cz.bortosa.flashy.DatabaseOperations.DatabaseManager;
import mff.cuni.cz.bortosa.flashy.Scenes.DependencyInjector;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneManager;
import mff.cuni.cz.bortosa.flashy.Scenes.SceneType;

public class FlashCardsApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager databaseManager = new DatabaseManager(
                "jdbc:mysql://127.0.0.1:3306/flashcards_schema",
                "root",
                "password"
        );
        DependencyInjector injector = new DependencyInjector(databaseManager);
        SceneManager sceneManager = new SceneManager(primaryStage, injector);

        // preloading the scenes
        for (SceneType sceneType : SceneType.values()) {
            sceneManager.loadScene(sceneType);
        }

        primaryStage.setScene(new Scene(sceneManager.getScene(SceneType.MAIN_MENU)));
        primaryStage.setTitle("Flashcard Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
