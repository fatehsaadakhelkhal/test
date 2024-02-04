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
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throttler.notifyWhenCanProceed(this::executeAfterNotification);
        Thread.sleep(15000);
    }

    void executeAfterNotification() {
        // handling remaining
        for(int i = 10; i<15; i++) {
            if(throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                System.out.println("should proceed " + i);
            } else {
                System.out.println("should NOT proceed " + i);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("execute after notification");
        throttler.notifyWhenCanProceed(this::executeAfterNotification);
    }
}