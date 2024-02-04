package test.exercise04;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticsImpl implements SlidingWindowStatistics.Statistics {
    private final Queue<Integer> measurements;
    private final Queue<Long> times;
    private final long timeWindow;

    public StatisticsImpl(Queue<Integer> measurements, Queue<Long> times, long timeWindow) {
        this.measurements = measurements;
        this.times = times;
        this.timeWindow = timeWindow;
    }

    @Override
    public Double getMean() {
        long countToSkip = this.times.stream().filter(t -> t < Instant.now().getEpochSecond() - this.timeWindow).count();
        System.out.println(this.measurements
                .stream()
                .skip(countToSkip)
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        return this.measurements
                .stream()
                .skip(countToSkip)
                .mapToInt(Integer::intValue)
                .summaryStatistics()
                .getAverage();
    }

    @Override
    public Integer getMode() {
        long countToSkip = this.times.stream().filter(t -> t < Instant.now().getEpochSecond() - this.timeWindow).count();
        System.out.println(this.measurements
                .stream()
                .skip(countToSkip)
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
        return this.measurements.stream()
                .skip(countToSkip)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    @Override
    public Integer getPctile(int pctile) {
        long countToSkip = this.times.stream().filter(t -> t < Instant.now().getEpochSecond() - this.timeWindow).count();

        long countRemaining = this.measurements.stream().skip(countToSkip).count();
        int index = (int) Math.ceil(pctile / 100.0 * countRemaining);
        if (index == 0) {
            return 0;
        } else {
            System.out.println(this.measurements
                    .stream()
                    .skip(countToSkip)
                    .map(Object::toString)
                    .collect(Collectors.joining(", ")));
            return this.measurements.stream().skip(countToSkip).sorted().toList().get(index - 1);
        }
    }
}
