package test.eventbus;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class LatestValueEventBusTest {
    @Test
    public void produce_without_consumers_should_run() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        bus.produce(new Event(1));
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        Subscriber<Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        Event event = new Event(1);
        bus.produce(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        Subscriber<Event> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<Event> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        Event event = new Event(1);
        bus.produce(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        Subscriber<Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        Event event1 = new Event(1);
        Event event2 = new Event(2);
        bus.produce(event1);
        bus.produce(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_then_produce_then_produce_obsolete_should_call_consumer_once() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        Subscriber<Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        Event event1 = new Event(2);
        Event event2 = new Event(1);
        bus.produce(event1);
        bus.produce(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
    }

    @Test
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        LatestValueEventBus<Event> bus = new LatestValueEventBus<Event>();
        Subscriber<Event> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<Event> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        bus.unsubscribe(spy1::consume);
        Event event = new Event(1);
        bus.produce(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    private static class Subscriber<E> {
        public void consume(E event) {

        }
    }

    private static class Event implements LatestValueEventBus.TimestampedEvent {
        private int timestamp;
        public Event(int timestamp) {
            this.timestamp = timestamp;
        }
        public int getTimestamp() {
            return this.timestamp;
        }
        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }
    }

}