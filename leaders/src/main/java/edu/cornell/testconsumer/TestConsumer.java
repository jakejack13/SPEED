package edu.cornell.testconsumer;

import lombok.NonNull;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representing an object that will consume and process test results.
 */
public abstract class TestConsumer {

    /**
     * Creates a new TestConsumer for the specified workers.
     * @param workerIds the ids of the workers to log test results with
     * @return a new TestConsumer object
     */
    public static @NonNull TestConsumer createTestConsumer(Set<String> workerIds) {
        return new PrintTestConsumer(workerIds);
    }

    private static final @NonNull String DONE = "DONE";

    /**
     * An enum representing the possible results of a test.
     */
    protected enum TestResult {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    /**
     * The set of all worker ids.
     */
    private final @NonNull Set<String> workerIds;

    /**
     * The set of all workers who have returned results so far.
     */
    private final @NonNull Set<String> workersSoFar;

    /**
     * Creates a new TestConsumer with the specified number of test class methods.
     * @param workerIds the set of all worker ids
     */
    protected TestConsumer(@NonNull Set<String> workerIds) {
        this.workerIds = workerIds;
        workersSoFar = new HashSet<>();

    }

    /**
     * Deserializes and logs the given test result.
     * @param serializedKey the key in the Kafka message
     * @param serializedValue the value in the Kafka message
     */
    public void processTestOutput(String serializedKey, String serializedValue) {
        if (serializedValue.equals(DONE)) {
            workersSoFar.add(serializedKey);
            return;
        }

        TestResult result = TestResult.valueOf(serializedValue);

        displayTestResult(serializedKey, result);
    }

    /**
     * Displays the test result via this class' specific route.
     * @param testName the name of the test to display
     * @param result the result to display
     */
    protected abstract void displayTestResult(String testName, TestResult result);

    /**
     * Returns whether all test methods have been executed.
     * @return true if all test methods have been executed, false otherwise
     */
    public boolean isDone() {
        return workersSoFar.equals(workerIds);
    }
}
