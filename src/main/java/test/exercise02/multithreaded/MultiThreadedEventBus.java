package test.exercise02.multithreaded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.BasicEventBus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class MultiThreadedEventBus<E> implements BasicEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedEventBus.class);

    private final BlockingDeque<E> queue = new LinkedBlockingDeque<>();
    protected final Map<Class<? extends E>, Collection<Consumer<E>>> consumerMap = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void publishEvent(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        queue.add(event);
        executor.execute(this::notifySubscribers);
    }

    private void notifySubscribers() {
        try {
            E event = queue.take();
            if (consumerMap.containsKey(event.getClass())) {
                consumerMap.get(event.getClass())
                        .forEach(consumer -> {
                            try {
                                consumer.accept(event);
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
}
