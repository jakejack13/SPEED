package edu.cornell.testresultproducer;

import lombok.NonNull;

/**
 * A class allowing the test runner to log its results with clients through a given output stream
 */
public class PrintTestOutputStream implements TestOutputStream {

    @Override
    public void sendTestResult(@NonNull String testClassName, @NonNull String testMethodName,
            @NonNull TestResult result) {
        System.out.println(testClassName + ":" + testMethodName + "; " + result);
    }

    @Override
    public void close() {
        // No-op
    }
}