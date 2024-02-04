package test.exercise01;

import org.junit.Test;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ProbabilisticRandomGenImplTest {
    @Test
    public void nextFromSample_one_value() {
        List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities = List.of(new ProbabilisticRandomGen.NumAndProbability(1, 1.0f));
        ProbabilisticRandomGenImpl generator = new ProbabilisticRandomGenImpl(numAndProbabilities);
        assertEquals(1, generator.nextFromSample());
    }

    @Test
    public void nextFromSample_one_value_inconsistent_probability() {
        List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities = List.of(new ProbabilisticRandomGen.NumAndProbability(1, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new ProbabilisticRandomGenImpl(numAndProbabilities));
    }
    @Test
    public void nextFromSample_several_values() {
        List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities = List.of(new ProbabilisticRandomGen.NumAndProbability(1, 0.5f),
                new ProbabilisticRandomGen.NumAndProbability(2, 0.3f),
                new ProbabilisticRandomGen.NumAndProbability(3, 0.2f));
        ProbabilisticRandomGenImpl generator = new ProbabilisticRandomGenImpl(numAndProbabilities);
        long countOnes = IntStream.generate(generator::nextFromSample).limit(1000)
                .asLongStream()
                .filter(value -> value == 1)
                .count();
        System.out.println("ones = " + countOnes);
        assertTrue(Math.abs(countOnes - 500) < 50);
        long countTwos = IntStream.generate(generator::nextFromSample).limit(1000)
                .asLongStream()
                .filter(value -> value == 2)
                .count();
        System.out.println("twos = " + countTwos);
        assertTrue(Math.abs(countTwos - 300) < 50);
        long countThrees = IntStream.generate(generator::nextFromSample).limit(1000)
                .asLongStream()
                .filter(value -> value == 3)
                .count();
        System.out.println("threes = " + countThrees);
        assertTrue(Math.abs(countThrees - 200) < 50);
    }

    @Test
    public void nextFromSample_several_values_custom_random_generator() {
        List<ProbabilisticRandomGen.NumAndProbability> numAndProbabilities = List.of(new ProbabilisticRandomGen.NumAndProbability(1, 0.5f),
                new ProbabilisticRandomGen.NumAndProbability(2, 0.3f),
                new ProbabilisticRandomGen.NumAndProbability(3, 0.2f));
        ProbabilisticRandomGenImpl generator = new ProbabilisticRandomGenImpl(numAndProbabilities, new MyRandomGenerator());
        assertEquals(2, generator.nextFromSample());
        assertEquals(2, generator.nextFromSample());
        assertEquals(3, generator.nextFromSample());
    }

    private static class MyRandomGenerator implements RandomGenerator {
        private int i = 0;
        private final double[] values = {0.5, 0.6, 0.9};
        @Override
        public long nextLong() {
            return 0;
        }

        @Override
        public double nextDouble() {
            return values[i++%3];
        }
    }
}