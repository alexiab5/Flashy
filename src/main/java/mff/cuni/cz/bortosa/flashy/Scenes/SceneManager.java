package mff.cuni.cz.bortosa.flashy.Scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import mff.cuni.cz.bortosa.flashy.Controllers.*;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private final Stage primaryStage;
    private final Map<SceneType, Parent> scenes = new HashMap<>();
    private final Map<SceneType, Object> controllers = new HashMap<>();
    private final DependencyInjector injector;

    public SceneManager(Stage primaryStage, DependencyInjector injector) {
        this.primaryStage = primaryStage;
        this.injector = injector;
    }

    public void loadScene(SceneType sceneType) throws IOException {
        Parent root = loadView(sceneType);
        scenes.put(sceneType, root);
    }

    public void switchTo(SceneType sceneType) {
        Parent sceneRoot = scenes.get(sceneType);
        if (sceneRoot != null) {
            Object controller = controllers.get(sceneType);
            primaryStage.getScene().setRoot(sceneRoot);
            if(controller instanceof SceneManaged) {
                ((SceneManaged) controller).onReloadSceneAction();
            }
        } else {
            System.err.println("Scene not found: " + sceneType);
        }
    }

    public Object getController(SceneType sceneType) {
        return controllers.get(sceneType);
    }

    private Parent loadView(SceneType sceneType) throws IOException {
//        System.out.println(getClass().getResource(sceneType.getPath()));
//        URL resource = getClass().getResource(sceneType.getPath());
//        if (resource != null) {
//            System.out.println("FXML file found: " + resource);
//        } else {
//            System.err.println("FXML file not found!");
//        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneType.getPath()));
        loader.setControllerFactory(controllerClass -> {
            try {
                // Inject dependencies into controllers
                Object controller = createController(controllerClass);
                if (controller instanceof SceneManaged) {
                    ((SceneManaged) controller).setSceneManager(this);
                }
                controllers.put(sceneType, controller);
                return controller;
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate controller: " + controllerClass.getName(), e);
            }
        });
        return loader.load();
    }

    private Object createController(Class<?> controllerClass) throws Exception {
        if (controllerClass == AddingFlashcardController.class) {
            AddingFlashcardController controller = new AddingFlashcardController(injector.getFlashcardService());
            injector.getFlashcardService().addObserver(controller);
            injector.getStudySessionService().addObserver(controller);
            return controller;
        }
        else if (controllerClass == ModifyingFlashcardController.class) {
            ModifyingFlashcardController controller = new ModifyingFlashcardController(injector.getFlashcardService());
            injector.getFlashcardService().addObserver(controller);
            injector.getStudySessionService().addObserver(controller);
            return controller;
        }
        else if (controllerClass == DeckController.class){
            DeckController controller = new DeckController(injector.getFlashcardService());
            return controller;
        }
        else if (controllerClass == MainController.class) {
            MainController controller = new MainController(injector.getFlashcardService());
            injector.getFlashcardService().addObserver(controller);
            injector.getStudySessionService().addObserver(controller);
            return controller;
        }
        else if (controllerClass == StudySessionController.class) {
            StudySessionController controller = new StudySessionController(injector.getStudySessionService());
            return controller;
        }
        else if (controllerClass == StatisticsController.class) {
            StatisticsController controller = new StatisticsController(injector.getFlashcardService());
            return controller;
        }
        return controllerClass.getDeclaredConstructor().newInstance();
    }

    public Parent getScene(SceneType sceneType) {
        return scenes.get(sceneType);
    }
}
