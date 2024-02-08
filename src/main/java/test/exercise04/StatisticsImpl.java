package test.exercise04;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticsImpl implements SlidingWindowStatistics.Statistics {
    private final Queue<TimestampedMeasure> timestampedMeasures = new LinkedBlockingQueue<>();
    private final long timeWindow;
    private Integer mode;
    private double average;

    private final AtomicBoolean needsUpdate = new AtomicBoolean(true);
    private List<Integer> sortedTimestampedMeasures;

    private final Lock lock = new ReentrantLock();

    public StatisticsImpl(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public void addMeasure(TimestampedMeasure measure) {
        lock.lock();
        try {
            timestampedMeasures.offer(measure);
            needsUpdate.set(true);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Double getMean() {
        if (needsUpdate.get()) {
            update();
        }
        return average;
    }

    @Override
    public Integer getMode() {
        if (needsUpdate.get()) {
            update();
        }
        return mode;
    }

    @Override
    public Integer getPctile(int pctile) {
        if (needsUpdate.get()) {
            update();
        }

        int percentile;
        int index = (int) Math.ceil(pctile / 100.0 * (long) this.timestampedMeasures.size());
        if (index == 0) {
            percentile = 0;
        } else {
            percentile = sortedTimestampedMeasures.get(index - 1);
        }
        return percentile;
    }

    private void update() {
        lock.lock();
        try {
            timestampedMeasures.removeIf(t -> t.timestamp() < Instant.now().getEpochSecond() - this.timeWindow);
            mode = this.timestampedMeasures
                    .stream()
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .map(TimestampedMeasure::measure)
                    .orElse(0);
            sortedTimestampedMeasures = this.timestampedMeasures
                    .stream()
                    .map(TimestampedMeasure::measure)
                    .sorted()
                    .toList();
            average = this.timestampedMeasures
                    .stream()
                    .mapToInt(TimestampedMeasure::measure)
                    .summaryStatistics()
                    .getAverage();
            needsUpdate.set(false);
        } finally {
            lock.unlock();
        }
    }
}
