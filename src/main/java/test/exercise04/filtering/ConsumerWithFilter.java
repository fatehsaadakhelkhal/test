package test.exercise04.filtering;

import java.util.function.Consumer;
import java.util.function.Predicate;

public record ConsumerWithFilter<E>(Consumer<E> consumer, Predicate<E> filter) {
}
