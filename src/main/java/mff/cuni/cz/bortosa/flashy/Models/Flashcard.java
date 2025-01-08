package mff.cuni.cz.bortosa.flashy.Models;

import java.util.Objects;

public class Flashcard {
    public enum State {
        CREATED, LEARNING, LEARNT, TO_REVIEW
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD, DEFAULT
    }

    private int id;
    private String question;
    private String answer;
    private String hint;
    private State state;
    private Difficulty difficulty;

    // for flashcards created by the user
    public Flashcard(String question, String answer, String hint) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty.");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be null or empty.");
        }
        this.id = -1;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.state = State.CREATED;
        this.difficulty = Difficulty.DEFAULT;
    }

    // for flashcards loaded from the database
    public Flashcard(int id, String question, String answer, String hint, State state, Difficulty difficulty) {
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty.");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be null or empty.");
        }
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.hint = hint;
        this.state = state;
        this.difficulty = difficulty;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getFlashcardId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public State getState() {
        return state;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setState(State newState) {
        this.state = newState;
    }

    public boolean isLearnt() {
        return this.state == State.LEARNT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flashcard flashcard = (Flashcard) obj;
        return id == flashcard.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Flashcard{" +
                "Id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", hint='" + hint + '\'' +
                ", state=" + state +
                ", difficulty=" + difficulty +
                '}';
    }
}
