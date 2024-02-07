package test.exercise03;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeWindowThrottler implements Throttler {
    private final int eventsNumber;
    private final long timeWindow;
    private final Queue<Long> timeQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public TimeWindowThrottler(int eventsNumber, long timeWindow) {
        this.eventsNumber = eventsNumber;
        this.timeWindow = timeWindow;
    }

    @Override
    public ThrottleResult shouldProceed() {
        long epochSecond = Instant.now().getEpochSecond();
        long timeLimit = epochSecond - timeWindow;
        timeQueue.removeIf(time -> time < timeLimit);
        timeQueue.offer(epochSecond);
        if ((long) timeQueue.size() >= eventsNumber) {
            return ThrottleResult.DO_NOT_PROCEED;
        } else {
            return ThrottleResult.PROCEED;
        }
    }

    @Override
    public void notifyWhenCanProceed(Runnable runnable) {
        long timeLimit = Instant.now().getEpochSecond() - timeWindow;
        timeQueue.removeIf(time -> time < timeLimit);
        if (timeQueue.size() >= eventsNumber) {
            long delay = timeWindow - (Instant.now().getEpochSecond() - timeLimit);
            service.schedule(runnable, delay, TimeUnit.SECONDS);
        } else {
            runnable.run();
        }
    }
}
