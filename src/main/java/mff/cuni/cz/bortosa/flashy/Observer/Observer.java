package mff.cuni.cz.bortosa.flashy.Observer;

import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;

/**
 * Observer interface for implementing the Observer design pattern.
 */
public interface Observer {
    /**
     * Updates the observer based on the given event type and data.
     *
     * @param eventType The type of event that occurred.
     * @param data      The data associated with the event.
     */
    void update(Event eventType, Object data);
}
