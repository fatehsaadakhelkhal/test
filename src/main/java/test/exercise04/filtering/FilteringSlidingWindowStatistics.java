package test.exercise04.filtering;

import test.exercise04.SlidingWindowStatistics;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface FilteringSlidingWindowStatistics extends SlidingWindowStatistics {
    // subscriber will have a callback that'll deliver a Statistics instance (push)
    void subscribeForStatistics(Consumer<Statistics> consumer, Predicate<Statistics> filter);
}