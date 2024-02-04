package test.exercise02;

import java.util.function.Consumer;

public interface BasicEventBus<E> {
    void publishEvent(E event);

    void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber);
}