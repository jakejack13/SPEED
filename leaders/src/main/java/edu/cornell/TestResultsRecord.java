package edu.cornell;

import lombok.NonNull;

/**
 * Represents a test results record with the result and elapsed time. 
 * This is used to store results from Kafka.
 * @param result the result of the test
 * @param elapsedTime the execution time of this test
 */
public record TestResultsRecord(@NonNull String result, int elapsedTime) {
}
