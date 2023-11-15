package test.eventbus;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class OtherEventBusTest {
    @Test
    public void produce_without_consumers_should_run() {
        OtherEventBus<Object> bus = new OtherEventBus<Object>();
        bus.produce(new Object());
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        OtherEventBus<Object> bus = new OtherEventBus<Object>();
        SubscriberImpl<Object> spy = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        OtherEventBus<Object> bus = new OtherEventBus<Object>();
        SubscriberImpl<Object> spy1 = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy1);
        SubscriberImpl<Object> spy2 = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy2);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        OtherEventBus<Object> bus = new OtherEventBus<Object>();
        SubscriberImpl<Object> spy = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy);
        Object event1 = new Object();
        Object event2 = new Object();
        bus.produce(event1);
        bus.produce(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        OtherEventBus<Object> bus = new OtherEventBus<Object>();
        SubscriberImpl<Object> spy1 = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy1);
        SubscriberImpl<Object> spy2 = Mockito.spy(new SubscriberImpl<>());
        bus.subscribe(spy2);
        bus.unsubscribe(spy1);
        Object event = new Object();
        bus.produce(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    private static class SubscriberImpl<E> implements OtherEventBus.Subscriber<E> {
        @Override
        public void consume(E event) {

        }
    }
}