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
                throttler.notifyWhenCanProceed(this::executeAfterNotification);
                System.out.println("should NOT proceed " + i);
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Thread.sleep(3000);
        Mockito.verify(executorMock, Mockito.times(10)).proceed();
    }

    void executeAfterNotification() {
        executorMock.proceed();
        System.out.println("execute after notification");
    }

    static class Executor {
        public void proceed() {

        }
    }
}