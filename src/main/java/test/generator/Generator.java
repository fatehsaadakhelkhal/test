package test.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * going from the principle that a generator is a relatively heavy and kind of costly resource,
 * it's built as an immutable class, and a builder is created for its instances to be created.
 * no need for defensive copy in the constructor as it is only accessible from the builder class and inside the
 * Generator class itself
 * keeping the Set is redundant with the array, may be removed later
 */
public final class Generator implements ProbabilisticRandomGen {
    private static final Logger logger = LoggerFactory.getLogger(Generator.class);
    public static final float EPSILON = 0.00001f;
    private Generator(Set<NumAndProbability> numsWithProbabilities, float precision, int[] distribution) {
        logger.info("created generator with {} values and {} precision", numsWithProbabilities.size(), precision);
        this.numsWithProbabilities = new HashSet<>(numsWithProbabilities);
        this.precision = precision;
        this.distribution = distribution;
    }
    private final Set<NumAndProbability> numsWithProbabilities;
    private final float precision;
    private final int[] distribution;

    @Override
    public int nextFromSample() {
        Random random = new Random();
        int generatedValue = distribution[random.nextInt(distribution.length)];
        logger.info("generated value {}", generatedValue);
        return generatedValue;
    }

    public static class Builder {
        private Set<NumAndProbability> numAndProbabilities = new HashSet<>();
        private float precision = 0.01f;

        public Builder addNumAndProbabilities(NumAndProbability nap) {
            this.numAndProbabilities.add(nap);
            return this;
        }

        public Builder precision(float precision) {
            this.precision = precision;
            return this;
        }

        public Generator build() {
            double sum = this.numAndProbabilities.stream().mapToDouble(NumAndProbability::getProbabilityOfSample).sum();
            if(Math.abs(sum - 1) > EPSILON) {
                throw new IllegalStateException("Sum of probabilities should be 1 when building");
            }
            if(Math.abs(precision - EPSILON) < EPSILON) {
                throw new IllegalStateException("precision can't be below precision");
            }
            int[] distribution = new int[Float.valueOf(1 / precision).intValue()];
            int i = 0;
            for(NumAndProbability nap : this.numAndProbabilities) {
                for (int j = 0; j < nap.getProbabilityOfSample() / precision && i < distribution.length; j++, i++) {
                    distribution[i] = nap.getNumber();
                }
            }
            return new Generator(this.numAndProbabilities, this.precision, distribution);
        }
    }
}
