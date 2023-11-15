package test.eventbus;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.ConcurrentTestRunner;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.ThreadUtils;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import com.google.code.tempusfugit.temporal.Duration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;

/*
    shouldn't work but works... I'll be back to it later
 */
@RunWith(ConcurrentTestRunner.class)
public class SingleThreadedEventBusAdvancedTest {
    @Rule public ConcurrentRule rule = new ConcurrentRule();
    @Rule public RepeatingRule repeatedly = new RepeatingRule();

    private static final SingleThreadedEventBus<Object> bus = new SingleThreadedEventBus<>();
    private static Subscriber<Object> subscriber = Mockito.spy(new Subscriber<>());

    @BeforeClass
    public static void subscribe() {
        bus.subscribe(subscriber::consume);
    }

    AtomicInteger count = new AtomicInteger(0);
    @Test
    @Concurrent(count = 100)
    public void producer() {
        Event event = new Event(count.incrementAndGet());
        bus.produce(event);
    }

    @After
    public void after() {
        ThreadUtils.sleep(Duration.millis(1000));
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(subscriber, Mockito.times(100)).consume(captor.capture());
        Assert.assertEquals(100, captor.getAllValues().size());
    }

    private static class Subscriber<E> {
        public void consume(E event) {

        }
    }

    private static record Event(int value) {}
}