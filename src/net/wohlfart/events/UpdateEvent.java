package net.wohlfart.events;

public class UpdateEvent<T> {
    private final T element;

    public UpdateEvent(final T element) {
        this.element = element;
    }

    public T get() {
        return element;
    }
}
