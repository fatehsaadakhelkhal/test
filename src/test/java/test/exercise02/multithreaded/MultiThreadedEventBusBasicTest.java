package test.exercise02.multithreaded;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class MultiThreadedEventBusBasicTest {
    @Test
    public void produce_without_consumers_should_run() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<>();
        bus.publishEvent(new Object());
        Assert.assertTrue(true);
    }

    @Test
    @Ignore
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<>();
        Subscriber<Object> spy1 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy1::consume);
        Subscriber<Object> spy2 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy2::consume);
        Object event = new Object();
        bus.publishEvent(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    private static class Subscriber<E> {
        public void consume(E event) {

        }
    }
}