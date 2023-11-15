package test.statistics;

import test.eventbus.MultiThreadedEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class ExampleThrottler<E extends ExampleThrottler.Event> extends MultiThreadedEventBus<E> implements Throttler<E> {
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedEventBus.class);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private AtomicInteger count = new AtomicInteger(0);
    private AtomicInteger sum = new AtomicInteger(0);
    private final int threshold;
    private E lastEvent;

    public ExampleThrottler(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void produce(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        lock.readLock().lock();
        lastEvent = event;
        count.incrementAndGet();
        sum.addAndGet(event.value());
        try {
            if(Double.valueOf(sum.get() / count.get()).intValue() > threshold) {
                super.consumerSet.forEach(consumer -> consumer.accept(event));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ThrottleResult shouldProceed() {
        if(Double.valueOf(sum.get() / count.get()).intValue() > threshold) {
            return ThrottleResult.PROCEED;
        } else {
            return ThrottleResult.DO_NOT_PROCEED;
        }
    }

    @Override
    public void notifyWhenCanProceed(Consumer<E> consumer) {
        if(shouldProceed() == ThrottleResult.PROCEED) {
            consumer.accept(lastEvent);
        }
    }

    public static record Event(int value) {}
}
