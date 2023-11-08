package edu.cornell.testoutput;

import lombok.extern.slf4j.Slf4j;

/**
 * A class allowing the test runner to log its results with clients through a given output stream
 */
@Slf4j
public class PrintTestOutputStream implements TestOutputStream {

    @Override
    public void sendTestResult(String testClassName, String testMethodName, TestResult result) {
        System.out.println(testClassName + ":" + testMethodName + "; " + result.toString());
    }

    @Override
    public void close() {
        // No-op
    }
}
