package test.exercise02.latestValue;

class Event implements LatestValueBasicEventBus.TimestampedEvent {
    private long timestamp;

    public Event(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}
