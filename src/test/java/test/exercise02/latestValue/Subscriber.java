package test.exercise02.latestValue;

class Subscriber<E> {
    public void consume(E event) {
        System.out.println(event);
    }
}
