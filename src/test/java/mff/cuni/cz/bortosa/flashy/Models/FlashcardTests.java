package mff.cuni.cz.bortosa.flashy.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlashcardTests {

    @Test
    void testConstructorWithValidArguments() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        assertEquals(-1, flashcard.getFlashcardId());
        assertEquals("What is the powerhouse of the cell?", flashcard.getQuestion());
        assertEquals("mitochondria", flashcard.getAnswer());
        assertEquals("produce energy", flashcard.getHint());
        assertEquals(Flashcard.State.CREATED, flashcard.getState());
        assertEquals(Flashcard.Difficulty.DEFAULT, flashcard.getDifficulty());
    }

    @Test
    void testConstructorWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Flashcard(null, "Answer", "Hint"));
        assertThrows(IllegalArgumentException.class, () -> new Flashcard("Question", null, "Hint"));
        assertThrows(IllegalArgumentException.class, () -> new Flashcard("", "Answer", "Hint"));
        assertThrows(IllegalArgumentException.class, () -> new Flashcard("Question", "", "Hint"));
    }

    @Test
    void testSetState() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        flashcard.setState(Flashcard.State.LEARNT);
        assertEquals(Flashcard.State.LEARNT, flashcard.getState());
    }

    void testSetDifficulty() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        flashcard.setDifficulty(Flashcard.Difficulty.EASY);
        assertEquals(Flashcard.Difficulty.EASY, flashcard.getDifficulty());
    }

    @Test
    void testIsLearnt() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        assertFalse(flashcard.isLearnt());
        flashcard.setState(Flashcard.State.LEARNT);
        assertTrue(flashcard.isLearnt());
    }

    @Test
    void testGetters() {
        Flashcard flashcard1 = new Flashcard(1, "What is Java?", "A programming language.", "Related to OOP.",
                Flashcard.State.CREATED, Flashcard.Difficulty.EASY);
        Flashcard flashcard2 = new Flashcard(1, "What is Python?", "A programming language.", null,
                Flashcard.State.CREATED, Flashcard.Difficulty.MEDIUM);

        assertEquals(flashcard1, flashcard2);
        assertEquals(flashcard1.hashCode(), flashcard2.hashCode());
    }

    @Test
    void testSetQuestion() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        flashcard.setQuestion("What is the powerhouse of the cell???");
        assertEquals("What is the powerhouse of the cell???", flashcard.getQuestion());
    }

    @Test
    void testSetAnswer() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitocondria", "produce energy");
        flashcard.setAnswer("mitochondria");
        assertEquals("mitochondria", flashcard.getAnswer());
    }

    @Test
    void testSetHint() {
        Flashcard flashcard = new Flashcard("What is the powerhouse of the cell?", "mitochondria", "produce energy");
        flashcard.setHint("idk");
        assertEquals("idk", flashcard.getHint());
    }

    @Test
    void testToString() {
        Flashcard flashcard = new Flashcard("What is Java?", "A programming language.", "Related to OOP.");
        String expected = "Flashcard{Id=-1, question='What is Java?', answer='A programming language.', hint='Related to OOP.', state=CREATED, difficulty=DEFAULT}";
        assertEquals(expected, flashcard.toString());
    }
}
