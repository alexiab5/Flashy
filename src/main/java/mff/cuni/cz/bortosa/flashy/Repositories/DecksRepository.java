package mff.cuni.cz.bortosa.flashy.Repositories;

import mff.cuni.cz.bortosa.flashy.Models.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DecksRepository {
    private final Connection connection;

    public DecksRepository(Connection connection) {
        this.connection = connection;
    }

    // Adds a new deck to the database and returns the generated ID
    public int addDeck(Deck deck) throws SQLException {
        String query = "INSERT INTO decks (name, description) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, deck.getName());
        stmt.setString(2, deck.getDescription());
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            deck.setId(id);
            return id;
        } else {
            throw new SQLException("Failed to add deck, no ID obtained.");
        }
    }

    // Retrieves all decks from the database
    public List<Deck> getAllDecks() throws SQLException {
        List<Deck> decks = new ArrayList<>();
        String query = "SELECT * FROM decks";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            Deck deck = new Deck(id, name, description);
            decks.add(deck);
        }
        return decks;
    }

    public Deck getDeckByName(String name) throws SQLException {
        String query = "SELECT * FROM decks WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String deckName = resultSet.getString("name");
                String description = resultSet.getString("description");

                return new Deck(id, deckName, description);
            } else {
                return null;
            }
        }
    }

    public Deck getDeckById(int deckId) throws SQLException {
        String query = "SELECT * FROM decks WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, deckId);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");

            return new Deck(id, name, description);
        }

        return null;
    }

    // Deletes a deck by ID
    public void deleteDeck(int id) throws SQLException {
        String query = "DELETE FROM decks WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    // Updates an existing deck and returns true if successful
    public boolean updateDeck(Deck deck) throws SQLException {
        String query = "UPDATE decks SET name = ?, description = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, deck.getName());
        stmt.setString(2, deck.getDescription());
        stmt.setInt(3, deck.getId());

        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    }

    public Connection getConnection() {
        return connection;
    }
}
