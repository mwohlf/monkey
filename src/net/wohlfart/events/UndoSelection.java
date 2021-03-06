package net.wohlfart.events;

public class UndoSelection<T> {
    private final T element;

    public UndoSelection(final T element) {
        this.element = element;
    }

    public T get() {
        return element;
    }
}
