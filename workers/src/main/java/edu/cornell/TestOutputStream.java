package edu.cornell;

import lombok.extern.slf4j.Slf4j;

/**
 * A class allowing the test runner to log its results with clients
 */
@Slf4j
public class TestOutputStream {

    /**
     * An enum representing the possible results of a test
     */
    public enum TestResult {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    /**
     * Creates a new TestOutputStream
     */
    public TestOutputStream() {

    }

    /**
     * Sends a test result to the client
     * @param testClassName - the name of the test class
     * @param testMethodName - the name of the test method
     * @param result - the result of the test
     */
    public void sendTestResult(String testClassName, String testMethodName, TestResult result) {

    }
}
