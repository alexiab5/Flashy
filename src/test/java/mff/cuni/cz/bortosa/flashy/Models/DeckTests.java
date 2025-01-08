package mff.cuni.cz.bortosa.flashy.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTests {

    @Test
    void testConstructorWithValidArguments() {
        Deck deck = new Deck("Science", "A deck of science flashcards");
        assertEquals("Science", deck.getName());
        assertEquals("A deck of science flashcards", deck.getDescription());
        assertEquals(-1, deck.getId());
    }

    @Test
    void testConstructorWithId() {
        Deck deck = new Deck(1, "History", "A deck of history flashcards");
        assertEquals(1, deck.getId());
        assertEquals("History", deck.getName());
        assertEquals("A deck of history flashcards", deck.getDescription());
    }

    @Test
    void testSettersAndGetters() {
        Deck deck = new Deck("Science", "A deck of science flashcards");
        deck.setId(2);
        deck.setName("Math");
        deck.setDescription("A deck of math flashcards");

        assertEquals(2, deck.getId());
        assertEquals("Math", deck.getName());
        assertEquals("A deck of math flashcards", deck.getDescription());
    }

    @Test
    void testToString() {
        Deck deck = new Deck(1, "Science", "A deck of science flashcards");
        String expected = "Deck{deckId=1, name='Science'}";
        assertEquals(expected, deck.toString());
    }
}
