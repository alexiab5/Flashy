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

/**
 * Manages the scenes of the JavaFX application, allowing for loading, switching,
 * and retrieving scene controllers while handling dependency injection.
 */
public class SceneManager {
    private final Stage primaryStage;
    private final Map<SceneType, Parent> scenes = new HashMap<>();
    private final Map<SceneType, Object> controllers = new HashMap<>();
    private final DependencyInjector injector;

    /**
     * Constructs a SceneManager with the primary stage and a dependency injector.
     * @param primaryStage The primary stage of the application.
     * @param injector     The dependency injector for providing required services.
     */
    public SceneManager(Stage primaryStage, DependencyInjector injector) {
        this.primaryStage = primaryStage;
        this.injector = injector;
    }

    /**
     * Loads and stores an FXML scene based on the provided scene type.
     * @param sceneType The type of scene to be loaded.
     * @throws IOException If loading the FXML file fails.
     */
    public void loadScene(SceneType sceneType) throws IOException {
        Parent root = loadView(sceneType);
        scenes.put(sceneType, root);
    }

    /**
     * Switches the current scene to the specified scene type.
     * If the scene is managed, it triggers the onReloadSceneAction method.
     * @param sceneType The scene type to switch to.
     */
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

    /**
     * Retrieves the controller associated with a given scene type.
     * @param sceneType The scene type for which the controller is requested.
     * @return The controller object or null if not found.
     */
    public Object getController(SceneType sceneType) {
        return controllers.get(sceneType);
    }

    private Parent loadView(SceneType sceneType) throws IOException {
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

    /**
     * Creates a controller instance for the given class and injects necessary dependencies.
     *
     * @param controllerClass The class of the controller to create.
     * @return The instantiated controller object.
     * @throws Exception If instantiation fails.
     */
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

    /**
     * Retrieves the root node of a previously loaded scene.
     * @param sceneType The type of scene to retrieve.
     * @return The root Parent node of the scene, or null if not found.
     */
    public Parent getScene(SceneType sceneType) {
        return scenes.get(sceneType);
    }
}
