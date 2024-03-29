package test.exercise02.latestValue;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class LatestValueEventBusTest {
    @Test
    public void produce_without_consumers_should_run() {
        LatestValueBasicEventBus<Event> bus = new LatestValueBasicEventBus<>();
        bus.publishEvent(new Event(1));
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_then_produce_obsolete_should_call_consumer_once() throws InterruptedException {
        LatestValueBasicEventBus<Event> bus = new LatestValueBasicEventBus<>();
        Subscriber<Event> spy = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Event.class, spy::consume);
        Event event1 = new Event(2);
        Event event2 = new Event(1);
        Event event3 = new Event(3);
        bus.publishEvent(event1);
        bus.publishEvent(event2);
        bus.publishEvent(event3);

        Thread.sleep(500);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event3);
    }

/*    @Test
    public void subscribe_then_produce_should_call_consumer() {
        LatestValueBasicEventBus<test.exercise02.latestValue.Event> bus = new LatestValueBasicEventBus<test.exercise02.latestValue.Event>();
        test.exercise02.latestValue.Subscriber<test.exercise02.latestValue.Event> spy = Mockito.spy(new test.exercise02.latestValue.Subscriber<>());
        bus.addSubscriber(test.exercise02.latestValue.Event.class, spy::consume);
        test.exercise02.latestValue.Event event = new test.exercise02.latestValue.Event(1);
        bus.publishEvent(event);
        Mockito.verify(spy, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        LatestValueBasicEventBus<test.exercise02.latestValue.Event> bus = new LatestValueBasicEventBus<test.exercise02.latestValue.Event>();
        test.exercise02.latestValue.Subscriber<test.exercise02.latestValue.Event> spy1 = Mockito.spy(new test.exercise02.latestValue.Subscriber<>());
        bus.addSubscriber(test.exercise02.latestValue.Event.class, spy1::consume);
        test.exercise02.latestValue.Subscriber<test.exercise02.latestValue.Event> spy2 = Mockito.spy(new test.exercise02.latestValue.Subscriber<>());
        bus.addSubscriber(test.exercise02.latestValue.Event.class, spy2::consume);
        test.exercise02.latestValue.Event event = new test.exercise02.latestValue.Event(1);
        bus.publishEvent(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        LatestValueBasicEventBus<Event> bus = new LatestValueBasicEventBus<Event>();
        Subscriber<Event> spy = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Event.class, spy::consume);
        Event event1 = new Event(1);
        Event event2 = new Event(2);
        bus.publishEvent(event1);
        bus.publishEvent(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }*/
}