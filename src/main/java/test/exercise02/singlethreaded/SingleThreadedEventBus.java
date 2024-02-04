package test.exercise02.singlethreaded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.BasicEventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SingleThreadedEventBus<E> implements BasicEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(SingleThreadedEventBus.class);

    private final Map<Class<? extends E>, Collection<Consumer<E>>> consumerMap = new HashMap<>();

    @Override
    public void publishEvent(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        consumerMap.computeIfAbsent((Class<? extends E>) event.getClass(), c -> new ArrayList<>())
                .forEach(consumer -> consumer.accept(event));
    }

    @Override
    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        logger.info("subscribing consumer {}", subscriber);
        consumerMap.computeIfAbsent(clazz, c -> new ArrayList<>()).add(subscriber);
    }
}
