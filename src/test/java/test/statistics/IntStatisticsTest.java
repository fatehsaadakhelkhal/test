package test.statistics;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

public class IntStatisticsTest extends TestCase {
    @Test
    public void testSum() {
        assertEquals(6, IntStatistics.sum(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testMax() {
        assertEquals(3, IntStatistics.max(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testMin() {
        assertEquals(1, IntStatistics.min(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void testAverage() {
        assertEquals(2.0, IntStatistics.average(Arrays.asList(1, 2, 3)), 0.000001);
    }

    @Test
    public void testCount() {
        assertEquals(3, IntStatistics.count(Arrays.asList(1, 2, 3)));
    }
}