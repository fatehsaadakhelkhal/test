package test.exercise02.multithreaded;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise02.BasicEventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class MultiThreadedEventBus<E> implements BasicEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedEventBus.class);

    private final BlockingDeque<E> queue = new LinkedBlockingDeque<>();
    protected final Map<Class<? extends E>, Collection<Consumer<E>>> consumerMap = new ConcurrentHashMap<>();

    public MultiThreadedEventBus() {
        Executors.newSingleThreadExecutor().execute(() -> {
            while(true) {
                try {
                    E event = queue.take();
                    consumerMap.getOrDefault(event.getClass(), new ArrayList<>())
                            .forEach(consumer -> consumer.accept(event));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    @Override
    public void publishEvent(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        queue.add(event);
    }

    @Override
    public void addSubscriber(Class<? extends E> clazz, Consumer<E> subscriber) {
        logger.info("subscribing consumer {}", subscriber);
        consumerMap.computeIfAbsent(clazz, c -> new ArrayList<>()).add(subscriber);
    }
}