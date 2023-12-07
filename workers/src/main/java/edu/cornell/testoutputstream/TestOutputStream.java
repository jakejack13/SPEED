package edu.cornell.testoutputstream;

/**
 * An interface allowing the test runner to log its results with clients
 */
public interface TestOutputStream extends AutoCloseable {

    /**
     * An enum representing the possible results of a test
     */
    enum TestResult {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }

    /**
     * Sends a test result to the client
     * @param testClassName the name of the test class
     * @param testMethodName the name of the test method
     * @param result the result of the test
     */
    void sendTestResult(String testClassName, String testMethodName, TestResult result);

}
