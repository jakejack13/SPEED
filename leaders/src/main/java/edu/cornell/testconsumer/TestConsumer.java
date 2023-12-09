package edu.cornell.testconsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;

/**
 * A class representing an object that will consume and process test results
 */
public abstract class TestConsumer {

    /**
     * An enum representing the possible results of a test
     */
    enum TestResult {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    /**
     * A record representing the result of executing a test method
     * @param testClass the name of the test class
     * @param testMethod the name of the test method inside the test class
     * @param result the result of the test
     */
    public record TestResultTuple(@NonNull String testClass, @NonNull String testMethod,
                                  @NonNull TestResult result) { }

    /**
     * A mapping from test class name to number of test methods in the test class
     */
    private final @NonNull Map<String, Integer> testClassMethods;

    /**
     * A mapping from test class name to number of test methods tested so far the test class
     */
    private final @NonNull Map<String, Integer> methodsTested;

    /**
     * Creates a new TestConsumer with the specified number of test class methods
     * @param testClassMethods a mapping from test class name to number of test methods
     *                         in the test class
     */
    protected TestConsumer(@NonNull Map<String, Integer> testClassMethods) {
        this.testClassMethods = testClassMethods;
        methodsTested = new HashMap<>();
        for (String testClass : testClassMethods.keySet()) {
            methodsTested.put(testClass, 0);
        }
    }

    /**
     * Deserializes and logs the given test result
     * @param serializedKey the key in the Kafka message
     * @param serializedValue the value in the Kafka message
     */
    public void processTestOutput(String serializedKey, String serializedValue) {
        String[] splitKey = serializedKey.split(":");
        String testClass = splitKey[0];
        String testMethod = splitKey[1];
        TestResult result = TestResult.valueOf(serializedValue);
        methodsTested.put(testClass, methodsTested.get(testClass)+1);

        displayTestResult(new TestResultTuple(testClass, testMethod, result));
    }

    /**
     * Displays the test result via this class' specific route
     * @param result the result to display
     */
    protected abstract void displayTestResult(TestResultTuple result);

    /**
     * Returns whether all test methods have been executed
     * @return true if all test methods have been executed, false otherwise
     */
    public boolean isDone() {
        return testClassMethods.keySet().stream().allMatch(
                s -> Objects.equals(testClassMethods.get(s),
                        methodsTested.get(s)));
    }
}
