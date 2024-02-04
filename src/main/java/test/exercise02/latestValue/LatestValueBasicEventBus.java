package test.exercise02.latestValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.BasicEventBus;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class LatestValueBasicEventBus<E extends LatestValueBasicEventBus.TimestampedEvent> implements BasicEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(LatestValueBasicEventBus.class);

    private final BlockingDeque<E> queue = new LinkedBlockingDeque<>();
    protected final Map<Class<? extends E>, Collection<Consumer<E>>> consumerMap = new ConcurrentHashMap<>();

    private final AtomicLong latestTimestamp = new AtomicLong(0);
    public LatestValueBasicEventBus() {
        Executors.newSingleThreadExecutor().execute(() -> {
            while(true) {
                try {
                    E event = queue.take();
                    while(event.getTimestamp() < latestTimestamp.get()) {
                        event = queue.take();
                    }
                    E finalEvent = event;
                    consumerMap.getOrDefault(event.getClass(), new ArrayList<>())
                            .forEach(consumer -> consumer.accept(finalEvent));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Override
    public void publishEvent(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        latestTimestamp.set(event.getTimestamp());
        queue.add(event);
    }

    @Override
    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        logger.info("subscribing consumer {}", subscriber);
        consumerMap.computeIfAbsent(clazz, c -> new ArrayList<>()).add(subscriber);
    }

    public interface TimestampedEvent {
        long getTimestamp();
    }
}
