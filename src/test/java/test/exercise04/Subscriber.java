package test.exercise04;

public class Subscriber {
    public void consume(SlidingWindowStatistics.Statistics s) {
        System.out.println("Consume mean=" + s.getMean() + ", mode=" + s.getMode() + ", percentile=" + s.getPctile(10));
    }

    public boolean filter(SlidingWindowStatistics.Statistics s) {
        System.out.println("Filter mean=" + s.getMean() + ", mode=" + s.getMode() + ", percentile=" + s.getPctile(10));
        return s.getMode() > 10;
    }
}
