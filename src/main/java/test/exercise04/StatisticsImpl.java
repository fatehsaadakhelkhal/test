package test.exercise04;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticsImpl implements SlidingWindowStatistics.Statistics {
    private final Queue<TimestampedMeasure> timestampedMeasures = new LinkedBlockingQueue<TimestampedMeasure>();
    private final long timeWindow;

    public StatisticsImpl(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    @Override
    public void addMeasure(TimestampedMeasure measure) {
        timestampedMeasures.offer(measure);
    }

    @Override
    public Double getMean() {
        timestampedMeasures.removeIf(t -> t.timestamp() < Instant.now().getEpochSecond() - this.timeWindow);
        return this.timestampedMeasures
                .stream()
                .mapToInt(TimestampedMeasure::measure)
                .summaryStatistics()
                .getAverage();
    }

    @Override
    public Integer getMode() {
        timestampedMeasures.removeIf(t -> t.timestamp() < Instant.now().getEpochSecond() - this.timeWindow);
        System.out.println(this.timestampedMeasures
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        return this.timestampedMeasures
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
    }

    @Override
    public Integer getPctile(int pctile) {
        timestampedMeasures.removeIf(t -> t.timestamp() < Instant.now().getEpochSecond() - this.timeWindow);
        System.out.println(this.timestampedMeasures
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));

        long count = this.timestampedMeasures.size();
        int index = (int) Math.ceil(pctile / 100.0 * count);
        if (index == 0) {
            return 0;
        } else {
            System.out.println(this.timestampedMeasures
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")));
            return this.timestampedMeasures
                    .stream()
                    .map(TimestampedMeasure::measure)
                    .sorted()
                    .toList()
                    .get(index - 1);
        }
    }
}
