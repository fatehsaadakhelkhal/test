package test.exercise02.singlethreaded;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class SingleThreadedEventBusTest {
    @Test
    public void produce_without_consumers_should_run() {
        SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
        bus.publishEvent(new Object());
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
        Subscriber<Object> spy = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy::consume);
        Object event = new Object();
        bus.publishEvent(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
        Subscriber<Object> spy1 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy1::consume);
        Subscriber<Object> spy2 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy2::consume);
        Object event = new Object();
        bus.publishEvent(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
        Subscriber<Object> spy = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy::consume);
        Object event1 = new Object();
        Object event2 = new Object();
        bus.publishEvent(event1);
        bus.publishEvent(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
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