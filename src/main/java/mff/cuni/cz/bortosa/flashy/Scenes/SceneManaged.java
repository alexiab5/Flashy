package mff.cuni.cz.bortosa.flashy.Scenes;

/**
 * Interface for controllers that are managed by the SceneManager.
 */
public interface SceneManaged {
    /**
     * Sets the SceneManager for this controller.
     *
     * @param sceneManager The scene manager instance.
     */
    void setSceneManager(SceneManager sceneManager);

    /**
     * Action to be performed when the scene is reloaded.
     */
    void onReloadSceneAction();
}
