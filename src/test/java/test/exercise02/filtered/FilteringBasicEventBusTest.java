package test.exercise02.filtered;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class FilteringBasicEventBusTest {
    @Test
    public void produce_without_consumers_should_run() {
        FilteringEventBusImpl<Object> bus = new FilteringEventBusImpl<>();
        bus.publishEvent(new Object());
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        FilteringEventBusImpl<Object> bus = new FilteringEventBusImpl<>();
        SubscriberImpl<Object> spy = Mockito.spy(new SubscriberImpl<>());
        bus.addSubscriber(Object.class, spy::consume);
        Object event = new Object();
        bus.publishEvent(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        FilteringEventBusImpl<Object> bus = new FilteringEventBusImpl<>();
        SubscriberImpl<Object> spy1 = Mockito.spy(new SubscriberImpl<>());
        bus.addSubscriber(Object.class, spy1::consume);
        SubscriberImpl<Object> spy2 = Mockito.spy(new SubscriberImpl<>());
        bus.addSubscriber(Object.class, spy2::consume);
        Object event = new Object();
        bus.publishEvent(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        FilteringEventBusImpl<Object> bus = new FilteringEventBusImpl<>();
        SubscriberImpl<Object> spy = Mockito.spy(new SubscriberImpl<>());
        bus.addSubscriber(Object.class, spy::consume);
        Object event1 = new Object();
        Object event2 = new Object();
        bus.publishEvent(event1);
        bus.publishEvent(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_filtered_then_produce_should_call_consumer_once() {
        FilteringEventBusImpl<Event> bus = new FilteringEventBusImpl<>();
        SubscriberImpl<Event> spy = Mockito.spy(new SubscriberImpl<>());
        bus.addSubscriberForFilteredEvents(Event.class, spy::consume, event -> event.value % 2 == 0);
        Event event1 = new Event(1);
        bus.publishEvent(event1);
        Event event2 = new Event(2);
        bus.publishEvent(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    private static class SubscriberImpl<E> {
        public void consume(E event) {

        }
    }

    private record Event(int value) {}
}