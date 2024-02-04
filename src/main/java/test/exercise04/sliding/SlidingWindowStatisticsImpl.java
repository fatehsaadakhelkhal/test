package test.exercise04.sliding;

import test.exercise04.SlidingWindowStatistics;
import test.exercise04.StatisticsImpl;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SlidingWindowStatisticsImpl implements SlidingWindowStatistics {
    private final Queue<Integer> measurements = new LinkedBlockingQueue<>();
    private final Queue<Long> times = new LinkedBlockingQueue<>();
    private final List<Consumer<Statistics>> consumers = new ArrayList<>();
    private final Statistics statistics;
    public SlidingWindowStatisticsImpl(long timeWindow) {
        statistics = new StatisticsImpl(measurements, times, timeWindow);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> consumers.forEach(consumer -> consumer.accept(getLatestStatistics())),
                        timeWindow,
                        timeWindow,
                        TimeUnit.SECONDS);
    }

    @Override
    public void add(int measurement) {
        times.offer(Instant.now().getEpochSecond());
        measurements.add(measurement);
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
