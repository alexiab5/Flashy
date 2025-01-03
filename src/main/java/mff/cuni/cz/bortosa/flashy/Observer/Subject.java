package mff.cuni.cz.bortosa.flashy.Observer;

public interface Subject {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Event eventType, Object data);

}
