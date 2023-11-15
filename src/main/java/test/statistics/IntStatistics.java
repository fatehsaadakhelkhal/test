package test.statistics;

import java.util.Collection;

public class IntStatistics {
    private IntStatistics() {

    }

    public static long sum(Collection<Integer> sequence) {
        return sequence.stream().mapToInt(Integer::intValue).summaryStatistics().getSum();
    }

    public static long max(Collection<Integer> sequence) {
        return sequence.stream().mapToInt(Integer::intValue).summaryStatistics().getMax();
    }

    public static long min(Collection<Integer> sequence) {
        return sequence.stream().mapToInt(Integer::intValue).summaryStatistics().getMin();
    }

    public static double average(Collection<Integer> sequence) {
        return sequence.stream().mapToInt(Integer::intValue).summaryStatistics().getAverage();
    }

    public static long count(Collection<Integer> sequence) {
        return sequence.stream().mapToInt(Integer::intValue).summaryStatistics().getCount();
    }
}
