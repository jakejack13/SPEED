package edu.cornell.testoutputstream;

import lombok.NonNull;

/**
 * Represents a test results record containing the result and elapsed time.
 */
public record TestResultsRecord(@NonNull String result, @NonNull Integer elapsedTime) {

    /**
     * Returns a string representation of the test results record.
     *
     * @return A string in the format "RESULT:{result};TIME_TAKEN:{elapsedTime}".
     */
    @Override
    public String toString() {
        return "RESULT:" + result + ";TIME_TAKEN:" + elapsedTime;
    }
}
