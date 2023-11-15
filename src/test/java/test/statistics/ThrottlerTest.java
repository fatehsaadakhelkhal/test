package test.statistics;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ThrottlerTest {
    @Test
    public void produce_without_consumers_should_run() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(0);
        bus.produce(new ExampleThrottler.Event(1));
        Assert.assertTrue(true);
    }

    @Test
    public void subscribe_then_produce_should_call_consumer() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(0);
        Subscriber<ExampleThrottler.Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        ExampleThrottler.Event event = new ExampleThrottler.Event(1);
        bus.produce(event);
        bus.notifyWhenCanProceed(spy::consume);
        Mockito.verify(spy, Mockito.times(2)).consume(event);
    }

    @Test
    public void subscribe_then_produce_below_threshold_should_not_call_consumer() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(2);
        Subscriber<ExampleThrottler.Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        ExampleThrottler.Event event = new ExampleThrottler.Event(1);
        bus.produce(event);
        bus.notifyWhenCanProceed(spy::consume);
        Mockito.verify(spy, Mockito.times(0)).consume(event);
    }

    @Test
    public void subscribe_twice_then_produce_should_call_consumer_twice() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(0);
        Subscriber<ExampleThrottler.Event> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<ExampleThrottler.Event> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        ExampleThrottler.Event event = new ExampleThrottler.Event(1);
        bus.produce(event);
        Mockito.verify(spy1, Mockito.times(1)).consume(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    @Test
    public void subscribe_then_produce_twice_should_call_consumer_twice() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(0);
        Subscriber<ExampleThrottler.Event> spy = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy::consume);
        ExampleThrottler.Event event1 = new ExampleThrottler.Event(1);
        ExampleThrottler.Event event2 = new ExampleThrottler.Event(2);
        bus.produce(event1);
        bus.produce(event2);
        Mockito.verify(spy, Mockito.times(1)).consume(event1);
        Mockito.verify(spy, Mockito.times(1)).consume(event2);
    }

    @Test
    public void subscribe_twice_then_unsubscribe_then_produce_should_call_consumer_once() {
        ExampleThrottler<ExampleThrottler.Event> bus = new ExampleThrottler<ExampleThrottler.Event>(0);
        Subscriber<ExampleThrottler.Event> spy1 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy1::consume);
        Subscriber<ExampleThrottler.Event> spy2 = Mockito.spy(new Subscriber<>());
        bus.subscribe(spy2::consume);
        bus.unsubscribe(spy1::consume);
        ExampleThrottler.Event event = new ExampleThrottler.Event(1);
        bus.produce(event);
        Mockito.verify(spy2, Mockito.times(1)).consume(event);
    }

    private static class Subscriber<E> {
        public void consume(E event) {

        }
    }
}