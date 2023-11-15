package test.statistics;

import test.eventbus.EventBus;

import java.util.function.Consumer;

public interface Throttler<E> extends EventBus<E> {
    ThrottleResult shouldProceed();
    void notifyWhenCanProceed(Consumer<E> consumer);
    enum ThrottleResult {
        PROCEED,
        DO_NOT_PROCEED
    }
}