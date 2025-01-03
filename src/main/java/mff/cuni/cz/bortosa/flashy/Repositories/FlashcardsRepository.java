package mff.cuni.cz.bortosa.flashy.Repositories;

import mff.cuni.cz.bortosa.flashy.Models.Flashcard;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class FlashcardsRepository {
    private final Connection connection;

    public FlashcardsRepository(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection(){
        return connection;
    }

    // adds a new flashcard to the database, returns the id generated automatically by the database
    public int addFlashcard(Flashcard flashcard) throws SQLException {
        String query = "INSERT INTO flashcards (question, answer, hint) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, flashcard.getQuestion());
        stmt.setString(2, flashcard.getAnswer());
        String hint = flashcard.getHint();
        if(hint != null){
            stmt.setString(3, hint);
        }
        else{
            stmt.setString(3, null);
        }
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1); // Return the generated ID
            flashcard.setId(id);   // !!!!!
            return id;
        } else {
            throw new SQLException("Failed to add flashcard, no ID obtained.");
        }
    }

    public List<Flashcard> getAllFlashcards() throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        String query = "SELECT * FROM flashcards";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(query);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String question = resultSet.getString("question");
            String answer = resultSet.getString("answer");
            String hint = resultSet.getString("hint");
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.valueOf(resultSet.getString("difficulty"));
            Flashcard.State state = Flashcard.State.valueOf(resultSet.getString("state"));

            Flashcard flashcard = new Flashcard(id, question, answer, hint, state, difficulty);
            flashcards.add(flashcard);
        }
        return flashcards;
    }

    public void deleteFlashcard(int id) throws SQLException {
        String query = "DELETE FROM flashcards WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);
        stmt.executeUpdate();
    }

    public Flashcard getFlashcardById(int flashcardId) throws SQLException {
        String query = "SELECT * FROM flashcards WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, flashcardId);  // Set the provided flashcard ID as a parameter

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            int id = resultSet.getInt("id");
            String question = resultSet.getString("question");
            String answer = resultSet.getString("answer");
            String hint = resultSet.getString("hint");
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.valueOf(resultSet.getString("difficulty"));
            Flashcard.State state = Flashcard.State.valueOf(resultSet.getString("state"));

            return new Flashcard(id, question, answer, hint, state, difficulty);
        }

        return null;
    }

    public boolean updateFlashcard(Flashcard flashcard) throws SQLException {
        String query = "UPDATE flashcards " +
                "SET question = ?, answer = ?, hint = ?, state = ?, difficulty = ? " +
                "WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, flashcard.getQuestion());
        stmt.setString(2, flashcard.getAnswer());
        stmt.setString(3, flashcard.getHint());
        stmt.setString(4, flashcard.getState().toString());
        stmt.setString(5, flashcard.getDifficulty().toString());
        stmt.setInt(6, flashcard.getFlashcardId());

        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    }

    // Updates the state of a flashcard in the database
    public void updateFlashcardState(int id, Flashcard.State newState) throws SQLException {
        String query = "UPDATE flashcards SET state = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, newState.name()); // Convert Enum to String
        stmt.setInt(2, id);
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("Failed to update flashcard state: no rows affected.");
        }
    }

    // Updates the difficulty of a flashcard in the database
    public void updateFlashcardDifficulty(int id, Flashcard.Difficulty newDifficulty) throws SQLException {
        String query = "UPDATE flashcards SET difficulty = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, newDifficulty.name()); // Convert Enum to String
        stmt.setInt(2, id);
        int rowsUpdated = stmt.executeUpdate();
        if (rowsUpdated == 0) {
            throw new SQLException("Failed to update flashcard difficulty: no rows affected.");
        }
    }

    // returns a map where keys are the states of the flashcard and values are the number
    // of flashcards in the database having that state
    public Map<Flashcard.State, Integer> getFlashcardsByState() throws SQLException {
        String query = "SELECT state, COUNT(*) FROM flashcards GROUP BY state";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        Map<Flashcard.State, Integer> stateCountMap = new EnumMap<>(Flashcard.State.class);
        while (resultSet.next()) {
            Flashcard.State state = Flashcard.State.valueOf(resultSet.getString("state"));
            int count = resultSet.getInt(2);
            stateCountMap.put(state, count);
        }
        return stateCountMap;
    }

    // returns a map where keys are the difficulties of the flashcard and values are the number
    // of flashcards in the database having that difficulty
    public Map<Flashcard.Difficulty, Integer> getFlashcardsByDifficulty() throws SQLException {
        String query = "SELECT difficulty, COUNT(*) FROM flashcards GROUP BY difficulty";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        Map<Flashcard.Difficulty, Integer> difficultyCountMap = new EnumMap<>(Flashcard.Difficulty.class);
        while (resultSet.next()) {
            Flashcard.Difficulty difficulty = Flashcard.Difficulty.valueOf(resultSet.getString("difficulty"));
            int count = resultSet.getInt(2);
            difficultyCountMap.put(difficulty, count);
        }
        return difficultyCountMap;
    }
}
