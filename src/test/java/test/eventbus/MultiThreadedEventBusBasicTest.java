package test.eventbus;

import org.mockito.Mockito;
import org.junit.Test;
import org.junit.Assert;

public class MultiThreadedEventBusBasicTest {
    @Test
    public void produce_without_consumers_should_run() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<Object>();
        bus.produce(new Object());
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<Object>();
        Subscriber<Object> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<Object>();
        Subscriber<Object> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<Object> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<Object>();
        Subscriber<Object> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        Object event1 = new Object();
        Object event2 = new Object();
        bus.produce(event1);
        bus.produce(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<Object>();
        Subscriber<Object> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<Object> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        bus.unsubscribe(spy1::consume);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    private static class Subscriber<E> {
        public void consume(E event) {

        }
    }
}