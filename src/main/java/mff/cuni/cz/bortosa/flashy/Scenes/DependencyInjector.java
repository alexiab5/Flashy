package mff.cuni.cz.bortosa.flashy.Scenes;

import mff.cuni.cz.bortosa.flashy.DatabaseOperations.DatabaseManager;
import mff.cuni.cz.bortosa.flashy.Repositories.DecksRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardDeckRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardsRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.StudySessionsRepository;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Services.StudySessionService;

import java.sql.*;

/**
 * Handles dependency injection for various services and repositories.
 */
public class DependencyInjector {
    private final DatabaseManager databaseManager;
    private FlashcardsRepository flashcardsRepository;
    private DecksRepository decksRepository;
    private FlashcardDeckRepository flashcardDeckRepository;
    private FlashcardService flashcardService;
    private StudySessionsRepository studySessionsRepository;
    private StudySessionService studySessionService;

    /**
     * Constructor initializing the dependency injector with a database manager.
     *
     * @param databaseManager The database manager instance.
     */
    public DependencyInjector(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Retrieves the flashcards repository, initializing it if necessary.
     * @return The flashcards repository.
     * @throws SQLException If a database error occurs.
     */
    public FlashcardsRepository getFlashcardsRepository() throws SQLException {
        if (flashcardsRepository == null) {
            flashcardsRepository = new FlashcardsRepository(databaseManager.getConnection());
        }
        return flashcardsRepository;
    }

    /**
     * Retrieves the decks repository, initializing it if necessary.
     * @return The decks repository.
     * @throws SQLException If a database error occurs.
     */
    public DecksRepository getDecksRepository() throws SQLException {
        if (decksRepository == null) {
            decksRepository = new DecksRepository(databaseManager.getConnection());
        }
        return decksRepository;
    }

    /**
     * Retrieves the flashcard-deck repository, initializing it if necessary.
     *
     * @return The flashcard-deck repository.
     * @throws SQLException If a database error occurs.
     */
    public FlashcardDeckRepository getFlashcardDeckRepository() throws SQLException {
        if (flashcardDeckRepository == null) {
            flashcardDeckRepository = new FlashcardDeckRepository(databaseManager.getConnection());
        }
        return flashcardDeckRepository;
    }

    /**
     * Retrieves the flashcard service, initializing it if necessary.
     *
     * @return The flashcard service.
     * @throws SQLException If a database error occurs.
     */
    public FlashcardService getFlashcardService() throws SQLException {
        if (flashcardService == null) {
            flashcardService = new FlashcardService(getFlashcardsRepository(), getFlashcardDeckRepository(), getDecksRepository());
        }
        return flashcardService;
    }

    /**
     * Retrieves the study session repository, initializing it if necessary.
     *
     * @return The study session repository.
     * @throws SQLException If a database error occurs.
     */
    public StudySessionsRepository getStudySessionRepository() throws SQLException {
        if (studySessionsRepository == null) {
            studySessionsRepository = new StudySessionsRepository(databaseManager.getConnection());
        }
        return studySessionsRepository;
    }

    /**
     * Retrieves the study session service, initializing it if necessary.
     *
     * @return The study session service.
     * @throws SQLException If a database error occurs.
     */
    public StudySessionService getStudySessionService() throws SQLException {
        if (studySessionService == null) {
            studySessionService = new StudySessionService(getFlashcardsRepository(), getFlashcardDeckRepository(), getDecksRepository());
        }
        return studySessionService;
    }
}
