package test.exercise03;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.stream.IntStream;

public class TimeWindowThrottler2Test {

    private final int[] array = IntStream.rangeClosed(0, 24).toArray();
    private int cursor = 0;
    private TimeWindowThrottler throttler;

    Executor executorMock = Mockito.mock(Executor.class);

    @Test
    public void test() throws InterruptedException {
        throttler = new TimeWindowThrottler(10, 20);
        for(int i = cursor; i < array.length; i++) {
            if(throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                System.out.println("should proceed " + i);
                executorMock.proceed();
                cursor++;
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
        Thread.sleep(30000);
        Mockito.verify(executorMock, Mockito.times(25)).proceed();
    }

    void executeAfterNotification() {
        // handling remaining
        for(int i = cursor; i <array.length; i++) {
            if(throttler.shouldProceed() == Throttler.ThrottleResult.PROCEED) {
                System.out.println("should proceed " + i);
                executorMock.proceed();
                cursor++;
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

    static class Executor {
        public void proceed() {

        }
    }
}