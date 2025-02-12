package mff.cuni.cz.bortosa.flashy.Repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing the relationship between flashcards and decks.
 * Provides methods to add, remove, and retrieve flashcards within decks.
 */
public class FlashcardDeckRepository {
    private final Connection connection;

    public FlashcardDeckRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a flashcard to a deck by inserting an entry into the flashcard_deck table.
     *
     * @param flashcardId The ID of the flashcard to add.
     * @param deckId      The ID of the deck to associate with the flashcard.
     * @throws SQLException If a database error occurs.
     */
    public void addFlashcardToDeck(int flashcardId, int deckId) throws SQLException {
        String query = "INSERT INTO flashcard_deck (flashcard_id, deck_id) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);
        stmt.setInt(2, deckId);
        stmt.executeUpdate();
    }

    /**
     * Removes a flashcard from a deck by deleting its entry from the flashcard_deck table.
     *
     * @param flashcardId The ID of the flashcard to remove.
     * @param deckId      The ID of the deck to remove the flashcard from.
     * @throws SQLException If a database error occurs.
     */
    public void removeFlashcardFromDeck(int flashcardId, int deckId) throws SQLException {
        String query = "DELETE FROM flashcard_deck WHERE flashcard_id = ? AND deck_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);
        stmt.setInt(2, deckId);
        stmt.executeUpdate();
    }

    /**
     * Retrieves a list of deck IDs that contain a specific flashcard.
     *
     * @param flashcardId The ID of the flashcard.
     * @return A list of deck IDs containing the flashcard.
     * @throws SQLException If a database error occurs.
     */
    public List<Integer> getDecksContainingFlashcard(int flashcardId) throws SQLException {
        String query = "SELECT deck_id FROM flashcard_deck WHERE flashcard_id = ?";
        List<Integer> deckIds = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            deckIds.add(rs.getInt("deck_id"));
        }
        return deckIds;
    }

    /**
     * Retrieves a list of flashcard IDs associated with a specific deck.
     *
     * @param deckId The ID of the deck.
     * @return A list of flashcard IDs in the specified deck.
     * @throws SQLException If a database error occurs.
     */
    public List<Integer> getFlashcardsFromDeck(int deckId) throws SQLException {
        String query = "SELECT flashcard_id FROM flashcard_deck WHERE deck_id = ?";
        List<Integer> flashcardIds = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, deckId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            flashcardIds.add(rs.getInt("flashcard_id"));
        }
        return flashcardIds;
    }
}
