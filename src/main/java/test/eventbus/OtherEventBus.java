package test.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class OtherEventBus<E> {
    private static final Logger logger = LoggerFactory.getLogger(OtherEventBus.class);

    private final Set<Subscriber<E>> subscriberSet = new HashSet<>();

    public void produce(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        subscriberSet.forEach(subscriber -> subscriber.consume(event));
    }

    public void subscribe(Subscriber<E> subscriber) {
        logger.info("subscribing subscriber {}", subscriber);
        subscriberSet.add(subscriber);
    }

    public void unsubscribe(Subscriber<E> subscriber) {
        logger.info("unsubscribing subscriber {}", subscriber);
        subscriberSet.remove(subscriber);
    }

    public interface Subscriber<E> {
        void consume(E event);
    }
}
