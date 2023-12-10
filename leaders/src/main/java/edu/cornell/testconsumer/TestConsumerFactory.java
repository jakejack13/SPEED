package edu.cornell.testconsumer;

import java.util.Map;
import lombok.NonNull;

/**
 * A factory class for creating new TestConsumer objects
 */
public final class TestConsumerFactory {

    private TestConsumerFactory() {
    }

    /**
     * Creates a new TestConsumer object
     * @param testMethods the mapping of test class to number of test methods inside that test class
     * @return a new TestConsumer object
     */
    public static TestConsumer createTestConsumer(@NonNull Map<String, Integer> testMethods) {
        return new PrintTestConsumerImpl(testMethods);
    }
}
