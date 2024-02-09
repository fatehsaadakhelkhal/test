package test.exercise04;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StatisticsImpl implements SlidingWindowStatistics.Statistics {
    private final Collection<TimestampedMeasure> measurements;
    public StatisticsImpl(Queue<TimestampedMeasure> measurements) {
        this.measurements = Collections.unmodifiableCollection(measurements);
    }

    @Override
    public Double getMean() {
        return this.measurements
                .stream()
                .mapToInt(TimestampedMeasure::measure)
                .summaryStatistics()
                .getAverage();
    }

    @Override
    public Integer getMode() {
        return this.measurements
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
        int percentile;
        int index = (int) Math.ceil(pctile / 100.0 * (long) this.measurements.size());
        if (index == 0) {
            percentile = 0;
        } else {
            List<Integer> sortedTimestampedMeasures = this.measurements
                    .stream()
                    .map(TimestampedMeasure::measure)
                    .sorted()
                    .toList();
            percentile = sortedTimestampedMeasures.get(index - 1);
        }
        return percentile;
    }
}
