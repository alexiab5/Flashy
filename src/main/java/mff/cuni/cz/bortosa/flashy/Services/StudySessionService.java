package mff.cuni.cz.bortosa.flashy.Services;

import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Event;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Observer.Subject;
import mff.cuni.cz.bortosa.flashy.Repositories.DecksRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardDeckRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardsRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudySessionService implements Subject {
    private final List<Observer> observers = new ArrayList<>();
    private final FlashcardsRepository flashcardsRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;
    private final DecksRepository decksRepository;

    public StudySessionService(FlashcardsRepository flashcardsRepository, FlashcardDeckRepository flashcardDeckRepository, DecksRepository decksRepository) {
        this.flashcardsRepository = flashcardsRepository;
        this.flashcardDeckRepository = flashcardDeckRepository;
        this.decksRepository = decksRepository;
    }

    // Add an observer to the list
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Remove an observer from the list
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Notify all observers that the data has changed
    @Override
    public void notifyObservers(Event eventType, Object data) {
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    public List<Integer> getFlashcardsFromDeck(int deckId) throws SQLException {
        return flashcardDeckRepository.getFlashcardsFromDeck(deckId);
    }

    public Flashcard getFlashcardByID(int flashcardID) throws SQLException {
        return flashcardsRepository.getFlashcardById(flashcardID);
    }

    public void setFlashcardState(Flashcard flashcard, Flashcard.State state) throws SQLException {
        flashcard.setState(state);
        flashcardsRepository.updateFlashcardState(flashcard.getFlashcardId(), state);
        notifyObservers(Event.UPDATE_FLASHCARD, flashcard);
    }

     public void setFlashcardDifficulty(Flashcard flashcard, Flashcard.Difficulty difficulty) throws SQLException {
        flashcard.setDifficulty(difficulty);
        flashcardsRepository.updateFlashcardDifficulty(flashcard.getFlashcardId(), difficulty);
         notifyObservers(Event.UPDATE_FLASHCARD, flashcard);
     }
}
