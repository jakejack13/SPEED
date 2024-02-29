package edu.cornell.testoutputstream;

import lombok.NonNull;
import java.io.PrintStream;

/**
 * A class allowing the test runner to log its results with clients through a given output stream.
 */
class PrintTestOutputStream implements TestOutputStream {

    /**
     * The output stream.
     */
    private final PrintStream out = System.out;

    @Override
    public void sendTestResult(@NonNull String testName, @NonNull TestResult result, 
            int elapsedTime) {
        out.println(testName + " : " + result + " : " + elapsedTime);
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
