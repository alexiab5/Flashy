package mff.cuni.cz.bortosa.flashy.Utils;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import mff.cuni.cz.bortosa.flashy.Models.Deck;

/**
 * Combo Box utility class
 */
public class ComboBoxUtil {

    /**
     * Creates a custom Combo Box for decks, displaying only their names.
     * @param decks - the observable list of decks that the ComboBox will be initialized with
     * @param deckComboBox - the combobox to be initialized
     * @return
     */
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
