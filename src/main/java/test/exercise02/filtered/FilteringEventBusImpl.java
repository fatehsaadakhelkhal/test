package test.exercise02.filtered;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.EventBusWithFilter;
import test.exercise02.latestValue.LatestValueBasicEventBus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringEventBusImpl<E> implements EventBusWithFilter<E> {
    private static final Logger logger = LoggerFactory.getLogger(FilteringEventBusImpl.class);

    private final Map<Class<? extends E>, Collection<ConsumerWithFilter<E>>> consumersWithFilters = new ConcurrentHashMap<>();

    public void publishEvent(E o) {
        if(consumersWithFilters.containsKey(o.getClass())) {
            consumersWithFilters.get(o.getClass())
                    .stream()
                    .filter(cwf  -> {
                        try {
                            return cwf.filter.test(o);
                        } catch (Exception e) {
                            logger.error("Error happened", e);
                            return false;
                        }
                    })
                    .forEach(cwf -> {
                        try {
                            cwf.consumer.accept(o);
                        } catch (Exception e) {
                            logger.error("Error happened", e);
                        }
                    });
        }
    }

    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        consumersWithFilters.computeIfAbsent(clazz, c -> new CopyOnWriteArrayList<>()).add(new ConsumerWithFilter<>(subscriber, e -> true));
    }

    public void addSubscriberForFilteredEvents(Class<? extends E> clazz, Consumer<E> subscriber, Predicate<E> filter) {
        consumersWithFilters.computeIfAbsent(clazz, c -> new CopyOnWriteArrayList<>()).add(new ConsumerWithFilter<>(subscriber, filter));
    }

    public record ConsumerWithFilter<E>(Consumer<E> consumer, Predicate<E> filter) {}
}
