package mff.cuni.cz.bortosa.flashy.Models;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private int id;
    private String name;
    private String description;

    public Deck(String name, String description) {
        this.id = -1;
        this.name = name;
        this.description = description;
    }

    public Deck(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

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

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
