package test.exercise04;

import java.util.function.Consumer;

public interface SlidingWindowStatistics {
    void add(int measurement);
    // subscriber will have a callback that'll deliver a Statistics instance (push)
    void subscribeForStatistics(Consumer<Statistics> consumer);
    // get latest statistics (poll)
    Statistics getLatestStatistics();
    interface Statistics {

        Double getMean();
        Integer getMode();
        Integer getPctile(int pctile);
    }
}