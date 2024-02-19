package edu.cornell.testoutputstream;

import lombok.NonNull;

public record TestResultsRecord(@NonNull String result, @NonNull Integer elapsedTime) {
    @Override
    public String toString() {
        return "RESULT:" + result + ";TIME_TAKEN:" + elapsedTime;
    }
}
