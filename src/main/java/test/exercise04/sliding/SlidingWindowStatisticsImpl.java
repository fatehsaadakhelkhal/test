package test.exercise04.sliding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise04.SlidingWindowStatistics;
import test.exercise04.StatisticsImpl;
import test.exercise04.TimestampedMeasure;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class SlidingWindowStatisticsImpl implements SlidingWindowStatistics {
    private static final Logger logger = LoggerFactory.getLogger(SlidingWindowStatisticsImpl.class);

    private final long timeWindow;
    private final Queue<TimestampedMeasure> measurements = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<Consumer<Statistics>> consumers = new CopyOnWriteArrayList<>();
    private Statistics statistics;

    private final Lock lock = new ReentrantLock();
    public SlidingWindowStatisticsImpl(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public void add(int measurement) {
        lock.lock();
        try {
            long now = Instant.now().getEpochSecond();
            measurements.removeIf(m -> m.timestamp() < now - timeWindow);
            measurements.offer(new TimestampedMeasure(measurement, now));
            statistics = new StatisticsImpl(measurements);
            executorService.execute(this::notifySubscribers);
        } finally {
            lock.unlock();
        }
    }

    private void notifySubscribers() {
        consumers.forEach(consumer -> {
            try {
                consumer.accept(getLatestStatistics());
            } catch (Exception e) {
                logger.error("Error happened", e);
            }
        });
    }

    @Override
    public void subscribeForStatistics(Consumer<Statistics> consumer) {
        consumers.add(consumer);
    }

    @Override
    public Statistics getLatestStatistics() {
        return statistics;
    }
}
