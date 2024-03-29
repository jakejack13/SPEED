package edu.cornell.testoutputstream;

import lombok.NonNull;
import java.io.Closeable;

/**
 * An interface allowing the test runner to log its results with clients.
 */
public interface TestOutputStream extends Closeable {

    /**
     * Creates and returns a new TestOutputStream.
     * @param kafkaAddress the address of the Kafka message bus
     * @return a new TestOutputStream
     */
    static @NonNull TestOutputStream createTestOutputStream(@NonNull String kafkaAddress) {
        return new KafkaTestOutputStream();
//        return new PrintTestOutputStream();
    }

    /**
     * An enum representing the possible results of a test.
     */
    enum TestResult {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    /**
     * Sends a test result to the client.
     * @param testName the name of the test
     * @param result the result of the test
     * @param elapsedTime the amount of time in nano-seconds taken to run the test
     */
    void sendTestResult(String testName, TestResult result, int elapsedTime);

    /**
     * Tells the listener that the tests have all finished executing.
     */
    void done();

}
