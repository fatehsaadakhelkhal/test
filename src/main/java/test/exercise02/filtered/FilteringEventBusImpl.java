package test.exercise02.filtered;

import test.exercise02.EventBusWithFilter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringEventBusImpl<E> implements EventBusWithFilter<E> {
    private final Map<Class<? extends E>, Collection<ConsumerWithFilter<E>>> consumersWithFilters = new HashMap<>();

    public void publishEvent(E o) {
        if(consumersWithFilters.containsKey(o.getClass())) {
            consumersWithFilters.getOrDefault(o.getClass(), new HashSet<>())
                    .stream()
                    .filter(cwf  -> cwf.filter.test(o))
                    .forEach(cwf -> cwf.consumer.accept(o));
        }
    }

    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        consumersWithFilters.computeIfAbsent(clazz, c -> new HashSet<>()).add(new ConsumerWithFilter<>(subscriber, e -> true));
    }

    public void addSubscriberForFilteredEvents(Class<? extends E> clazz, Consumer<E> subscriber, Predicate<E> filter) {
        consumersWithFilters.computeIfAbsent(clazz, c -> new HashSet<>()).add(new ConsumerWithFilter<>(subscriber, filter));
    }

    public record ConsumerWithFilter<E>(Consumer<E> consumer, Predicate<E> filter) {}
}
