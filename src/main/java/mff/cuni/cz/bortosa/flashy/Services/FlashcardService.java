package mff.cuni.cz.bortosa.flashy.Services;

import mff.cuni.cz.bortosa.flashy.Controllers.DeckController;
import mff.cuni.cz.bortosa.flashy.Models.Deck;
import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Observer.Event;
import mff.cuni.cz.bortosa.flashy.Observer.Observer;
import mff.cuni.cz.bortosa.flashy.Observer.Subject;
import mff.cuni.cz.bortosa.flashy.Repositories.DecksRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardDeckRepository;
import mff.cuni.cz.bortosa.flashy.Repositories.FlashcardsRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class FlashcardService implements Subject {
    private final FlashcardsRepository flashcardsRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;
    private final DecksRepository decksRepository;
    private final List<Observer> observers = new ArrayList<>();

    public FlashcardService(FlashcardsRepository flashcardsRepository, FlashcardDeckRepository flashcardDeckRepository, DecksRepository decksRepository) {
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

    public void addFlashcardWithoutDeck(Flashcard flashcard) throws SQLException {
        this.flashcardsRepository.addFlashcard(flashcard);
    }

    public void addFlashcardWithDeck(Flashcard flashcard, Deck deck) throws SQLException {
        int flashcardId = flashcardsRepository.addFlashcard(flashcard);
        flashcardDeckRepository.addFlashcardToDeck(flashcardId, deck.getId());

        // notify observers
        notifyObservers(Event.ADD_FLASHCARD, flashcard);
    }

    public void addFlashcardWithNewDeck(Flashcard flashcard, Deck newDeck) throws SQLException {
        int deckId = decksRepository.addDeck(newDeck);
        int flashcardId = flashcardsRepository.addFlashcard(flashcard);
        flashcardDeckRepository.addFlashcardToDeck(flashcardId, deckId);

        // notify observers
        notifyObservers(Event.ADD_FLASHCARD, flashcard);
        notifyObservers(Event.ADD_DECK, newDeck);
    }

    public void updateFlashcard(Flashcard newFlashcard) throws SQLException{
        flashcardsRepository.updateFlashcard(newFlashcard);
        notifyObservers(Event.UPDATE_FLASHCARD, newFlashcard);
    }

    public List<Flashcard> getAllFlashcards() throws SQLException {
        return flashcardsRepository.getAllFlashcards();
    }

    public List<Deck> getAllDecks() throws SQLException {
        return decksRepository.getAllDecks();
    }

    public Deck getDeckByName(String name) throws SQLException {
        return decksRepository.getDeckByName(name);
    }

    public void deleteFlashcardWithDecks(int flashcardId) throws SQLException {
        try {
            // Begin transaction
            flashcardsRepository.getConnection().setAutoCommit(false);

            // Remove associations from flashcard_deck table
            List<Integer> associatedDecks = flashcardDeckRepository.getDecksContainingFlashcard(flashcardId);
            for (int deckId : associatedDecks) {
                flashcardDeckRepository.removeFlashcardFromDeck(flashcardId, deckId);
            }

            Flashcard removedFlashcard = flashcardsRepository.getFlashcardById(flashcardId);
            // Delete the flashcard from the flashcards table
            flashcardsRepository.deleteFlashcard(flashcardId);

            // Commit transaction
            flashcardsRepository.getConnection().commit();

            // notify observers
            notifyObservers(Event.REMOVE_FLASHCARD, removedFlashcard);
        } catch (SQLException e) {
            // Roll back on failure
            flashcardsRepository.getConnection().rollback();
            throw e;
        } finally {
            // Restore auto-commit mode
            flashcardsRepository.getConnection().setAutoCommit(true);
        }
    }

    public void addDeck(Deck deck) throws SQLException {
        this.decksRepository.addDeck(deck);
        notifyObservers(Event.ADD_DECK, deck);
    }

    public void deleteDeck(Deck deck) throws SQLException{
        this.decksRepository.deleteDeck(deck.getId());
        notifyObservers(Event.REMOVE_DECK, deck);
    }

    public void deleteDeckByName(String deckName) throws SQLException {
        Deck deck = decksRepository.getDeckByName(deckName);
        this.decksRepository.deleteDeck(deck.getId());
        notifyObservers(Event.REMOVE_DECK, deck);
    }

    public void exportDeckToCSV(String deckName, String fileName) throws SQLException, IOException {
        int deckID = getDeckByName(deckName).getId();

        String defaultDirectory = "exports";
        if (!fileName.toLowerCase().endsWith(".csv")) {
            fileName += ".csv";
        }

        // Ensure the directory exists
        Files.createDirectories(Paths.get(defaultDirectory));

        // Build the full file path
        String filePath = Paths.get(defaultDirectory, fileName).toString();

        List<Integer> flashcardIds = flashcardDeckRepository.getFlashcardsFromDeck(deckID);
        // Step 2: Open a CSV file for writing
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("ID,Question,Answer,Hint,State,Difficulty\n");

            for (int flashcardId : flashcardIds) {
                Flashcard flashcard = flashcardsRepository.getFlashcardById(flashcardId);
                String hint = (flashcard.getHint() != null) ? flashcard.getHint().replace("\"", "\"\"") : "";
                if (flashcard != null) {
                    writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",%s,%s\n",
                            flashcard.getFlashcardId(),
                            flashcard.getQuestion().replace("\"", "\"\""),
                            flashcard.getAnswer().replace("\"", "\"\""),
                            hint,
                            flashcard.getState(),
                            flashcard.getDifficulty()
                    ));
                }
            }
        }
    }

    public Map<Flashcard.State, Integer> getFlashcardsByState() throws SQLException {
        return flashcardsRepository.getFlashcardsByState();
    }

    public Map<Flashcard.Difficulty, Integer> getFlashcardsByDifficulty() throws SQLException {
        return flashcardsRepository.getFlashcardsByDifficulty();
    }
}

