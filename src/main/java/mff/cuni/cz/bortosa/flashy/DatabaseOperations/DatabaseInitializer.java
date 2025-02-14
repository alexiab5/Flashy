package mff.cuni.cz.bortosa.flashy.DatabaseOperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:sqlite:database/flashcards.db";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement stmt = connection.createStatement()) {

            // Create decks table
            String createDecksTable = "CREATE TABLE IF NOT EXISTS decks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "description TEXT)";

            // Create flashcards table
            String createFlashcardsTable = "CREATE TABLE IF NOT EXISTS flashcards ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "question TEXT NOT NULL, "
                    + "answer TEXT NOT NULL, "
                    + "hint TEXT, "
                    + "state TEXT CHECK(state IN ('CREATED','LEARNING','LEARNT','TO_REVIEW')) DEFAULT 'CREATED', "
                    + "difficulty TEXT CHECK(difficulty IN ('EASY','MEDIUM','HARD','DEFAULT')) DEFAULT 'DEFAULT')";

            // Create flashcard_deck table (Many-to-Many Relationship)
            String createFlashcardDeckTable = "CREATE TABLE IF NOT EXISTS flashcard_deck ("
                    + "flashcard_id INTEGER NOT NULL, "
                    + "deck_id INTEGER NOT NULL, "
                    + "PRIMARY KEY (flashcard_id, deck_id), "
                    + "FOREIGN KEY (flashcard_id) REFERENCES flashcards(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE)";

            // Execute table creation
            stmt.execute(createDecksTable);
            stmt.execute(createFlashcardsTable);
            stmt.execute(createFlashcardDeckTable);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
