package test.exercise02.latestValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.BasicEventBus;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class LatestValueBasicEventBus<E extends LatestValueBasicEventBus.TimestampedEvent> implements BasicEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(LatestValueBasicEventBus.class);

    private final BlockingDeque<E> queue = new LinkedBlockingDeque<>();
    protected final Map<Class<? extends E>, Collection<Consumer<E>>> consumerMap = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private final AtomicLong latestTimestamp = new AtomicLong(0);

    @Override
    public void publishEvent(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        latestTimestamp.set(Math.max(event.getTimestamp(), latestTimestamp.get()));
        queue.add(event);
        executor.execute(this::notifySubscribers);
    }

    private void notifySubscribers() {
        try {
            E event = queue.take();
            while (event.getTimestamp() < latestTimestamp.get()) {
                event = queue.take();
            }
            E finalEvent = event;
            if (consumerMap.containsKey(event.getClass())) {
                consumerMap.get(event.getClass())
                        .forEach(consumer -> {
                            try {
                                consumer.accept(finalEvent);
                            } catch (Exception e) {
                                logger.error("Error happened", e);
                            }
                        });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        logger.info("subscribing consumer {}", subscriber);
        consumerMap.computeIfAbsent(clazz, c -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    public interface TimestampedEvent {
        long getTimestamp();
    }
}
