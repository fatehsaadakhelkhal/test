package test.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class LatestValueEventBus<E extends LatestValueEventBus.TimestampedEvent> implements EventBus<E>{
    private static final Logger logger = LoggerFactory.getLogger(LatestValueEventBus.class);

    private int lastReceivedTimestamp = -1; // could be a time object, using int for simplicity
    private final Set<Consumer<E>> consumerSet = new HashSet<>();

    @Override
    public void produce(E event) {
        logger.info("producing event {} to consumers...", event.toString());
        if(event.getTimestamp() > lastReceivedTimestamp) {
            lastReceivedTimestamp = event.getTimestamp();
            consumerSet.forEach(consumer -> consumer.accept(event));
        }
    }

    @Override
    public void subscribe(Consumer<E> consumer) {
        logger.info("subscribing consumer {}", consumer);
        consumerSet.add(consumer);
    }

    @Override
    public void unsubscribe(Consumer<E> consumer) {
        logger.info("unsubscribing consumer {}", consumer);
        consumerSet.remove(consumer);
    }

    public static interface TimestampedEvent {
        void setTimestamp(int timestamp);
        int getTimestamp();
    }
}
