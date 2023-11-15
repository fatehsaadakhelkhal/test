package test.eventbus;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ProposedEventBus<E> {
    void publishEvent(E o);

    void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber);

    void addSubscriberForFilteredEvents(Class<? extends E> clazz, Consumer<E> subscriber, Predicate<E> filter);
}
