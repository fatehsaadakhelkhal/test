package test.exercise04.filtering;

import test.exercise04.StatisticsImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringSlidingWindowStatisticsImpl implements FilteringSlidingWindowStatistics {
    private final Queue<Integer> measurements = new LinkedBlockingQueue<>();
    private final Queue<Long> times = new LinkedBlockingQueue<>();
    private final List<Consumer<Statistics>> consumers = new ArrayList<>();
    private final List<Predicate<Statistics>> filters = new ArrayList<>();
    private final Statistics statistics;

    public FilteringSlidingWindowStatisticsImpl(long timeWindow) {
        statistics = new StatisticsImpl(measurements, times, timeWindow);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                            for (int i = 0; i < consumers.size(); i++) {
                                if (filters.get(i).test(getLatestStatistics())) {
                                    consumers.get(i).accept(getLatestStatistics());
                                }
                            }
                        },
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
        filters.add(s -> true);
    }



    @Override
    public Statistics getLatestStatistics() {
        return statistics;
    }

    @Override
    public void subscribeForStatistics(Consumer<Statistics> consumer, Predicate<Statistics> filter) {
        consumers.add(consumer);
        filters.add(filter);
    }
}
