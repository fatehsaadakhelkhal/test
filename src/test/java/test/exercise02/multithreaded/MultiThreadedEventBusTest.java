package test.exercise02.multithreaded;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;

public class MultiThreadedEventBusTest {
    @Test
    public void subscribe_twice_then_then_produce_ten_times_should_call_consumer_ten_times() throws InterruptedException {
        MultiThreadedEventBus<Object> bus = new MultiThreadedEventBus<>();
        Subscriber<Object> spy1 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy1::consume);
        Subscriber<Object> spy2 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy2::consume);
        Subscriber<Object> spy3 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy3::consume);
        Subscriber<Object> spy4 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy4::consume);
        Subscriber<Object> spy5 = Mockito.spy(new Subscriber<>());
        bus.addSubscriber(Object.class, spy5::consume);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for(int i = 0; i < 100; i++) {
            executorService.execute(() -> {
                Object event = new Object();
                bus.publishEvent(event);
            });
        }
        executorService.awaitTermination(12, TimeUnit.SECONDS);
        Mockito.verify(spy1, Mockito.times(100)).consume(any(Object.class));
        Mockito.verify(spy2, Mockito.times(100)).consume(any(Object.class));
        Mockito.verify(spy3, Mockito.times(100)).consume(any(Object.class));
        Mockito.verify(spy4, Mockito.times(100)).consume(any(Object.class));
        Mockito.verify(spy5, Mockito.times(100)).consume(any(Object.class));
    }

    private static class Subscriber<E> {
        public void consume(E event) {
            System.out.println(event.toString());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}