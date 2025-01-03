package mff.cuni.cz.bortosa.flashy.Utils;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import mff.cuni.cz.bortosa.flashy.Models.Deck;

public class ComboBoxUtil {
    public static ComboBox<Deck> createDecksComboBox(ObservableList<Deck> decks, ComboBox<Deck> deckComboBox) {
        deckComboBox.setItems(decks);

        deckComboBox.setCellFactory(comboBox -> new ListCell<>() {
            @Override
            protected void updateItem(Deck deck, boolean empty) {
                super.updateItem(deck, empty);
                if (empty || deck == null) {
                    setText(null);
                } else {
                    setText(deck.getName());
                }
            }
        });

        deckComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Deck deck) {
                return deck == null ? "" : deck.getName();
            }

            @Override
            public Deck fromString(String string) {
                return null;
            }
        });

       return deckComboBox;
    }
}
