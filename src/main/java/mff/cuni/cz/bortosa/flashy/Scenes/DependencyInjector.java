package mff.cuni.cz.bortosa.flashy.Scenes;

import mff.cuni.cz.bortosa.flashy.DatabaseOperations.DatabaseManager;
import mff.cuni.cz.bortosa.flashy.Repositories.DecksRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardDeckRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardsRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.StudySessionsRepository;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;
import mff.cuni.cz.bortosa.flashy.Services.StudySessionService;

import java.sql.*;

public class DependencyInjector {
    private final DatabaseManager databaseManager;
    private FlashcardsRepository flashcardsRepository;
    private DecksRepository decksRepository;
    private FlashcardDeckRepository flashcardDeckRepository;
    private FlashcardService flashcardService;
    private StudySessionsRepository studySessionsRepository;
    private StudySessionService studySessionService;

    public DependencyInjector(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public FlashcardsRepository getFlashcardsRepository() throws SQLException {
        if (flashcardsRepository == null) {
            flashcardsRepository = new FlashcardsRepository(databaseManager.getConnection());
        }
        return flashcardsRepository;
    }

    public DecksRepository getDecksRepository() throws SQLException {
        if (decksRepository == null) {
            decksRepository = new DecksRepository(databaseManager.getConnection());
        }
        return decksRepository;
    }

    public FlashcardDeckRepository getFlashcardDeckRepository() throws SQLException {
        if (flashcardDeckRepository == null) {
            flashcardDeckRepository = new FlashcardDeckRepository(databaseManager.getConnection());
        }
        return flashcardDeckRepository;
    }

    public FlashcardService getFlashcardService() throws SQLException {
        if (flashcardService == null) {
            flashcardService = new FlashcardService(getFlashcardsRepository(), getFlashcardDeckRepository(), getDecksRepository());
        }
        return flashcardService;
    }

    public StudySessionsRepository getStudySessionRepository() throws SQLException {
        if (studySessionsRepository == null) {
            studySessionsRepository = new StudySessionsRepository(databaseManager.getConnection());
        }
        return studySessionsRepository;
    }

    public StudySessionService getStudySessionService() throws SQLException {
        if (studySessionService == null) {
            studySessionService = new StudySessionService(getFlashcardsRepository(), getFlashcardDeckRepository(), getDecksRepository());
        }
        return studySessionService;
    }
}
