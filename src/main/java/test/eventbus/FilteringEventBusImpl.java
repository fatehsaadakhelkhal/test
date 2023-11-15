package test.eventbus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringEventBusImpl<E> implements ProposedEventBus<E> {
    private Map<Class<? extends E>, Set<Consumer<E>>> consumersMap = new HashMap<>();
    private Map<Class<? extends E>, Predicate<E>> filtersMap = new HashMap<>();

    public void publishEvent(E o) {
        if(!filtersMap.containsKey(o.getClass())
        || filtersMap.get(o.getClass()).test(o)) {
            if(consumersMap.containsKey(o.getClass())) {
                consumersMap.get(o.getClass()).forEach(consumer -> consumer.accept(o));
            }
        }
    }

    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        consumersMap.computeIfAbsent(clazz, c -> new HashSet<>()).add(subscriber);
    }

    public void addSubscriberForFilteredEvents(Class<? extends E> clazz, Consumer<E> subscriber, Predicate<E> filter) {
        this.addSubscriber(clazz, subscriber);
        filtersMap.put(clazz, filter);
    }

}
