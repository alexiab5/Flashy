package mff.cuni.cz.bortosa.flashy.Observer;

/**
 * Subject interface for implementing the Observer design pattern.
 */
public interface Subject {
    /**
     * Adds an observer to the subject.
     *
     * @param observer The observer to add.
     */
    void addObserver(Observer observer);

    /**
     * Removes an observer from the subject.
     *
     * @param observer The observer to remove.
     */
    void removeObserver(Observer observer);

    /**
     * Notifies all observers of an event.
     *
     * @param eventType The type of event.
     * @param data      The associated data.
     */
    void notifyObservers(Event eventType, Object data);
}
