package mff.cuni.cz.bortosa.flashy.Repositories;

import mff.cuni.cz.bortosa.flashy.Models.Flashcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for managing the flashcards in the database.
 * Provides methods to add, remove, update and retrieve flashcards.
 */
public class FlashcardsRepository {

    private final String dbUrl = "jdbc:sqlite:database/flashcards.db";
    private final Connection connection;

    public FlashcardsRepository(Connection connection) throws SQLException {
        this.connection = connection;
        enableWAL(); // Enable WAL mode to reduce locking issues
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    // Enable WAL mode to improve concurrent reads and writes
    private void enableWAL() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
        }
    }

    // Adds a new flashcard to the database, returns the ID generated automatically by the database
    public int addFlashcard(Flashcard flashcard) throws SQLException {
        String query = "INSERT INTO flashcards (question, answer, hint) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); // Start transaction

            stmt.setString(1, flashcard.getQuestion());
            stmt.setString(2, flashcard.getAnswer());
            stmt.setString(3, flashcard.getHint() != null ? flashcard.getHint() : null);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                flashcard.setId(id);
                conn.commit(); // Commit changes
                return id;
            } else {
                conn.rollback();
                throw new SQLException("Failed to add flashcard, no ID obtained.");
            }
        }
    }

    // Retrieves all flashcards in the database
    public List<Flashcard> getAllFlashcards() throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        String query = "SELECT * FROM flashcards";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String question = resultSet.getString("question");
                String answer = resultSet.getString("answer");
                String hint = resultSet.getString("hint");
                Flashcard.Difficulty difficulty = Flashcard.Difficulty.valueOf(resultSet.getString("difficulty"));
                Flashcard.State state = Flashcard.State.valueOf(resultSet.getString("state"));

                flashcards.add(new Flashcard(id, question, answer, hint, state, difficulty));
            }
        }
        return flashcards;
    }

    // Deletes a flashcard with the specified ID
    public void deleteFlashcard(int id) throws SQLException {
        String query = "DELETE FROM flashcards WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Start transaction
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.commit(); // Commit transaction
        }
    }

    // Retrieves a flashcard with the specified ID
    public Flashcard getFlashcardById(int flashcardId) throws SQLException {
        String query = "SELECT * FROM flashcards WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, flashcardId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Flashcard(
                            resultSet.getInt("id"),
                            resultSet.getString("question"),
                            resultSet.getString("answer"),
                            resultSet.getString("hint"),
                            Flashcard.State.valueOf(resultSet.getString("state")),
                            Flashcard.Difficulty.valueOf(resultSet.getString("difficulty"))
                    );
                }
            }
        }
        return null;
    }

    // Updates a flashcard with the specified ID
    public boolean updateFlashcard(Flashcard flashcard) throws SQLException {
        String query = "UPDATE flashcards SET question = ?, answer = ?, hint = ?, state = ?, difficulty = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Start transaction
            stmt.setString(1, flashcard.getQuestion());
            stmt.setString(2, flashcard.getAnswer());
            stmt.setString(3, flashcard.getHint());
            stmt.setString(4, flashcard.getState().toString());
            stmt.setString(5, flashcard.getDifficulty().toString());
            stmt.setInt(6, flashcard.getFlashcardId());

            int rowsAffected = stmt.executeUpdate();
            conn.commit(); // Commit changes
            return rowsAffected > 0;
        }
    }

    // Updates the state of a flashcard in the database
    public void updateFlashcardState(int id, Flashcard.State newState) throws SQLException {
        String query = "UPDATE flashcards SET state = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);
            stmt.setString(1, newState.name());
            stmt.setInt(2, id);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                conn.rollback();
                throw new SQLException("Failed to update flashcard state: no rows affected.");
            }
            conn.commit();
        }
    }

    // Updates the difficulty of a flashcard in the database
    public void updateFlashcardDifficulty(int id, Flashcard.Difficulty newDifficulty) throws SQLException {
        String query = "UPDATE flashcards SET difficulty = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);
            stmt.setString(1, newDifficulty.name());
            stmt.setInt(2, id);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                conn.rollback();
                throw new SQLException("Failed to update flashcard difficulty: no rows affected.");
            }
            conn.commit();
        }
    }

    // Returns a map where keys are the states of the flashcard and values are the number of flashcards in each state
    public Map<Flashcard.State, Integer> getFlashcardsByState() throws SQLException {
        String query = "SELECT state, COUNT(*) FROM flashcards GROUP BY state";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            Map<Flashcard.State, Integer> stateCountMap = new EnumMap<>(Flashcard.State.class);
            while (resultSet.next()) {
                stateCountMap.put(Flashcard.State.valueOf(resultSet.getString("state")), resultSet.getInt(2));
            }
            return stateCountMap;
        }
    }

    // Returns a map where keys are the difficulties of the flashcard and values are the number of flashcards in each difficulty
    public Map<Flashcard.Difficulty, Integer> getFlashcardsByDifficulty() throws SQLException {
        String query = "SELECT difficulty, COUNT(*) FROM flashcards GROUP BY difficulty";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            Map<Flashcard.Difficulty, Integer> difficultyCountMap = new EnumMap<>(Flashcard.Difficulty.class);
            while (resultSet.next()) {
                difficultyCountMap.put(Flashcard.Difficulty.valueOf(resultSet.getString("difficulty")), resultSet.getInt(2));
            }
            return difficultyCountMap;
        }
    }
}
