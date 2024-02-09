package test.exercise04.filtering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.exercise04.TimestampedMeasure;
import test.exercise04.StatisticsImpl;


import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringSlidingWindowStatisticsImpl implements FilteringSlidingWindowStatistics {
    private static final Logger logger = LoggerFactory.getLogger(FilteringSlidingWindowStatisticsImpl.class);

    private final List<ConsumerWithFilter<Statistics>> consumersWithFilters = new CopyOnWriteArrayList<>();
    private Statistics statistics;
    private final long timeWindow;

    private final Queue<TimestampedMeasure> measurements = new LinkedBlockingQueue<>();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final Lock lock = new ReentrantLock();

    public FilteringSlidingWindowStatisticsImpl(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    private void notifySubscribers() {
        consumersWithFilters.forEach(consumerWithFilter -> {
            try {
                for (ConsumerWithFilter<Statistics> consumersWithFilter : consumersWithFilters) {
                    if (consumersWithFilter.filter().test(getLatestStatistics())) {
                        consumersWithFilter.consumer().accept(getLatestStatistics());
                    }
                }
            } catch (Exception e) {
                logger.error("Error happened", e);
            }
        });
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

    @Override
    public void subscribeForStatistics(Consumer<Statistics> consumer) {
        consumersWithFilters.add(new ConsumerWithFilter<>(consumer, s -> true));
    }

    @Override
    public Statistics getLatestStatistics() {
        return statistics;
    }

    @Override
    public void subscribeForStatistics(Consumer<Statistics> consumer, Predicate<Statistics> filter) {
        consumersWithFilters.add(new ConsumerWithFilter<>(consumer, filter));
    }
}
