package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;
import edu.cornell.testoutputstream.TestOutputStream;

/**
 * Runs the given Environment Context and Tracks Test Results
 */
public interface TestRunner {

    /**
     * Runs the tests as specified in the environment context and outputs the results
     * @param context the environment context containing information about the tests
     * @param outputStream the output stream to send the results to
     * @return true if all tests passed, false otherwise
     */
    boolean runTest(TestEnvContext<String> context, TestOutputStream outputStream);
}
