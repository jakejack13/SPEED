package edu.cornell.testoutputstream;

import lombok.NonNull;

/**
 * Represents a test results record containing the result and elapsed time.
 * @param result the test result
 * @param elapsedTime the execution time of the method
 */
public record TestResultsRecord(@NonNull String result, int elapsedTime) {

    /**
     * Returns a string representation of the test results record.
     *
     * @return A string in the format "RESULT:{result};TIME_TAKEN:{elapsedTime}".
     */
    @Override
    public @NonNull String toString() {
        return "RESULT:" + result + ";TIME_TAKEN:" + elapsedTime;
    }
}
