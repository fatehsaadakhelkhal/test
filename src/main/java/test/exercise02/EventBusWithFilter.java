package test.exercise02;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface EventBusWithFilter<E> extends BasicEventBus<E> {
    void publishEvent(E event);

    void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber);

    void addSubscriberForFilteredEvents(Class<? extends E> clazz, Consumer<E> subscriber, Predicate<E> filter);
}
