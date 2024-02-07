package test.exercise04.sliding;

import test.exercise04.SlidingWindowStatistics;
import test.exercise04.StatisticsImpl;
import test.exercise04.TimestampedMeasure;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class SlidingWindowStatisticsImpl implements SlidingWindowStatistics {
    private final List<Consumer<Statistics>> consumers = new CopyOnWriteArrayList<>();
    private final Statistics statistics;
    public SlidingWindowStatisticsImpl(long timeWindow) {
        statistics = new StatisticsImpl(timeWindow);
    }

    @Override
    public void add(int measurement) {
        statistics.addMeasure(new TimestampedMeasure(measurement, Instant.now().getEpochSecond()));
        consumers.forEach(c -> c.accept(statistics));
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
