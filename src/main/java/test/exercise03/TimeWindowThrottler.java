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
        long windowEvents = timeQueue.stream().filter(time -> time > timeLimit).count();
        timeQueue.offer(epochSecond);
        if (windowEvents > eventsNumber) {
            return ThrottleResult.DO_NOT_PROCEED;
        } else {
            return ThrottleResult.PROCEED;
        }
    }

    @Override
    public void notifyWhenCanProceed(Runnable runnable) {
        long timeLimit = Instant.now().getEpochSecond() - timeWindow;
        timeQueue.stream()
                .dropWhile(time -> time < timeLimit)
                .findFirst()
                .ifPresent(t -> {
                    long delay = timeWindow - (Instant.now().getEpochSecond() - t);
                    service.schedule(() -> executeSchedule(t + timeWindow, runnable), delay, TimeUnit.SECONDS);
                });
    }

    public void executeSchedule(Long timeLimit, Runnable runnable) {
        Long time;
        do {
            time = timeQueue.poll();
        } while (time != null && time < timeLimit);
        runnable.run();
    }
}
