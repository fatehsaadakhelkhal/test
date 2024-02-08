package test.exercise04.filtering;

import org.junit.Test;
import org.mockito.Mockito;
import test.exercise04.SlidingWindowStatistics;
import test.exercise04.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class FilteringSlidingWindowStatisticsImplTest {

    @Test
    public void test_main() {
        FilteringSlidingWindowStatistics stats = new FilteringSlidingWindowStatisticsImpl(5);
        Subscriber subscriber = Mockito.spy(new Subscriber());
        stats.subscribeForStatistics(subscriber::consume, subscriber::filter);
        double mean = 0;
        int mode = 0;
        int percentile = 0;
        for(int i = 0; i < 20; i++) {
            stats.add(i);
            mean = stats.getLatestStatistics().getMean();
            mode = stats.getLatestStatistics().getMode();
            percentile = stats.getLatestStatistics().getPctile(10);
            System.out.println("Mean=" + mean
                    + ", Mode=" + mode
                    + ", Percentile=" + percentile);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        assertEquals(16.5, mean, 0.000001);
        assertEquals(19, mode);
        assertEquals(percentile, 14);
        Mockito.verify(subscriber, Mockito.times(2)).consume(any(SlidingWindowStatistics.Statistics.class));
        Mockito.verify(subscriber, Mockito.times(4)).filter(any(SlidingWindowStatistics.Statistics.class));
    }

    @Test
    public void test_basic() {
        FilteringSlidingWindowStatistics stats = new FilteringSlidingWindowStatisticsImpl(5);
        Subscriber subscriber = Mockito.spy(new Subscriber());
        stats.subscribeForStatistics(subscriber::consume);
        double mean = 0;
        int mode = 0;
        int percentile = 0;
        for(int i = 0; i < 20; i++) {
            stats.add(i);
            mean = stats.getLatestStatistics().getMean();
            mode = stats.getLatestStatistics().getMode();
            percentile = stats.getLatestStatistics().getPctile(10);
            System.out.println("Mean=" + mean
                    + ", Mode=" + mode
                    + ", Percentile=" + percentile);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        assertEquals(16.5, mean, 0.000001);
        assertEquals(19, mode);
        assertEquals(percentile, 14);
        Mockito.verify(subscriber, Mockito.times(4)).consume(any(SlidingWindowStatistics.Statistics.class));
        Mockito.verify(subscriber, Mockito.times(0)).filter(any(SlidingWindowStatistics.Statistics.class));
    }

}