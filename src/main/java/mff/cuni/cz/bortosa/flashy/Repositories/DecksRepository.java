package mff.cuni.cz.bortosa.flashy.Repositories;

import mff.cuni.cz.bortosa.flashy.Models.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing the decks in the database.
 * Provides methods to add, remove, and retrieve decks.
 */
public class DecksRepository {
    private final Connection connection;

    public DecksRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Adds a new deck to the database and returns the generated ID.
     *
     * @param deck The deck to be added.
     * @return The generated ID of the newly inserted deck.
     * @throws SQLException If a database error occurs.
     */
    public int addDeck(Deck deck) throws SQLException {
        String query = "INSERT INTO decks (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, deck.getName());
            stmt.setString(2, deck.getDescription());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    deck.setId(id);
                    return id;
                } else {
                    throw new SQLException("Failed to add deck, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves all decks from the database.
     *
     * @return A list of all decks.
     * @throws SQLException If a database error occurs.
     */
    public List<Deck> getAllDecks() throws SQLException {
        String query = "SELECT * FROM decks";
        List<Deck> decks = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                decks.add(new Deck(id, name, description));
            }
        }
        return decks;
    }

    /**
     * Retrieves a deck by its name.
     *
     * @param name The name of the deck.
     * @return The deck if found, otherwise null.
     * @throws SQLException If a database error occurs.
     */
    public Deck getDeckByName(String name) throws SQLException {
        String query = "SELECT * FROM decks WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Deck(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a deck by its ID.
     *
     * @param deckId The ID of the deck.
     * @return The deck if found, otherwise null.
     * @throws SQLException If a database error occurs.
     */
    public Deck getDeckById(int deckId) throws SQLException {
        String query = "SELECT * FROM decks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, deckId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Deck(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Deletes a deck by its ID.
     *
     * @param id The ID of the deck to delete.
     * @throws SQLException If a database error occurs.
     */
    public void deleteDeck(int id) throws SQLException {
        String query = "DELETE FROM decks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing deck's name and description.
     *
     * @param deck The deck with updated information.
     * @return true if the deck was updated successfully, false otherwise.
     * @throws SQLException If a database error occurs.
     */
    public boolean updateDeck(Deck deck) throws SQLException {
        String query = "UPDATE decks SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, deck.getName());
            stmt.setString(2, deck.getDescription());
            stmt.setInt(3, deck.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
