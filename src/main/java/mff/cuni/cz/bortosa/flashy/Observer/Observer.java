package mff.cuni.cz.bortosa.flashy.Observer;

import mff.cuni.cz.bortosa.flashy.Models.Flashcard;
import mff.cuni.cz.bortosa.flashy.Services.FlashcardService;

public interface Observer {
    void update(Event eventType, Object data);
}
