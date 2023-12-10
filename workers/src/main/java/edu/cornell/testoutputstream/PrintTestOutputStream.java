package edu.cornell.testoutputstream;

import lombok.NonNull;

/**
 * A class allowing the test runner to log its results with clients through a given output stream
 */
class PrintTestOutputStream implements TestOutputStream {

    @Override
    public void sendTestResult(@NonNull String testName, @NonNull TestResult result) {
        System.out.println(testName + ":" + result);
    }

    @Override
    public void done() {
        // No-op
    }

    @Override
    public void close() {
        // No-op
    }
}
