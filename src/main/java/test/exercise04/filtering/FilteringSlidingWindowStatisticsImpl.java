package test.exercise04.filtering;

import test.exercise04.StatisticsImpl;
import test.exercise04.TimestampedMeasure;

import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FilteringSlidingWindowStatisticsImpl implements FilteringSlidingWindowStatistics {
    private final Queue<TimestampedMeasure> timestampedMeasures = new LinkedBlockingQueue<>();
    private final List<ConsumerWithFilter<Statistics>> consumersWithFilters = new CopyOnWriteArrayList<>();
    private final Statistics statistics;

    public FilteringSlidingWindowStatisticsImpl(long timeWindow) {
        statistics = new StatisticsImpl(timeWindow);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                            for (ConsumerWithFilter<Statistics> consumersWithFilter : consumersWithFilters) {
                                if (consumersWithFilter.filter().test(getLatestStatistics())) {
                                    consumersWithFilter.consumer().accept(getLatestStatistics());
                                }
                            }
                        },
                        timeWindow,
                        timeWindow,
                        TimeUnit.SECONDS);
    }

    @Override
    public void add(int measurement) {
        timestampedMeasures.offer(new TimestampedMeasure(measurement, Instant.now().getEpochSecond()));
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
