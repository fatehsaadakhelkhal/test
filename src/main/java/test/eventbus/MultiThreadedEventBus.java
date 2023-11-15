package test.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MultiThreadedEventBus<E> implements EventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedEventBus.class);

    protected final Set<Consumer<E>> consumerSet = ConcurrentHashMap.newKeySet();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void produce(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        lock.readLock().lock();
        try {
            consumerSet.forEach(consumer -> consumer.accept(event));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void subscribe(Consumer<E> consumer) {
        logger.info("subscribing consumer {}", consumer);
        lock.writeLock().lock();
        try {
            consumerSet.add(consumer);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void unsubscribe(Consumer<E> consumer) {
        logger.info("unsubscribing consumer {}", consumer);
        lock.writeLock().lock();
        try {
            consumerSet.remove(consumer);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
