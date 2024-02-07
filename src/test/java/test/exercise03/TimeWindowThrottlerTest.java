package test.exercise03;

import org.junit.Test;

public class TimeWindowThrottlerTest {

    private TimeWindowThrottler throttler;

    @Test
    public void test() throws InterruptedException {
        throttler = new TimeWindowThrottler(10, 20);
        for(int i = 0; i< 15; i++) {
            if(throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                System.out.println("should proceed " + i);
            } else {
                System.out.println("should NOT proceed " + i);
                throttler.notifyWhenCanProceed(this::executeAfterNotification);
                break;
            }
        }
        Thread.sleep(1000);
    }

    void executeAfterNotification() {
        // handling remaining
        System.out.println("execute after notification");
    }
}