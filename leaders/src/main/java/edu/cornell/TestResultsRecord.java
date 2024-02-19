package edu.cornell;

import lombok.NonNull;

/**
 * Represents a test results record with the result and elapsed time. This is used to store results from Kafka.
 */
public record TestResultsRecord(@NonNull String result, @NonNull Integer elapsedTime) {
}
