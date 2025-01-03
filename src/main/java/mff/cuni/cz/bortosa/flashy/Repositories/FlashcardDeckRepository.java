package mff.cuni.cz.bortosa.flashy.Repositories;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlashcardDeckRepository {
    private final Connection connection;

    public FlashcardDeckRepository(Connection connection) {
        this.connection = connection;
    }

    public void addFlashcardToDeck(int flashcardId, int deckId) throws SQLException {
        String query = "INSERT INTO flashcard_deck (flashcard_id, deck_id) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);
        stmt.setInt(2, deckId);
        stmt.executeUpdate();
    }

    // Remove a flashcard from a deck
    public void removeFlashcardFromDeck(int flashcardId, int deckId) throws SQLException {
        String query = "DELETE FROM flashcard_deck WHERE flashcard_id = ? AND deck_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);
        stmt.setInt(2, deckId);
        stmt.executeUpdate();
    }

    // Get all decks containing a specific flashcard
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

    // Get all flashcards in a specific deck
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
