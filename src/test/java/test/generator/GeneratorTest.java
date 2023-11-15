package test.generator;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.stream.IntStream;

public class GeneratorTest {

    @Test
    public void nextFromSample_one_value() {
        Generator generator = new Generator.Builder()
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(1,1.0f))
                .build();
        assertEquals(1, generator.nextFromSample());
    }

    @Test
    public void nextFromSample_one_value_inconsistent_probability() {
        Generator.Builder builder = new Generator.Builder()
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(1,0.8f));
        assertThrows(IllegalStateException.class, () -> builder.build());
    }

    @Test
    public void nextFromSample_one_value_with_precision() {
        Generator generator = new Generator.Builder()
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(1,1.0f))
                .precision(0.001f).build();
        assertEquals(1.0, generator.nextFromSample(), Generator.EPSILON);
    }

    @Test
    public void nextFromSample_one_value_with_precision_less_than_epsilon() {
        Generator.Builder builder = new Generator.Builder()
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(1,1.0f))
                .precision(Generator.EPSILON / 10);
        assertThrows(IllegalStateException.class, () -> builder.build());
    }

    /*
    the assertions in this test are only for the exercise purposes and the test itself might be instable due to
    its statistical definition
     */
    @Test
    public void nextFromSample_several_values() {
        Generator.Builder builder = new Generator.Builder()
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(1, 0.5f))
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(2, 0.3f))
                .addNumAndProbabilities(new ProbabilisticRandomGen.NumAndProbability(3, 0.2f))
                .precision(0.1f);
        Generator generator = builder.build();

        long countOnes = IntStream.generate(() -> generator.nextFromSample()).limit(1000)
                .asLongStream()
                .filter(value -> value == 1)
                .count();
        System.out.println("ones = " + countOnes);
        assertTrue(Math.abs(countOnes - 500) < 50);
        long countTwos = IntStream.generate(() -> generator.nextFromSample()).limit(1000)
                .asLongStream()
                .filter(value -> value == 2)
                .count();
        System.out.println("twos = " + countTwos);
        assertTrue(Math.abs(countTwos - 300) < 50);
        long countThrees = IntStream.generate(() -> generator.nextFromSample()).limit(1000)
                .asLongStream()
                .filter(value -> value == 3)
                .count();
        System.out.println("threes = " + countThrees);
        assertTrue(Math.abs(countThrees - 200) < 50);
    }
}