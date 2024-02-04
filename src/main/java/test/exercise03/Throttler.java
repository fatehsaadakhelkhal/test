package test.exercise03;

public interface Throttler {
    ThrottleResult shouldProceed();
    void notifyWhenCanProceed(Runnable runnable);
    enum ThrottleResult {
        PROCEED,
        DO_NOT_PROCEED
    }
}