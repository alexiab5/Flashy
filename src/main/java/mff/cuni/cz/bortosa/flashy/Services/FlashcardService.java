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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Service class for managing flashcards and decks, implementing the Observer pattern.
 * Provides methods to add, update, delete, and export flashcards and decks.
 */
public class FlashcardService implements Subject {
    private final FlashcardsRepository flashcardsRepository;
    private final FlashcardDeckRepository flashcardDeckRepository;
    private final DecksRepository decksRepository;
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a FlashcardService with the required repositories.
     *
     * @param flashcardsRepository       Repository for flashcards.
     * @param flashcardDeckRepository    Repository for flashcard-deck relationships.
     * @param decksRepository            Repository for decks.
     */
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

    /**
     * Adds a flashcard without associating it with a deck.
     *
     * @param flashcard The flashcard to add.
     * @throws SQLException If a database error occurs.
     */
    public void addFlashcardWithoutDeck(Flashcard flashcard) throws SQLException {
        this.flashcardsRepository.addFlashcard(flashcard);
    }

    /**
     * Adds a flashcard and associates it with an existing deck.
     *
     * @param flashcard The flashcard to add.
     * @param deck      The deck to associate the flashcard with.
     * @throws SQLException If a database error occurs.
     */
    public void addFlashcardWithDeck(Flashcard flashcard, Deck deck) throws SQLException {
        int flashcardId = flashcardsRepository.addFlashcard(flashcard);
        flashcardDeckRepository.addFlashcardToDeck(flashcardId, deck.getId());

        // notify observers
        notifyObservers(Event.ADD_FLASHCARD, flashcard);
    }

    /**
     * Adds a flashcard and creates a new deck for it.
     *
     * @param flashcard The flashcard to add.
     * @param newDeck   The new deck to create and associate with the flashcard.
     * @throws SQLException If a database error occurs.
     */
    public void addFlashcardWithNewDeck(Flashcard flashcard, Deck newDeck) throws SQLException {
        int deckId = decksRepository.addDeck(newDeck);
        int flashcardId = flashcardsRepository.addFlashcard(flashcard);
        flashcardDeckRepository.addFlashcardToDeck(flashcardId, deckId);

        // notify observers
        notifyObservers(Event.ADD_FLASHCARD, flashcard);
        notifyObservers(Event.ADD_DECK, newDeck);
    }

    /**
     * Updates an existing flashcard.
     *
     * @param newFlashcard The flashcard to be updated.
     * @throws SQLException If a database error occurs.
     */
    public void updateFlashcard(Flashcard newFlashcard) throws SQLException{
        flashcardsRepository.updateFlashcard(newFlashcard);
        notifyObservers(Event.UPDATE_FLASHCARD, newFlashcard);
    }

    /**
     * Retrieves all flashcards from the repository.
     *
     * @return A list of all flashcards.
     * @throws SQLException If a database error occurs.
     */
    public List<Flashcard> getAllFlashcards() throws SQLException {
        return flashcardsRepository.getAllFlashcards();
    }

    /**
     * Retrieves all decks from the repository.
     *
     * @return A list of all decks.
     * @throws SQLException If a database error occurs.
     */
    public List<Deck> getAllDecks() throws SQLException {
        return decksRepository.getAllDecks();
    }

    /**
     * Retrieves a deck by its name.
     *
     * @param name The name of the deck.
     * @return The corresponding Deck object.
     * @throws SQLException If a database error occurs.
     */
    public Deck getDeckByName(String name) throws SQLException {
        return decksRepository.getDeckByName(name);
    }

    /**
     * Deletes a flashcard and removes its associations with decks.
     *
     * @param flashcardId The ID of the flashcard to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteFlashcardWithDecks(int flashcardId) throws SQLException {
        try (Connection conn = flashcardsRepository.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // Remove associations from flashcard_deck table
            List<Integer> associatedDecks = flashcardDeckRepository.getDecksContainingFlashcard(flashcardId);
            for (int deckId : associatedDecks) {
                flashcardDeckRepository.removeFlashcardFromDeck(flashcardId, deckId);
            }

            Flashcard removedFlashcard = flashcardsRepository.getFlashcardById(flashcardId);

            // Delete the flashcard from the flashcards table
            flashcardsRepository.deleteFlashcard(flashcardId);

            conn.commit(); // Commit transaction

            // Notify observers
            notifyObservers(Event.REMOVE_FLASHCARD, removedFlashcard);
        } catch (SQLException e) {
            // Rollback transaction if an error occurs
            try (Connection conn = flashcardsRepository.getConnection()) {
                conn.rollback();
            }
            throw e;
        }
    }

    /**
     * Adds a new deck to the repository.
     *
     * @param deck The deck to add.
     * @throws SQLException If a database error occurs.
     */
    public void addDeck(Deck deck) throws SQLException {
        this.decksRepository.addDeck(deck);
        notifyObservers(Event.ADD_DECK, deck);
    }

    /**
     * Deletes a deck from the repository.
     *
     * @param deck The deck to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteDeck(Deck deck) throws SQLException{
        this.decksRepository.deleteDeck(deck.getId());
        notifyObservers(Event.REMOVE_DECK, deck);
    }

    /**
     * Deletes a deck by its name.
     *
     * @param deckName The name of the deck to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteDeckByName(String deckName) throws SQLException {
        Deck deck = decksRepository.getDeckByName(deckName);
        this.decksRepository.deleteDeck(deck.getId());
        notifyObservers(Event.REMOVE_DECK, deck);
    }

    /**
     * Exports a deck's flashcards to a CSV file.
     *
     * @param deckName The name of the deck to export.
     * @param fileName The name of the output CSV file.
     * @throws SQLException If a database error occurs.
     * @throws IOException  If a file writing error occurs.
     */
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

    public void importDeckFromCSV(Deck deck, String fileName) throws SQLException, IOException {
        String defaultDirectory = "exports";
        String filePath = Paths.get(defaultDirectory, fileName).toString();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            // Ensure the deck exists in the database, create if not
            int deckId = decksRepository.addDeck(deck);

            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // Skip the header line
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Properly handle commas inside quotes
                if (values.length < 6) continue; // Ensure valid row

                String question = values[1].replace("\"", "").trim();
                String answer = values[2].replace("\"", "").trim();
                String hint = values[3].replace("\"", "").trim();
                String state = values[4].trim();
                String difficulty = values[5].trim();

                // Insert flashcard and get its ID
                int flashcardId = flashcardsRepository.addFlashcard(new Flashcard(-1, question, answer, hint, Flashcard.State.valueOf(state), Flashcard.Difficulty.valueOf(difficulty)));

                // Associate the flashcard with the deck
                this.flashcardDeckRepository.addFlashcardToDeck(flashcardId, deckId);
            }
        }
    }
}

