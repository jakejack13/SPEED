package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;
import edu.cornell.testoutputstream.TestOutputStream;

import java.io.File;

/**
 * Runs the given Environment Context and Tracks Test Results
 */
public interface TestRunner {

    /**
     * Runs the tests as specified in the environment context and outputs the results
     * @param context the environment context containing information about the tests
     * @param outputStream the output stream to send the results to
     * @param classPath the directory to find all of the test classes from
     * @return true if all tests passed, false otherwise
     */
    boolean runTest(TestEnvContext context, TestOutputStream outputStream, File classPath);
}
