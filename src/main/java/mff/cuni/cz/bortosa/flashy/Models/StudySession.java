package mff.cuni.cz.bortosa.flashy.Models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class StudySession {
    private final int id;
    private final Deck deck;
    private final LocalDateTime sessionDate;

    public StudySession(Deck deck) {
        this.id = -1;
        this.deck = deck;
        this.sessionDate = LocalDateTime.now();
    }

    public StudySession(int id, Deck deck, LocalDateTime sessionDate) {
        this.id = id;
        this.deck = deck;
        this.sessionDate = sessionDate;
    }

    public int getId() {
        return id;
    }

    public Deck getDeck() {
        return deck;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void updateProgress(Flashcard card, boolean knewAnswer) {
        if (knewAnswer) {
            card.updateState(Flashcard.State.LEARNT);
        } else {
            card.updateState(Flashcard.State.LEARNING);
        }
    }

//    public double calculateCompletionPercentage() {
//        int total = deck.getFlashcards().size();
//        long learntCount = progress.entrySet().stream().filter(Map.Entry::getValue).count();
//        return total > 0 ? (double) learntCount / total * 100 : 0;
//    }

    @Override
    public String toString() {
        return "StudySession{" +
                "sessionId=" + id +
                ", deck=" + deck +
                ", sessionDate=" + sessionDate +
                '}';
    }
}