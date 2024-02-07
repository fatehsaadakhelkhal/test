package test.exercise01;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

public class ProbabilisticRandomGenImpl implements ProbabilisticRandomGen {
    public static final float EPSILON = 0.00001f;

    RandomGenerator random;

    private final int[] numbers;
    private final double[] cumulatedProbabilities;
    public ProbabilisticRandomGenImpl(List<NumAndProbability> numAndProbabilityList) {
        this(numAndProbabilityList, new Random());
    }

    public ProbabilisticRandomGenImpl(List<NumAndProbability> numAndProbabilityList, RandomGenerator random) {
        this.random = random;
        int size = numAndProbabilityList.size();
        numbers = new int[size];
        cumulatedProbabilities = new double[size];
        if(!numAndProbabilityList.isEmpty()) {
            numbers[0] = numAndProbabilityList.get(0).getNumber();
            cumulatedProbabilities[0] = numAndProbabilityList.get(0).getProbabilityOfSample();
            for (int i = 1; i < size; i++) {
                numbers[i] = numAndProbabilityList.get(i).getNumber();
                cumulatedProbabilities[i] = numAndProbabilityList.get(i).getProbabilityOfSample() + cumulatedProbabilities[i - 1];
            }
            if (Math.abs(cumulatedProbabilities[cumulatedProbabilities.length-1] - 1) > EPSILON) {
                throw new IllegalArgumentException("probabilities don't sum up to 1");
            }
        }
    }

    @Override
    public int nextFromSample() {
        int index = Arrays.binarySearch(cumulatedProbabilities, random.nextDouble());
        if(index < 0) {
            return numbers[-index-1];
        } else {
            return numbers[index+1];
        }
    }
}
