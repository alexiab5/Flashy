package mff.cuni.cz.bortosa.flashy.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Deck {
    private int id;
    private String name;
    private String description;

    // for decks created by the user
    public Deck(String name, String description) {
        this.id = -1;
        this.name = name;
        this.description = description;
    }

    // for decks fetched from the database
    public Deck(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // getters, setters ...
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // !!!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return id == deck.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
