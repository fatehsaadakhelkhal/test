package test.eventbus;

import java.util.function.Consumer;

public interface EventBus<E> {
    void produce(E event);

    void subscribe(Consumer<E> consumer);

    void unsubscribe(Consumer<E> consumer);
}
